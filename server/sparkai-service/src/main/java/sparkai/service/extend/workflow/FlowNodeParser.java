package sparkai.service.extend.workflow;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class FlowNodeParser {

    @Autowired
    NodeProvider nodeProvider;

    // 所有的连线
    private final Map<String, List<EdgeVo>> edges = new HashMap<>();

    // 所有的节点
    private final Map<String, NodeVo> nodes = new HashMap<>();

    // 开始节点
    private String startId = "";

    public SseEmitter emitter;

    public void setEmitter(SseEmitter emitter) {
        this.emitter = emitter;
    }

    /**
     * 执行编排流程
     * @param flowData String
     */
    public void run(String flowData) {
        // 构建执行流
        this.buildData(flowData);

        // 开始节点指向的对象
        List<EdgeVo> edgeVoList = this.edges.get(this.startId);
        // TODO 处理并发数据
        for (EdgeVo edgeVo : edgeVoList) {

            NodeVo nodeInfo = this.nodes.get(edgeVo.getTarget());
            if (nodeInfo.getShape().equals("end-node")) {
                // TODO 流程结束
            } else {

                // 获取node处理方法 所有的节点对应的指定方法在 sparkai.service.extend.workflow 下
                IWorkflowNode flowNode = nodeProvider.handle(nodeInfo.getShape());
                flowNode.setEmitter(this.emitter);
                flowNode.handle(nodeInfo.getData());
            }
        }
    }

    /**
     * 构建节点数据
     * @param flowData String
     */
    protected void buildData(String flowData) {

        JSONObject flowObject = JSONUtil.parseObj(flowData);

        // 获得开始节点，并整理出链接点的关系
        for (int i = 0; i < flowObject.getJSONArray("cells").size(); i++) {
            JSONObject item = flowObject.getJSONArray("cells").getJSONObject(i);
            Object shape = item.get("shape");
            if (shape.equals("start-node")) {
                this.startId = item.get("id").toString();
            }

            if (shape.equals("edge")) {

                List<EdgeVo> edge = new LinkedList<>();
                EdgeVo edgeVo = new EdgeVo();
                edgeVo.setId(item.get("id").toString());
                edgeVo.setTarget(item.getJSONObject("target").get("cell").toString());
                edge.add(edgeVo);

                this.edges.put(item.getJSONObject("source").get("cell").toString(), edge);
            } else {
                NodeVo nodeVo = new NodeVo();
                nodeVo.setId(item.get("id").toString());
                nodeVo.setShape(shape.toString());
                nodeVo.setData(item.getJSONObject("data"));

                this.nodes.put(item.get("id").toString(), nodeVo);
            }
        }
    }
}