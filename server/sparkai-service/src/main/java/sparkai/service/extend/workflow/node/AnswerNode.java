package sparkai.service.extend.workflow.node;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.enums.NodeTypeEnum;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.helper.SseEmitterHelper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.service.interfaces.dataset.IHitTestService;
import sparkai.service.vo.dataset.HitTestVo;
import sparkai.service.vo.dataset.SearchVo;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AnswerNode implements IWorkflowNode {

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Setter
    public SseEmitter emitter;

    @Autowired
    IHitTestService searchService;

    @Override
    public List<EdgeVo> handle(NodeVo nodeInfo, long runtimeId, String sourceId, Map<String, List<EdgeVo>> edges) {

        try {

            JSONObject nodeObject = nodeInfo.getData();
            Integer answerType = nodeObject.getInt("answerType");

            // 上个节点的信息
            ApplicationWorkflowRuntimeContextEntity context = applicationWorkflowRuntimeContextMapper.selectOne(
                    new QueryWrapper<ApplicationWorkflowRuntimeContextEntity>().eq("runtime_id", runtimeId).eq("cell", sourceId));

            // 记录运行时数据
            ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
            contextEntity.setStep(context.getStep() + 1);
            contextEntity.setNodeType(NodeTypeEnum.ANSWER.getCode());
            contextEntity.setRuntimeId(runtimeId);

            JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());
            if (answerType.equals(1)) {

                // 找出回复内容
                JSONArray inputArr = nodeObject.getJSONArray("inputData");
                String returnAnswerType = inputArr.get(1).toString();
                String answer = "";
                // 如果上个节点是dataset节点，且输出为检索结果
                if (context.getNodeType().equals(NodeTypeEnum.DATASET.getCode())
                        && returnAnswerType.equals("sys.result")) {

                    // 执行知识库检索并输出
                    String question = preOutput.getStr("node_question");
                    String datasetIds = preOutput.getStr("sys.result");
                    answer = searchDataset(question, datasetIds);
                } else {
                    answer = preOutput.get(returnAnswerType).toString();
                }

                emitter.send(answer);
                // 记录问题分类节点的输出
                preOutput.set("sys.answer", answer);
            } else {
                emitter.send(nodeObject.getStr("answer"));

                // 记录问题分类节点的输出
                preOutput.set("sys.answer", nodeObject.getStr("answer"));
            }

            contextEntity.setOutputData(preOutput.toString());
            contextEntity.setCell(nodeInfo.getId());
            contextEntity.setCreateTime(Tool.nowDateTime());
            applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        } catch (IOException e) {
            sseEmitterHelper.sendErrorSse(emitter, e.getMessage());
        }

        // 获取下一个节点
        return edges.get(nodeInfo.getId());
    }

    /**
     * 知识库检索
     * @param question String
     * @param datasetIds String
     * @return String
     */
    private String searchDataset(String question, String datasetIds) {

        HitTestVo searchDataVo = new HitTestVo();
        searchDataVo.setKeyword(question);
        searchDataVo.setDatasetIds(datasetIds);
        searchDataVo.setSimilarity(0.9);
        searchDataVo.setTopRank(5);
        searchDataVo.setType("embedding");
        List<SearchVo> searchRes = searchService.search(searchDataVo);

        return searchRes.stream().map(SearchVo::getContent).collect(Collectors.joining());
    }
}