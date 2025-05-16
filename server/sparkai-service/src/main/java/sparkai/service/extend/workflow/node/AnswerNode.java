package sparkai.service.extend.workflow.node;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import sparkai.service.entity.application.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.entity.system.ModelsEntity;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.helper.AssistantBuildHelper;
import sparkai.service.helper.ChatModelBuildHelper;
import sparkai.service.helper.SseEmitterHelper;
import sparkai.service.helper.StreamChatModelBuildHelper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.mapper.system.ModelsMapper;
import sparkai.service.service.interfaces.application.IAiService;
import sparkai.service.service.interfaces.dataset.IHitTestService;
import sparkai.service.validate.application.ApplicationSaveValidate;
import sparkai.service.vo.dataset.DatasetSimpleVo;
import sparkai.service.vo.dataset.HitTestVo;
import sparkai.service.vo.dataset.SearchVo;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AnswerNode implements IWorkflowNode {

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Setter
    public SseEmitter emitter;

    @Autowired
    IHitTestService searchService;

    @Autowired
    ModelsMapper modelsMapper;

    @Autowired
    AssistantBuildHelper assistantBuildHelper;

    @Autowired
    StreamChatModelBuildHelper streamChatModelBuildHelper;

    @Autowired
    ChatModelBuildHelper chatModelBuildHelper;

    @Override
    public List<EdgeVo> handle(NodeVo nodeInfo, long runtimeId, String sourceId, Map<String, List<EdgeVo>> edges) {

        try {

            JSONObject nodeObject = nodeInfo.getData();
            Integer answerType = nodeObject.getInt("answerType");

            // 上个节点的信息
            ApplicationWorkflowRuntimeContextEntity context = applicationWorkflowRuntimeContextMapper.selectOne(
                    new QueryWrapper<ApplicationWorkflowRuntimeContextEntity>().eq("runtime_id", runtimeId).eq("cell", sourceId));

            // 记录运行时数据
            ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
            contextEntity.setStep(context.getStep() + 1);
            contextEntity.setNodeType(NodeTypeEnum.ANSWER.getCode());
            contextEntity.setRuntimeId(runtimeId);

            JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());
            if (answerType.equals(1)) {

                // 找出回复内容
                JSONArray inputArr = nodeObject.getJSONArray("inputData");
                String returnAnswerType = inputArr.get(1).toString();
                String answer = "";
                // 如果上个节点是dataset节点，且输出为检索结果
                if (context.getNodeType().equals(NodeTypeEnum.DATASET.getCode())
                        && returnAnswerType.equals("sys.result")) {

                    // 执行知识库检索并输出
                    String question = preOutput.getStr("node_question");
                    String datasetIds = preOutput.getStr("sys.result");
                    answer = datasetAnswer(question, datasetIds);
                    emitter.send(answer);
                } else if (context.getNodeType().equals(NodeTypeEnum.LLM.getCode())
                        && returnAnswerType.equals("sys.content")) {

                    String llmRes = llmAnswer(context.getOutputData(), context.getModelData());
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
                    emitter.send(answer);
                }
                // 记录问题分类节点的输出
                preOutput.set("sys.answer", answer);
            } else {
                emitter.send(nodeObject.getStr("answer"));

                // 记录问题分类节点的输出
                preOutput.set("sys.answer", nodeObject.getStr("answer"));
            }

            contextEntity.setOutputData(preOutput.toString());
            contextEntity.setCell(nodeInfo.getId());
            contextEntity.setCreateTime(Tool.nowDateTime());
            applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        } catch (IOException e) {
            sseEmitterHelper.sendErrorSse(emitter, e.getMessage());
        }

        // 获取下一个节点
        return edges.get(nodeInfo.getId());
    }

    /**
     * 知识库检索
     * @param question String
     * @param datasetIds String
     * @return String
     */
    private String datasetAnswer(String question, String datasetIds) {

        HitTestVo searchDataVo = new HitTestVo();
        searchDataVo.setKeyword(question);
        searchDataVo.setDatasetIds(datasetIds);
        searchDataVo.setSimilarity(0.9);
        searchDataVo.setTopRank(3);
        searchDataVo.setType("embedding");
        List<SearchVo> searchRes = searchService.search(searchDataVo);

        return searchRes.stream().map(SearchVo::getContent).collect(Collectors.joining());
    }

    /**
     * 大模型回答
     * @param inputData String
     * @param modelInfo String
     * @return String
     */
    private String llmAnswer(String inputData, String modelInfo) {

        try {

            JSONObject inputObject = JSONUtil.parseObj(inputData);
            JSONObject modelObject = JSONUtil.parseObj(modelInfo);

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

            ApplicationSaveValidate validate = new ApplicationSaveValidate();
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

            validate.setMemoryNum(modelObject.getInt("memory"));
            validate.setCompressingQuery(1);
            validate.setSearchMode("embedding");
            validate.setTopRank(3);
            validate.setPrompt(modelObject.getStr("systemMsg"));
            validate.setSimilarity(modelDataInfo.getDouble("temperature"));

            JSONArray inputArr = modelObject.getJSONArray("inputData");
            String inputNodeData = inputArr.get(1).toString();
            String question = inputObject.get(inputNodeData).toString();

            validate.setContent(modelObject.getStr("userMsg") + question);

            // step 3 构建 IAiService
            IAiService assistant = assistantBuildHelper.build(validate, streamingChatModel, chatLanguageModel);

            TokenStream tokenStream;
            if (validate.getPrompt().isBlank()) {
                tokenStream = assistant.chatInTokenStream(validate.getContent());
            } else {
                tokenStream = assistant.chatWithSystem(validate.getPrompt(), validate.getContent());
            }

            AtomicReference<String> answer = new AtomicReference<>("");
            CountDownLatch latch = new CountDownLatch(1);
            sseEmitterHelper.asyncSend2Client(tokenStream, emitter, (response) -> {

                answer.set(response);
                latch.countDown();
            });

            latch.await();

            return answer.get();
        } catch (Exception e) {
            log.error("回复节点构建llm错误：", e);
            return null;
        }
    }
}