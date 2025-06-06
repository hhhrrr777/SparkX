package sparkai.service.extend.workflow.node;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.enums.NodeTypeEnum;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.application.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.entity.system.ModelsEntity;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.helper.ApplicationHelper;
import sparkai.service.helper.ChatModelBuildHelper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.mapper.system.ModelsMapper;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class PurposeNode implements IWorkflowNode {

    @Autowired
    ModelsMapper modelsMapper;

    @Autowired
    ChatModelBuildHelper chatModelBuildHelper;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Setter
    public SseEmitter emitter;

    @Autowired
    ApplicationHelper applicationHelper;

    @Override
    public List<EdgeVo> handle(NodeVo nodeInfo, long runtimeId, String sourceId, Map<String, List<EdgeVo>> edges) {

        JSONObject nodeObject = nodeInfo.getData();
        JSONObject modeData = nodeObject.getJSONObject("modelInfo");
        String modelId = modeData.get("modelId").toString();

        // 获取模型信息
        ModelsEntity modelInfo = modelsMapper.selectById(modelId);
        if (modelInfo == null) {
            throw new BusinessException("模型异常");
        }

        // 构建模型普通对象
        ApplicationEntity applicationInfo = new ApplicationEntity();
        applicationInfo.setTemperature(Double.parseDouble(modeData.get("temperature").toString()));
        applicationInfo.setModelName(modeData.get("modelName").toString());
        ChatLanguageModel chatLanguageModel = chatModelBuildHelper.build(modelInfo, applicationInfo);

        // 读取设置的问题分类
        JSONArray cateList = nodeObject.getJSONArray("cateList");
        StringBuilder cateListStr = new StringBuilder();
        for (int i = 0; i < cateList.size(); i++) {
            cateListStr.append((i + 1)).append(":").append(cateList.getJSONObject(i).get("name").toString()).append("\n");
        }

        // 获取上一个节点的信息
        ApplicationWorkflowRuntimeContextEntity context = applicationHelper.getRuntimeContext(runtimeId, sourceId, nodeInfo.getId());

        // 本节点输入的参数
        JSONArray inputArr = nodeObject.getJSONArray("inputData");
        String inputData = inputArr.get(1).toString();
        JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());

        String question = "已知问题分类：\n" + cateListStr + "\n请根据问题：" + preOutput.get(inputData).toString()
                + "。\n判断出所属的分类并仅给出问题前的编号";
        UserMessage userMessage = UserMessage.from(TextContent.from(question));
        ChatResponse chatResponse = chatLanguageModel.chat(userMessage);
        String answer = chatResponse.aiMessage().text();

        // 记录运行时数据
        ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
        contextEntity.setStep(context.getStep() + 1);
        contextEntity.setNodeType(NodeTypeEnum.PURPOSE.getCode());
        contextEntity.setRuntimeId(runtimeId);

        // 记录问题分类节点的输出
        int index = Integer.parseInt(answer) - 1;
        preOutput.set("sys.purposeName", cateList.getJSONObject(index).get("name"));
        contextEntity.setOutputData(preOutput.toString());

        // 模型使用情况
        JSONObject modelData = JSONUtil.createObj();
        modelData.set("inputTokenCount", chatResponse.tokenUsage().inputTokenCount());
        modelData.set("outputTokenCount", chatResponse.tokenUsage().outputTokenCount());
        modelData.set("totalTokenCount", chatResponse.tokenUsage().totalTokenCount());
        contextEntity.setModelData(modelData.toString());

        contextEntity.setCell(nodeInfo.getId());
        contextEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        // 获取下一个节点
        List<EdgeVo> nextEdgeVoList = edges.get(nodeInfo.getId());
        List<EdgeVo> newEdgeVoList = new LinkedList<>();
        newEdgeVoList.add(nextEdgeVoList.get(index));

        return newEdgeVoList;
    }
}