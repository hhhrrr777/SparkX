package sparkai.service.extend.chat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import dev.langchain4j.service.TokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.exception.BusinessException;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.workflow.ApplicationWorkflowEntity;
import sparkai.service.extend.workflow.FlowNodeParser;
import sparkai.service.mapper.workflow.ApplicationWorkflowMapper;
import sparkai.service.validate.application.ApplicationSaveValidate;

@Component
public class WorkflowChat implements IChat {

    @Autowired
    ApplicationWorkflowMapper applicationWorkflowMapper;

    @Autowired
    FlowNodeParser flowNodeParser;

    public SseEmitter emitter;

    public void setEmitter(SseEmitter emitter) {
        this.emitter = emitter;
    }

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

        flowNodeParser.setEmitter(this.emitter);
        flowNodeParser.run(info.getFlowData());

        return null;
    }
}