// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.extend.workflow.node;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.TokenStream;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.enums.NodeTypeEnum;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.entity.system.ModelsEntity;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.helper.*;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.mapper.system.ModelsMapper;
import sparkai.service.service.interfaces.application.IAiService;
import sparkai.service.service.interfaces.dataset.IDatasetSearchService;
import sparkai.service.validate.application.ApplicationChatValidate;
import sparkai.service.vo.dataset.DatasetSimpleVo;
import sparkai.service.vo.dataset.DatasetSearchVo;
import sparkai.service.vo.dataset.SearchVo;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeRuntimeVo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AnswerNode implements IWorkflowNode {

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    @Setter
    public CountDownLatch latch;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Setter
    public SseEmitter emitter;

    @Autowired
    IDatasetSearchService searchService;

    @Autowired
    ModelsMapper modelsMapper;

    @Autowired
    AssistantBuildHelper assistantBuildHelper;

    @Autowired
    StreamChatModelBuildHelper streamChatModelBuildHelper;

    @Autowired
    ChatModelBuildHelper chatModelBuildHelper;

    @Autowired
    ApplicationHelper applicationHelper;

    @Override
    public List<EdgeVo> handle(NodeRuntimeVo runtimeVo) {

        try {

            JSONObject nodeObject = runtimeVo.getNodeInfo().getData();
            Integer answerType = nodeObject.getInt("answerType");
            // 本节点输入的参数
            JSONArray inputArr = nodeObject.getJSONArray("inputData");
            String inputSourceId;
            if (!inputArr.isEmpty()) {
                inputSourceId = inputArr.get(0).toString();
            } else {
                inputSourceId = "";
            }

            // 上个节点的信息
            ApplicationWorkflowRuntimeContextEntity context =
                    applicationHelper.getRuntimeContext(runtimeVo.getRuntimeId(), runtimeVo.getSourceId(), inputSourceId);
            if (context == null) {
                return null;
            }

            // 记录运行时数据
            ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
            contextEntity.setStep(context.getStep() + 1);
            contextEntity.setNodeType(NodeTypeEnum.ANSWER.getCode());
            contextEntity.setRuntimeId(runtimeVo.getRuntimeId());

            JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());
            if (answerType.equals(1)) {

                // 找出回复内容
                String returnAnswerType = inputArr.get(1).toString();
                String answer;
                // 如果上个节点是dataset节点，且输出为检索结果
                if (context.getNodeType().equals(NodeTypeEnum.DATASET.getCode())
                        && returnAnswerType.equals("sys.result")) {

                    // 执行知识库检索并输出
                    String question = preOutput.getStr("node_question");
                    String datasetIds = preOutput.getStr("sys.result");
                    answer = datasetAnswer(question, datasetIds, context);
                    emitter.send(Tool.buildSendData(context.getRuntimeId(), context.getCell(), answer));
                } else if (context.getNodeType().equals(NodeTypeEnum.LLM.getCode())
                        && returnAnswerType.equals("sys.content")) {

                    String llmRes = llmAnswer(context, runtimeVo.getUserId(), runtimeVo.getSessionId());
                    JSONObject llmResData = JSONUtil.parseObj(llmRes);
                    answer = llmResData.getStr("content");

                    // 模型使用情况
                    JSONObject modelData = JSONUtil.createObj();
                    modelData.set("inputTokenCount", llmResData.getStr("inputTokenCount"));
                    modelData.set("outputTokenCount", llmResData.getStr("outputTokenCount"));
                    modelData.set("totalTokenCount", llmResData.getStr("totalTokenCount"));
                    contextEntity.setModelData(modelData.toString());
                } else {

                    answer = preOutput.get(returnAnswerType).toString();
                    emitter.send(Tool.buildSendData(context.getRuntimeId(), context.getCell(), answer));
                }
                // 记录问题分类节点的输出
                preOutput.set("sys.answer", answer);
            } else {
                emitter.send(Tool.buildSendData(context.getRuntimeId(), context.getCell(), nodeObject.getStr("answer")));

                // 记录问题分类节点的输出
                preOutput.set("sys.answer", nodeObject.getStr("answer"));
            }

            contextEntity.setOutputData(preOutput.toString());
            contextEntity.setCell(runtimeVo.getNodeInfo().getId());
            contextEntity.setCreateTime(Tool.nowDateTime());
            applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        } catch (IOException e) {
            sseEmitterHelper.sendErrorSse(emitter, e.getMessage());
        }

        latch.countDown();

        // 获取下一个节点
        return runtimeVo.getEdges().get(runtimeVo.getNodeInfo().getId());
    }

    /**
     * 知识库检索
     * @param question String
     * @param datasetIds String
     * @param context ApplicationWorkflowRuntimeContextEntity
     * @return String
     */
    private String datasetAnswer(String question, String datasetIds, ApplicationWorkflowRuntimeContextEntity context) {

        JSONObject nodeObject = JSONUtil.parseObj(context.getModelData());

        DatasetSearchVo searchDataVo = new DatasetSearchVo();
        searchDataVo.setKeyword(question);
        searchDataVo.setDatasetIds(datasetIds);
        searchDataVo.setSimilarity(nodeObject.getDouble("similarity"));
        searchDataVo.setTopRank(nodeObject.getInt("topRank"));
        searchDataVo.setType("embedding");
        List<SearchVo> searchRes = searchService.search(searchDataVo);

        return searchRes.stream().map(SearchVo::getContent).collect(Collectors.joining());
    }

    /**
     * 大模型回答
     * @param context ApplicationWorkflowRuntimeContextEntity
     * @param userId String
     * @param sessionId String
     * @return String
     */
    private String llmAnswer(ApplicationWorkflowRuntimeContextEntity context, String userId, String sessionId) {

        try {

            JSONObject inputObject = JSONUtil.parseObj(context.getOutputData());
            JSONObject modelObject = JSONUtil.parseObj(context.getModelData());

            JSONObject modelDataInfo = modelObject.getJSONObject("modelInfo");
            // 获取模型信息
            ModelsEntity modelResInfo = modelsMapper.selectById(modelDataInfo.getStr("modelId"));

            // step 1 构建模型流式应答对象
            ApplicationEntity applicationInfo = new ApplicationEntity();
            applicationInfo.setTemperature(modelDataInfo.getDouble("temperature"));
            applicationInfo.setModelName(modelDataInfo.getStr("modelName"));
            StreamingChatLanguageModel streamingChatModel = streamChatModelBuildHelper.build(modelResInfo, applicationInfo);

            // step 2 构建模型普通对象，用于问题优化下使用
            ChatLanguageModel chatLanguageModel = chatModelBuildHelper.build(modelResInfo, applicationInfo);

            ApplicationChatValidate validate = new ApplicationChatValidate();
            // 写入引用的知识库
            List<DatasetSimpleVo> dataListVo = new ArrayList<>();
            if (inputObject.containsKey("sys.result") && !inputObject.getStr("sys.result").isBlank()) {
                List<String> datasetIdsArr = Arrays.stream(inputObject.getStr("sys.result").split(",")).toList();

                for (String datasetId : datasetIdsArr) {
                    DatasetSimpleVo datasetSimpleVo = new DatasetSimpleVo();
                    datasetSimpleVo.setDatasetId(datasetId);

                    dataListVo.add(datasetSimpleVo);
                }
            }
            validate.setDatasetList(dataListVo);

            JSONArray inputArr = modelObject.getJSONArray("inputData");
            String inputNodeData = inputArr.get(1).toString();
            String question = inputObject.get(inputNodeData).toString();

            validate.setContent(modelObject.getStr("userMsg") + question);
            validate.setContextId(context.getId());
            validate.setSessionId(sessionId);
            validate.setCell(context.getCell()); // 以次区分不同节点的上下文记录

            applicationInfo.setMemoryNum(modelObject.getInt("memory"));
            applicationInfo.setCompressingQuery(1);
            applicationInfo.setSearchMode("embedding");
            applicationInfo.setTopRank(3);
            applicationInfo.setPrompt(modelObject.getStr("systemMsg"));
            applicationInfo.setSimilarity(BigDecimal.valueOf(modelDataInfo.getDouble("temperature")));
            applicationInfo.setUserId(userId);

            // step 3 构建 IAiService
            IAiService assistant = assistantBuildHelper.build(applicationInfo, validate, streamingChatModel, chatLanguageModel);

            TokenStream tokenStream;
            if (applicationInfo.getPrompt().isBlank()) {
                tokenStream = assistant.chatInTokenStream(validate.getContent());
            } else {
                tokenStream = assistant.chatWithSystem(applicationInfo.getPrompt(), validate.getContent());
            }

            AtomicReference<String> answer = new AtomicReference<>("");
            AtomicBoolean runComplete = new AtomicBoolean(false);
            sseEmitterHelper.asyncSend2Client(tokenStream, emitter, context.getRuntimeId(), context.getCell(), (content) -> {

                answer.set(content);
                runComplete.set(true);
            });

            // 阻塞等待异步发送完成
            while (!runComplete.get()) {}

            return answer.get();
        } catch (Exception e) {
            log.error("回复节点构建llm错误：", e);
            return null;
        }
    }
}