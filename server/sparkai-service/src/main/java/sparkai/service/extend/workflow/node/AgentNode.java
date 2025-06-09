package sparkai.service.extend.workflow.node;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
import sparkai.service.entity.application.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.extend.chat.AgentChat;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.helper.ApplicationHelper;
import sparkai.service.helper.SseEmitterHelper;
import sparkai.service.mapper.application.ApplicationMapper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.validate.application.ApplicationChatValidate;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class AgentNode implements IWorkflowNode {

    @Setter
    public SseEmitter emitter;

    @Setter
    public CountDownLatch latch;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    ApplicationMapper applicationMapper;

    @Autowired
    AgentChat agentChat;

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    @Autowired
    ApplicationHelper applicationHelper;

    @Override
    public List<EdgeVo> handle(NodeVo nodeInfo, long runtimeId, String sourceId, Map<String, List<EdgeVo>> edges) {

        JSONObject nodeObject = nodeInfo.getData();
        // 本节点输入的参数
        JSONArray inputArr = nodeObject.getJSONArray("inputData");
        String inputSourceId;
        if (inputArr.size() > 0) {
            inputSourceId = inputArr.get(0).toString();
        } else {
            inputSourceId = "";
        }

        // 获取上一个节点的信息
        ApplicationWorkflowRuntimeContextEntity context = applicationHelper.getRuntimeContext(runtimeId, sourceId, inputSourceId);
        if (context == null) {
            return null;
        }

        String inputData = inputArr.get(1).toString();
        JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());

        String question = preOutput.get(inputData).toString();
        String agentId = nodeObject.getStr("agentId");

        // 获取应用信息
        ApplicationEntity applicationInfo = applicationMapper.selectById(agentId);
        if (applicationInfo == null) {
            throw new BusinessException("应用配置错误");
        }

        ApplicationChatValidate validate = new ApplicationChatValidate();
        validate.setContent(question);
        validate.setAppId(agentId);
        validate.setDatasetList(applicationHelper.getRelationDatasetList(agentId));

        try {

            TokenStream tokenStream = agentChat.streamChat(applicationInfo, validate);

            sseEmitterHelper.asyncSend2Client(tokenStream, emitter, (response) -> {

                // 记录运行时数据
                ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
                contextEntity.setStep(context.getStep() + 1);
                contextEntity.setNodeType(NodeTypeEnum.AGENT.getCode());
                contextEntity.setRuntimeId(runtimeId);

                // 模型使用情况
                JSONObject modelData = JSONUtil.createObj();
                JSONObject llmResData = JSONUtil.parseObj(response);
                modelData.set("inputTokenCount", llmResData.getStr("inputTokenCount"));
                modelData.set("outputTokenCount", llmResData.getStr("outputTokenCount"));
                modelData.set("totalTokenCount", llmResData.getStr("totalTokenCount"));
                contextEntity.setModelData(modelData.toString());

                // 记录问题分类节点的输出
                String answer = llmResData.getStr("content");
                preOutput.set("sys.agentContent", answer);

                contextEntity.setOutputData(preOutput.toString());
                contextEntity.setCell(nodeInfo.getId());
                contextEntity.setCreateTime(Tool.nowDateTime());
                applicationWorkflowRuntimeContextMapper.insert(contextEntity);

                latch.countDown();
            });

        } catch (Exception e) {
            log.error("回复节点构建llm错误：", e);
            return null;
        }

        // 获取下一个节点
        return edges.get(nodeInfo.getId());
    }
}