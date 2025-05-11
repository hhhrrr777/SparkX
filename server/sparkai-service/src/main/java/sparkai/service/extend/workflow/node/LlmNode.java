package sparkai.service.extend.workflow.node;

import cn.hutool.json.JSONObject;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.util.List;
import java.util.Map;

@Component
public class LlmNode implements IWorkflowNode {

    @Setter
    public SseEmitter emitter;

    @Override
    public List<EdgeVo> handle(NodeVo nodeInfo, long runtimeId, String sourceId, Map<String, List<EdgeVo>> edges) {
        return null;
    }
}