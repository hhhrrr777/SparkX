package sparkai.service.extend.workflow.node;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.helper.SseEmitterHelper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class AnswerNode implements IWorkflowNode {

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Setter
    public SseEmitter emitter;

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
            contextEntity.setNodeType("answer-node");
            contextEntity.setRuntimeId(runtimeId);

            if (answerType.equals(1)) {

                // 找出回复内容
                JSONArray inputArr = nodeObject.getJSONArray("inputData");
                JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());
                String answer = preOutput.get(inputArr.get(1).toString()).toString();
                emitter.send(answer);

                // 记录问题分类节点的输入
                contextEntity.setInputData(context.getOutputData());

                // 记录问题分类节点的输出
                JSONObject dbOutputData = JSONUtil.createObj();
                dbOutputData.set("sys.answer", answer);
                contextEntity.setOutputData(dbOutputData.toString());
            } else {
                emitter.send(nodeObject.getStr("answer"));

                // 记录问题分类节点的输出
                JSONObject dbOutputData = JSONUtil.createObj();
                dbOutputData.set("sys.answer", nodeObject.getStr("answer"));
            }

            contextEntity.setCell(nodeInfo.getId());
            contextEntity.setCreateTime(Tool.nowDateTime());
            applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        } catch (IOException e) {
            sseEmitterHelper.sendErrorSse(emitter, e.getMessage());
        }

        return null;
    }
}