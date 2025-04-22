package sparkai.service.extend.workflow.node;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.service.extend.workflow.IWorkflowNode;

@Component
public class PurposeNode implements IWorkflowNode {

    public SseEmitter emitter;

    public void setEmitter(SseEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void handle(JSONObject nodeObject) {

        System.out.println("------------------------");
        System.out.println(nodeObject);
        System.out.println("------------------------");
    }
}