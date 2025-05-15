package sparkai.service.extend.chat;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import dev.langchain4j.service.TokenStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.application.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.entity.application.ApplicationWorkflowRuntimeEntity;
import sparkai.service.entity.workflow.ApplicationWorkflowEntity;
import sparkai.service.extend.workflow.FlowNodeParser;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeMapper;
import sparkai.service.mapper.workflow.ApplicationWorkflowMapper;
import sparkai.service.validate.application.ApplicationSaveValidate;

@Component
public class WorkflowChat implements IChat {

    @Autowired
    ApplicationWorkflowMapper applicationWorkflowMapper;

    @Autowired
    FlowNodeParser flowNodeParser;

    @Autowired
    ApplicationWorkflowRuntimeMapper applicationWorkflowRuntimeMapper;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Setter
    public SseEmitter emitter;

    /**
     * 编排模式聊天
     * @param applicationInfo ApplicationEntity
     * @param validate ApplicationSaveValidate
     * @return TokenStream
     */
    @Override
    public TokenStream streamChat(ApplicationEntity applicationInfo, ApplicationSaveValidate validate) {

        // 流程配置
        ApplicationWorkflowEntity info = applicationWorkflowMapper.selectOne(new QueryWrapper<ApplicationWorkflowEntity>()
                .eq("app_id", applicationInfo.getAppId()));
        if (info == null) {
            throw new BusinessException("流程未配置");
        }

        // 记录开始节点数据
        ApplicationWorkflowRuntimeEntity runtimeEntity = new ApplicationWorkflowRuntimeEntity();
        String title = validate.getContent();
        if (title.length() > 25) {
            title = title.substring(0, 25);
        }
        runtimeEntity.setTitle(title);
        runtimeEntity.setUserId("b6c67084-ad55-4ced-82c4-4d9d304e8616");
        runtimeEntity.setFlowId(info.getId());
        runtimeEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeMapper.insert(runtimeEntity);

        ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
        contextEntity.setStep(1);
        contextEntity.setNodeType("start-node");
        contextEntity.setRuntimeId(runtimeEntity.getId());

        JSONObject inputData = JSONUtil.createObj();
        inputData.set("sys.question", validate.getContent());
        inputData.set("sys.time", Tool.nowDateTime());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        inputData.set("sys.ip", request.getRemoteAddr());
        inputData.set("sys.appId", info.getAppId());
        contextEntity.setOutputData(inputData.toString());

        contextEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        // 启动节点执行
        flowNodeParser.setEmitter(this.emitter);
        flowNodeParser.setRuntimeId(runtimeEntity.getId());
        flowNodeParser.run(info.getFlowData());

        return null;
    }
}