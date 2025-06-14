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
import sparkai.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.entity.system.ModelsEntity;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.helper.ApplicationHelper;
import sparkai.service.helper.ChatModelBuildHelper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.mapper.system.ModelsMapper;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeRuntimeVo;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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

    @Setter
    public CountDownLatch latch;

    @Autowired
    ApplicationHelper applicationHelper;

    @Override
    public List<EdgeVo> handle(NodeRuntimeVo runtimeVo) {

        JSONObject nodeObject = runtimeVo.getNodeInfo().getData();
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

        // 本节点输入的参数
        JSONArray inputArr = nodeObject.getJSONArray("inputData");
        String inputSourceId;
        if (!inputArr.isEmpty()) {
            inputSourceId = inputArr.get(0).toString();
        } else {
            inputSourceId = "";
        }

        // 获取上一个节点的信息
        ApplicationWorkflowRuntimeContextEntity context =
                applicationHelper.getRuntimeContext(runtimeVo.getRuntimeId(), runtimeVo.getSourceId(), inputSourceId);

        String inputData = inputArr.get(1).toString();
        JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());

        String question = "已知问题分类：\n" + cateListStr + "\n请根据问题：" + preOutput.get(inputData).toString()
                + "。\n判断出所属的分类并仅给出问题前的编号,例如：1";
        UserMessage userMessage = UserMessage.from(TextContent.from(question));
        ChatResponse chatResponse = chatLanguageModel.chat(userMessage);
        String answer = chatResponse.aiMessage().text();

        // 记录运行时数据
        ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
        contextEntity.setStep(context.getStep() + 1);
        contextEntity.setNodeType(NodeTypeEnum.PURPOSE.getCode());
        contextEntity.setRuntimeId(runtimeVo.getRuntimeId());

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

        contextEntity.setCell(runtimeVo.getNodeInfo().getId());
        contextEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        // 获取下一个节点
        List<EdgeVo> nextEdgeVoList = runtimeVo.getEdges().get(runtimeVo.getNodeInfo().getId());
        List<EdgeVo> newEdgeVoList = new LinkedList<>();
        newEdgeVoList.add(nextEdgeVoList.get(index));

        latch.countDown();

        return newEdgeVoList;
    }
}