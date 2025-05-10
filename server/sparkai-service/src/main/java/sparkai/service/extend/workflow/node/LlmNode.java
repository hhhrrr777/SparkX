package sparkai.service.extend.workflow.node;

import cn.hutool.json.JSONObject;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.service.extend.workflow.IWorkflowNode;

@Component
public class LlmNode implements IWorkflowNode {

    @Setter
    public SseEmitter emitter;

    @Override
    public void handle(JSONObject nodeObject, long runtimeId) {

    }
}