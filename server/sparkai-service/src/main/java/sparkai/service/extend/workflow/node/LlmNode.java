package sparkai.service.extend.workflow.node;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.application.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.entity.system.ModelsEntity;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.helper.SseEmitterHelper;
import sparkai.service.helper.StreamChatModelBuildHelper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.mapper.system.ModelsMapper;
import sparkai.service.service.interfaces.application.IAiService;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.util.List;
import java.util.Map;

@Component
public class LlmNode implements IWorkflowNode {

    @Setter
    public SseEmitter emitter;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    ModelsMapper modelsMapper;

    @Autowired
    StreamChatModelBuildHelper streamChatModelBuildHelper;

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    @Override
    public List<EdgeVo> handle(NodeVo nodeInfo, long runtimeId, String sourceId, Map<String, List<EdgeVo>> edges) {

        JSONObject nodeObject = nodeInfo.getData();

        // 获取上一个节点的信息
        ApplicationWorkflowRuntimeContextEntity context = applicationWorkflowRuntimeContextMapper.selectOne(
                new QueryWrapper<ApplicationWorkflowRuntimeContextEntity>().eq("runtime_id", runtimeId).eq("cell", sourceId));

        // 模型信息
        JSONObject modelSetInfo = nodeObject.getJSONObject("modelInfo");
        String modelId = modelSetInfo.get("modelId").toString();

        // 获取模型信息
        ModelsEntity modelInfo = modelsMapper.selectById(modelId);
        if (modelInfo == null) {
            throw new BusinessException("模型异常");
        }

        // 构建模型普通对象
        ApplicationEntity applicationInfo = new ApplicationEntity();
        applicationInfo.setTemperature(Double.parseDouble(modelSetInfo.get("temperature").toString()));
        applicationInfo.setModelName(modelSetInfo.get("modelName").toString());
        StreamingChatLanguageModel streamingChatModel = streamChatModelBuildHelper.build(modelInfo, applicationInfo);

        IAiService assistant = AiServices.builder(IAiService.class)
                .streamingChatLanguageModel(streamingChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(nodeObject.getInt("memory"))) // 聊天上下文
                .build();

        JSONArray inputArr = nodeObject.getJSONArray("inputData");
        String inputData = inputArr.get(1).toString();
        JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());
        // 用户提示词
        String userMsg = nodeObject.getStr("userMsg");
        String question = userMsg + preOutput.get(inputData).toString();
        // 系统提示词
        String systemMsg = nodeObject.getStr("systemMsg");

        TokenStream tokenStream;
        if (systemMsg.isBlank()) {
            tokenStream = assistant.chatInTokenStream(question);
        } else {
            tokenStream = assistant.chatWithSystem(systemMsg, question);
        }

        // 发送消息
        sseEmitterHelper.asyncSend2Client(tokenStream, emitter, (res) -> {

            JSONObject jsonRes = JSONUtil.parseObj(res);

            // 记录运行时数据
            ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
            contextEntity.setStep(context.getStep() + 1);
            contextEntity.setNodeType("llm-node");
            contextEntity.setRuntimeId(runtimeId);

            // 记录问题分类节点的输出
            preOutput.set("sys.content", jsonRes.getStr("content"));
            contextEntity.setOutputData(preOutput.toString());

            // 模型使用情况
            JSONObject modelData = JSONUtil.createObj();
            modelData.set("inputTokenCount", jsonRes.getInt("inputTokenCount"));
            modelData.set("outputTokenCount", jsonRes.getInt("outputTokenCount"));
            modelData.set("totalTokenCount", jsonRes.getInt("totalTokenCount"));
            contextEntity.setModelData(modelData.toString());

            contextEntity.setCell(nodeInfo.getId());
            contextEntity.setCreateTime(Tool.nowDateTime());
            applicationWorkflowRuntimeContextMapper.insert(contextEntity);
        });

        // 获取下一个节点
        return edges.get(nodeInfo.getId());
    }
}