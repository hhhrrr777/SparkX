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
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.enums.NodeTypeEnum;
import sparkai.common.utils.Tool;
import sparkai.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.helper.ApplicationHelper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeRuntimeVo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public class DatasetNode implements IWorkflowNode {

    @Setter
    public SseEmitter emitter;

    @Setter
    public CountDownLatch latch;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    ApplicationHelper applicationHelper;

    @Override
    public List<EdgeVo> handle(NodeRuntimeVo runtimeVo) {

        JSONObject nodeObject = runtimeVo.getNodeInfo().getData();
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

        String question;
        JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());
        if (!inputArr.isEmpty()) {
            String inputData = inputArr.get(1).toString();
            question = preOutput.get(inputData).toString();
        } else {
            question = "";
        }

        JSONArray datasetsArr = nodeObject.getJSONArray("datasets");
        List<String> datasetIds = new ArrayList<>();
        for (int i = 0; i < datasetsArr.size(); i++) {
            datasetIds.add(datasetsArr.getJSONObject(i).getStr("datasetId"));
        }

        // 记录运行时数据
        ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
        contextEntity.setStep(context.getStep() + 1);
        contextEntity.setNodeType(NodeTypeEnum.DATASET.getCode());
        contextEntity.setRuntimeId(runtimeVo.getRuntimeId());

        // 记录问题分类节点的输出
        preOutput.set("node_question", question);
        preOutput.set("sys.result", String.join(",", datasetIds));
        contextEntity.setOutputData(preOutput.toString());

        contextEntity.setCell(runtimeVo.getNodeInfo().getId());
        contextEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        latch.countDown();

        // 获取下一个节点
        return runtimeVo.getEdges().get(runtimeVo.getNodeInfo().getId());
    }
}