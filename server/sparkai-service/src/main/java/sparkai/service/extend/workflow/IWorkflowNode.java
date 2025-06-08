package sparkai.service.extend.workflow;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public interface IWorkflowNode {

    void setEmitter(SseEmitter emitter);

    void setLatch(CountDownLatch latch);

    List<EdgeVo> handle(NodeVo nodeInfo, long runtimeId, String sourceId, Map<String, List<EdgeVo>> edges);
}
