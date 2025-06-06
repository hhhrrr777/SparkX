package sparkai.service.extend.workflow.node;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.enums.NodeTypeEnum;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.helper.ApplicationHelper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
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
    ApplicationHelper applicationHelper;

    @Override
    public List<EdgeVo> handle(NodeVo nodeInfo, long runtimeId, String sourceId, Map<String, List<EdgeVo>> edges) {

        JSONObject nodeObject = nodeInfo.getData();
        // 本节点输入的参数
        JSONArray inputArr = nodeObject.getJSONArray("inputData");

        // 获取上一个节点的信息
        ApplicationWorkflowRuntimeContextEntity context = applicationHelper.getRuntimeContext(runtimeId, sourceId, inputArr.get(0).toString());

        // 记录运行时数据
        ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
        contextEntity.setStep(context.getStep() + 1);
        contextEntity.setNodeType(NodeTypeEnum.LLM.getCode());
        contextEntity.setRuntimeId(runtimeId);
        contextEntity.setModelData(nodeObject.toString());
        JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());
        contextEntity.setOutputData(preOutput.toString());
        contextEntity.setCell(nodeInfo.getId());
        contextEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        // 获取下一个节点
        return edges.get(nodeInfo.getId());
    }
}