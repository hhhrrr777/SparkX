package sparkai.service.extend.workflow.node;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.enums.NodeTypeEnum;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.helper.ApplicationHelper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class SwitchNode implements IWorkflowNode {

    @Setter
    public SseEmitter emitter;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    ApplicationHelper applicationHelper;

    @Override
    public List<EdgeVo> handle(NodeVo nodeInfo, long runtimeId, String sourceId, Map<String, List<EdgeVo>> edges) {

        JSONObject nodeObject = nodeInfo.getData();
        // 分支配置
        JSONArray ifBranch = nodeObject.getJSONArray("ifBranch");

        boolean match = false;
        JSONObject preOutput = null;
        ApplicationWorkflowRuntimeContextEntity context = null;
        int index = -1;

        for (int i = 0; i < ifBranch.size(); i++) {

            JSONObject nowNodeObject = ifBranch.getJSONObject(i);
            String type = nowNodeObject.getStr("type");
            if ((type.equals("if") || type.equals("elseif"))) {

                Integer judging = nowNodeObject.getInt("switch");
                // 本节点输入的参数
                JSONArray inputArr = nowNodeObject.getJSONArray("data");
                List<Boolean> matchArr = new ArrayList<>();
                for (int j = 0; j < inputArr.size(); j++) {

                    JSONArray inputJsonArr = inputArr.getJSONObject(j).getJSONArray("input");
                    String inputIndex = inputJsonArr.get(1).toString();
                    String inputSourceId = inputJsonArr.get(0).toString();
                    // 获取上一个节点的信息
                    context = applicationHelper.getRuntimeContext(runtimeId, sourceId, inputSourceId);
                    if (context == null) {
                        return null;
                    }

                    preOutput = JSONUtil.parseObj(context.getOutputData());
                    String inputData = preOutput.get(inputIndex).toString();
                    int tips = inputArr.getJSONObject(j).getInt("tips");
                    String value = inputArr.getJSONObject(j).getStr("value");

                    Boolean matchRes = switchTest(tips, inputData, value);
                    matchArr.add(matchRes);
                }

                // AND 条件
                if (judging.equals(1)) {

                    boolean hasMatch = matchArr.stream().allMatch(item -> item);
                    if (hasMatch) {
                        match = true;
                        index = i;
                    }
                } else { // OR 条件

                    for (Boolean item : matchArr) {
                        if (item) {
                            match = true;
                            index = i;
                            break;
                        }
                    }
                }
            }

            if (match) {
                break;
            }
        }

        // 最终的else分支
        if (!match) {
            index = ifBranch.size() - 1;
        }

        // 记录运行时数据
        if (context != null) {
            ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
            contextEntity.setStep(context.getStep() + 1);
            contextEntity.setNodeType(NodeTypeEnum.SWITCH.getCode());
            contextEntity.setRuntimeId(runtimeId);
            // 记录问题分类节点的输入
            contextEntity.setOutputData(preOutput.toString());
            contextEntity.setCell(nodeInfo.getId());
            contextEntity.setCreateTime(Tool.nowDateTime());
            applicationWorkflowRuntimeContextMapper.insert(contextEntity);
        }

        // 获取下一个节点
        List<EdgeVo> nextEdgeVoList = edges.get(nodeInfo.getId());
        List<EdgeVo> newEdgeVoList = new LinkedList<>();
        newEdgeVoList.add(nextEdgeVoList.get(index));

        return newEdgeVoList;
    }

    /**
     * 条件判断
     * @param tips int
     * @param inputVal String
     * @param value String
     * @return boolean
     */
    private boolean switchTest(int tips, String inputVal, String value) {
        boolean match = false;
        switch (tips) {
            // 为空
            case 1 -> {
                if (StrUtil.isBlank(inputVal)) {
                    match = true;
                }
            }
            // 不为空
            case 2 -> {
                if (StrUtil.isNotBlank(inputVal)) {
                    match = true;
                }
            }
            // 包含
            case 3 -> {
                if (inputVal.contains(value)) {
                    match = true;
                }
            }
            // 不包含
            case 4 -> {
                if (!inputVal.contains(value)) {
                    match = true;
                }
            }
            // 等于
            case 5 -> {
                if (inputVal.equals(value)) {
                    match = true;
                }
            }
            // 大于等于
            case 6 -> {
                if (Integer.parseInt(inputVal) >= Integer.parseInt(value)) {
                    match = true;
                }
            }
            // 小于
            case 7 -> {
                if (Integer.parseInt(inputVal) < Integer.parseInt(value)) {
                    match = true;
                }
            }
            // 长度等于
            case 8 -> {
                if (inputVal.length() == Integer.parseInt(value)) {
                    match = true;
                }
            }
            // 长度大于等于
            case 9 -> {
                if (inputVal.length() >= Integer.parseInt(value)) {
                    match = true;
                }
            }
            // 长度大于
            case 10 -> {
                if (inputVal.length() > Integer.parseInt(value)) {
                    match = true;
                }
            }
            // 长度小于等于
            case 11 -> {
                if (inputVal.length() <= Integer.parseInt(value)) {
                    match = true;
                }
            }
            // 长度小于
            case 12 -> {
                if (inputVal.length() < Integer.parseInt(value)) {
                    match = true;
                }
            }
        }

        return match;
    }
}