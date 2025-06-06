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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DatasetNode implements IWorkflowNode {

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

        // 上个节点的信息
        ApplicationWorkflowRuntimeContextEntity context = applicationHelper.getRuntimeContext(runtimeId, sourceId, inputArr.get(0).toString());

        String inputData = inputArr.get(1).toString();
        JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());
        String question = preOutput.get(inputData).toString();

        JSONArray datasetsArr = nodeObject.getJSONArray("datasets");
        List<String> datasetIds = new ArrayList<>();
        for (int i = 0; i < datasetsArr.size(); i++) {
            datasetIds.add(datasetsArr.getJSONObject(i).getStr("datasetId"));
        }

        // 记录运行时数据
        ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
        contextEntity.setStep(context.getStep() + 1);
        contextEntity.setNodeType(NodeTypeEnum.DATASET.getCode());
        contextEntity.setRuntimeId(runtimeId);

        // 记录问题分类节点的输出
        preOutput.set("node_question", question);
        preOutput.set("sys.result", datasetIds);
        contextEntity.setOutputData(preOutput.toString());

        contextEntity.setCell(nodeInfo.getId());
        contextEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        // 获取下一个节点
        return edges.get(nodeInfo.getId());
    }
}