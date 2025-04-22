package sparkai.service.extend.workflow.node;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.service.extend.workflow.IWorkflowNode;

@Component
public class DatasetNode implements IWorkflowNode {

    public SseEmitter emitter;

    public void setEmitter(SseEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void handle(JSONObject nodeObject) {

    }
}