package sparkai.service.extend.workflow;

import cn.hutool.json.JSONObject;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface IWorkflowNode {

    void setEmitter(SseEmitter emitter);

    void handle(JSONObject nodeObject);
}
