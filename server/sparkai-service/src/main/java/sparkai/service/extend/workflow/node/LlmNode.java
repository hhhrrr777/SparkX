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
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.system.ModelsEntity;
import sparkai.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.helper.*;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.mapper.system.ModelsMapper;
import sparkai.service.service.interfaces.application.IAiService;
import sparkai.service.validate.application.ApplicationChatValidate;
import sparkai.service.vo.dataset.DatasetSimpleVo;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.LlmAnswerVo;
import sparkai.service.vo.workflow.NextAnswerNodeVo;
import sparkai.service.vo.workflow.NodeRuntimeVo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class LlmNode implements IWorkflowNode {

    @Setter
    public SseEmitter emitter;

    @Setter
    public CountDownLatch latch;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    ApplicationHelper applicationHelper;

    @Autowired
    ModelsMapper modelsMapper;

    @Autowired
    AssistantBuildHelper assistantBuildHelper;

    @Autowired
    StreamChatModelBuildHelper streamChatModelBuildHelper;

    @Autowired
    ChatModelBuildHelper chatModelBuildHelper;

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    private String question;

    @Override
    public List<EdgeVo> handle(NodeRuntimeVo runtimeVo) {

        JSONObject nodeObject = runtimeVo.getNodeInfo().getData();
        // 本节点输入的参数
        String inputSourceId = "";

        // 获取上一个节点的信息
        ApplicationWorkflowRuntimeContextEntity context =
                applicationHelper.getRuntimeContext(runtimeVo.getRuntimeId(), runtimeVo.getSourceId(), inputSourceId);
        if (context == null) {
            return null;
        }

        JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());
        question = preOutput.getStr("sys.question");

        // 记录运行时数据
        ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
        contextEntity.setStep(context.getStep() + 1);
        contextEntity.setNodeType(NodeTypeEnum.LLM.getCode());
        contextEntity.setRuntimeId(runtimeVo.getRuntimeId());
        contextEntity.setModelData(nodeObject.toString());
        contextEntity.setOutputData(preOutput.toString());
        contextEntity.setCell(runtimeVo.getNodeInfo().getId());
        contextEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        // 查看下一个节点是否是回复节点，且回复的内容是本节点的输出
        NextAnswerNodeVo nextAnswerNodeInfo = applicationHelper.checkNextIsAnswerNode(runtimeVo);
        boolean needSend = (nextAnswerNodeInfo.isNodeIsAnswer() && nextAnswerNodeInfo.getAnswerType() == 1);

        try {

            // 本节点的输出信息
            TokenStream tokenStream = llmAnswer(contextEntity, runtimeVo.getUserId(), runtimeVo.getSessionId());
            if (tokenStream == null) {
                throw new BusinessException("LLM节点出现系统异常");
            }

            AtomicBoolean runComplete = new AtomicBoolean(false);
            sseEmitterHelper.asyncSend2Client(tokenStream, emitter, contextEntity.getRuntimeId(),
                    contextEntity.getCell(), needSend, (llmRes) -> {

                        JSONObject llmResData = JSONUtil.parseObj(llmRes);
                        String answer = llmResData.getStr("content");

                        // 记录llm输出
                        JSONObject preContextOutput = JSONUtil.parseObj(contextEntity.getOutputData());
                        preContextOutput.set("sys.content", answer);
                        contextEntity.setOutputData(preContextOutput.toString());

                        // token使用情况
                        JSONObject modelData = JSONUtil.createObj();
                        modelData.set("inputTokenCount", llmResData.getStr("inputTokenCount"));
                        modelData.set("outputTokenCount", llmResData.getStr("outputTokenCount"));
                        modelData.set("totalTokenCount", llmResData.getStr("totalTokenCount"));
                        contextEntity.setModelData(modelData.toString());

                        applicationWorkflowRuntimeContextMapper.updateById(contextEntity);

                        runComplete.set(true);
                    });

            // 阻塞等待异步发送完成
            while (!runComplete.get()) {}

        } catch (Exception e) {
            log.error("知识库检索节点错误, {}", e.getMessage());
            throw new BusinessException("知识库检索节点错误");
        }

        latch.countDown();

        // 获取下一个节点
        return runtimeVo.getEdges().get(runtimeVo.getNodeInfo().getId());
    }

    /**
     * 大模型流式回答
     * @param context ApplicationWorkflowRuntimeContextEntity
     * @param userId String
     * @param sessionId String
     * @return String
     */
    private TokenStream llmAnswer(ApplicationWorkflowRuntimeContextEntity context, String userId, String sessionId) {

        try {

            LlmAnswerVo llmAnswerData = buildBaseData(context, userId, sessionId);

            TokenStream tokenStream;
            if (llmAnswerData.getApplication().getPrompt().isBlank()) {
                tokenStream = llmAnswerData.getAssistant().chatInTokenStream(question);
            } else {
                tokenStream = llmAnswerData.getAssistant().chatWithSystem(llmAnswerData.getApplication().getPrompt(), question);
            }

            return tokenStream;
        } catch (Exception e) {
            log.error("回复节点构建llm错误：", e);
            return null;
        }
    }

    /**
     * 构建基础信息
     * @param context ApplicationWorkflowRuntimeContextEntity
     * @param userId String
     * @param sessionId String
     * @return LlmAnswerVo
     */
    private LlmAnswerVo buildBaseData(ApplicationWorkflowRuntimeContextEntity context, String userId, String sessionId) {

        LlmAnswerVo llmAnswerVo = new LlmAnswerVo();

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
        if (inputObject.containsKey("node.datasets") && !inputObject.getStr("node.datasets").isBlank()) {
            List<String> datasetIdsArr = Arrays.stream(inputObject.getStr("node.datasets").split(",")).toList();

            for (String datasetId : datasetIdsArr) {
                DatasetSimpleVo datasetSimpleVo = new DatasetSimpleVo();
                datasetSimpleVo.setDatasetId(datasetId);

                dataListVo.add(datasetSimpleVo);
            }
        }
        validate.setDatasetList(dataListVo);

        // TODO 此处马上重构
        String question = inputObject.getStr("node.question");
        validate.setContent(question);
        validate.setContextId(context.getId());
        validate.setSessionId(sessionId);
        validate.setCell(context.getCell()); // 以次区分不同节点的上下文记录

        applicationInfo.setMemoryNum(modelObject.getInt("memory"));
        applicationInfo.setCompressingQuery(1);
        applicationInfo.setSearchMode("embedding");
        applicationInfo.setTopRank(modelObject.getInt("topRank"));
        applicationInfo.setPrompt(modelObject.getStr("systemMsg"));
        applicationInfo.setSimilarity(BigDecimal.valueOf(modelDataInfo.getDouble("temperature")));
        applicationInfo.setUserId(userId);

        // step 3 构建 IAiService
        IAiService assistant = assistantBuildHelper.build(applicationInfo, validate, streamingChatModel, chatLanguageModel);;

        llmAnswerVo.setApplication(applicationInfo);
        llmAnswerVo.setValidate(validate);
        llmAnswerVo.setStreamingChatModel(streamingChatModel);
        llmAnswerVo.setChatLanguageModel(chatLanguageModel);
        llmAnswerVo.setAssistant(assistant);

        return llmAnswerVo;
    }
}