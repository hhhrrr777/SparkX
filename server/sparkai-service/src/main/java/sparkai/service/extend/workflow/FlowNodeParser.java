// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.extend.workflow;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.exception.BusinessException;
import sparkai.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.helper.SseEmitterHelper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeRuntimeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    // 运行时id
    @Setter
    public Long runtimeId;

    /**
     * 执行编排流程
     * @param flowData String
     * @param userId String
     * @param sessionId String
     */
    @Async
    public void run(String flowData, String userId, String sessionId) {
        // 重新初始化
        this.edges = new HashMap<>();
        this.nodes = new HashMap<>();

        // 构建执行流
        this.buildData(flowData);

        // 开始节点指向的对象
        List<EdgeVo> edgeVoList = this.edges.get(this.startId);
        if (CollectionUtils.isEmpty(edgeVoList)) {
            throw new BusinessException("流程异常");
        }

        execute(edgeVoList.get(0), this.startId, userId, sessionId);
    }

    /**
     * 节点逻辑执行
     * @param edgeVo EdgeVo
     * @param userId String
     * @param sourceId String
     * @param sessionId String
     */
    private void execute(EdgeVo edgeVo, String sourceId, String userId, String sessionId) {

        Map<String, EdgeVo> nextNeedVoMap = new HashMap<>();
        List<String> targetIds = edgeVo.getTarget();

        CountDownLatch latch = new CountDownLatch(targetIds.size());

        // 并联流程
        for (String targetId : targetIds) {

            NodeVo nodeInfo = this.nodes.get(targetId);
            // log.info("本次解析的节点是 ： {}", nodeInfo);
            // 获取node处理方法 所有的节点对应的指定方法在 sparkai.service.extend.workflow.node 下
            IWorkflowNode flowNode = nodeProvider.handle(nodeInfo.getShape());
            flowNode.setEmitter(this.emitter);
            flowNode.setLatch(latch);

            NodeRuntimeVo runtimeVo = new NodeRuntimeVo();
            runtimeVo.setNodeInfo(nodeInfo);
            runtimeVo.setEdges(this.edges);
            runtimeVo.setNodes(this.nodes);
            runtimeVo.setRuntimeId(this.runtimeId); // 运行id
            runtimeVo.setSourceId(sourceId); // 开始节点
            runtimeVo.setUserId(userId); // 当前用户
            runtimeVo.setSessionId(sessionId);

            List<EdgeVo> nextEdgeVoList = flowNode.handle(runtimeVo);

            if (!CollectionUtils.isEmpty(nextEdgeVoList)) {
                nextNeedVoMap.put(nodeInfo.getId(), nextEdgeVoList.get(0));
            }
        }

        try {

            // 阻塞等到节点中的异步发送完成后，再继续
            latch.await();

            // 进入下一个节点
            if (!MapUtil.isEmpty(nextNeedVoMap)) {

                nextNeedVoMap.forEach((nodeId, nodeData) -> {
                    execute(nodeData, nodeId, userId, sessionId);
                });
            } else { // 流程结束
                sseEmitterHelper.sendEndSse(emitter, "");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 处理中断异常
            log.error("线程阻塞错误, {}", e.getMessage());
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
                        List<String> targetCell = new LinkedList<>();
                        targetCell.add(item.getJSONObject("target").get("cell").toString());
                        edgeVo.setTarget(targetCell);
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