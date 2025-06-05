package sparkai.service.extend.workflow;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.service.entity.application.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.util.*;

@Component
@Slf4j
public class FlowNodeParser {

    @Autowired
    NodeProvider nodeProvider;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    // 所有的连线
    private Map<String, List<EdgeVo>> edges = new HashMap<>();

    // 所有的节点
    private Map<String, NodeVo> nodes = new HashMap<>();

    // 开始节点
    private String startId = "";

    @Setter
    public SseEmitter emitter;

    // 运行时id
    @Setter
    public Long runtimeId;

    /**
     * 执行编排流程
     * @param flowData String
     */
    public void run(String flowData) {
        // 重新初始化
        this.edges = new HashMap<>();
        this.nodes = new HashMap<>();

        // 构建执行流
        this.buildData(flowData);

        log.info("连线 ： {}" , JSONUtil.toJsonStr(this.edges));
        log.info("节点 ： {}" , JSONUtil.toJsonStr(this.nodes));

        // 开始节点指向的对象
        List<EdgeVo> edgeVoList = this.edges.get(this.startId);
        execute(edgeVoList, this.startId);
    }

    /**
     * 节点逻辑执行
     * @param edgeVoList List<EdgeVo>
     * @param sourceId String
     */
    private void execute(List<EdgeVo> edgeVoList, String sourceId) {

        if (CollectionUtils.isEmpty(edgeVoList)) {
            return;
        }

        log.info("本次进入的节点数量 ： {}" , edgeVoList.size());

        for (EdgeVo edgeVo : edgeVoList) {

            NodeVo nodeInfo = this.nodes.get(edgeVo.getTarget());
            log.info("本次解析的节点是 ： {}", nodeInfo);

            // 获取node处理方法 所有的节点对应的指定方法在 sparkai.service.extend.workflow.node 下
            IWorkflowNode flowNode = nodeProvider.handle(nodeInfo.getShape());
            flowNode.setEmitter(this.emitter);
            List<EdgeVo> nextEdgeVoList = flowNode.handle(nodeInfo, this.runtimeId, sourceId, this.edges);
            execute(nextEdgeVoList, nodeInfo.getId());
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
                // 更新节点id
                ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
                contextEntity.setCell(this.startId);
                applicationWorkflowRuntimeContextMapper.update(contextEntity,
                        new QueryWrapper<ApplicationWorkflowRuntimeContextEntity>().eq("node_type", "start-node")
                                .eq("runtime_id", this.runtimeId));
            }

            if (shape.equals("edge")) {

                String key = item.getJSONObject("source").get("cell").toString();
                List<EdgeVo> hasEdges = this.edges.get(key);

                EdgeVo edgeVo = new EdgeVo();
                edgeVo.setId(item.get("id").toString());
                edgeVo.setSource(item.getJSONObject("source").get("cell").toString());
                edgeVo.setSourcePort(item.getJSONObject("source").get("port").toString());
                edgeVo.setTarget(new ArrayList<>());

                if (CollectionUtils.isEmpty(hasEdges)) {
                    List<EdgeVo> edge = new LinkedList<>();
                    edge.add(edgeVo);

                    List<String> targetCells = new LinkedList<>();
                    targetCells.add(item.getJSONObject("target").get("cell").toString());
                    edgeVo.setTarget(targetCells);

                    this.edges.put(key, edge);
                } else {

                    // 并联节点
                    boolean hasMultiple = false;
                    for (EdgeVo hasEdge : hasEdges) {
                        if (hasEdge.getSourcePort().equals(edgeVo.getSourcePort())) {
                            hasMultiple = true;

                            List<String> targetCells = hasEdge.getTarget();
                            targetCells.add(item.getJSONObject("target").get("cell").toString());
                            hasEdge.setTarget(targetCells);
                        }
                    }

                    if (!hasMultiple) {
                        hasEdges.add(edgeVo);
                    }

                    this.edges.put(key, hasEdges);
                }

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