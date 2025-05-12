package sparkai.service.extend.workflow.node;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.service.entity.application.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.extend.workflow.IWorkflowNode;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.vo.workflow.EdgeVo;
import sparkai.service.vo.workflow.NodeVo;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class SwitchNode implements IWorkflowNode {

    @Setter
    public SseEmitter emitter;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Override
    public List<EdgeVo> handle(NodeVo nodeInfo, long runtimeId, String sourceId, Map<String, List<EdgeVo>> edges) {

        JSONObject nodeObject = nodeInfo.getData();
        // 分支配置
        JSONArray ifBranch = nodeObject.getJSONArray("ifBranch");

        // 获取上一个节点的信息
        ApplicationWorkflowRuntimeContextEntity context = applicationWorkflowRuntimeContextMapper.selectOne(
                new QueryWrapper<ApplicationWorkflowRuntimeContextEntity>().eq("runtime_id", runtimeId).eq("cell", sourceId));
        JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());

        boolean match = false;
        int index = -1;
        for (int i = 0; i < ifBranch.size(); i++) {

            JSONObject nowNodeObject = ifBranch.getJSONObject(i);

            String type = nowNodeObject.getStr("type");
            if ((type.equals("if") || type.equals("elseif")) && !match) {

                JSONArray inputArr = nowNodeObject.getJSONArray("data");
                String inputData = inputArr.get(1).toString();
                String inputVal = String.valueOf(preOutput.get(inputData));
                int tips = nowNodeObject.getInt("tips");
                String value = nowNodeObject.getStr("value");
                switch (tips) {
                    // 为空
                    case 1 -> {
                        if (StrUtil.isBlank(inputVal)) {
                            match = true;
                            index = i;
                        }
                    }
                    // 不为空
                    case 2 -> {
                        if (StrUtil.isNotBlank(inputVal)) {
                            match = true;
                            index = i;
                        }
                    }
                    // 包含
                    case 3 -> {
                        if (inputVal.contains(value)) {
                            match = true;
                            index = i;
                        }
                    }
                    // 不包含
                    case 4 -> {
                        if (!inputVal.contains(value)) {
                            match = true;
                            index = i;
                        }
                    }
                    // 等于
                    case 5 -> {
                        if (inputVal.equals(value)) {
                            match = true;
                            index = i;
                        }
                    }
                    // 大于等于
                    case 6 -> {
                        if (Integer.parseInt(inputVal) >= Integer.parseInt(value)) {
                            match = true;
                            index = i;
                        }
                    }
                    // 小于
                    case 7 -> {
                        if (Integer.parseInt(inputVal) < Integer.parseInt(value)) {
                            match = true;
                            index = i;
                        }
                    }
                    // 长度等于
                    case 8 -> {
                        if (inputVal.length() == Integer.parseInt(value)) {
                            match = true;
                            index = i;
                        }
                    }
                    // 长度大于等于
                    case 9 -> {
                        if (inputVal.length() >= Integer.parseInt(value)) {
                            match = true;
                            index = i;
                        }
                    }
                    // 长度大于
                    case 10 -> {
                        if (inputVal.length() > Integer.parseInt(value)) {
                            match = true;
                            index = i;
                        }
                    }
                    // 长度小于等于
                    case 11 -> {
                        if (inputVal.length() <= Integer.parseInt(value)) {
                            match = true;
                            index = i;
                        }
                    }
                    // 长度小于
                    case 12 -> {
                        if (inputVal.length() < Integer.parseInt(value)) {
                            match = true;
                            index = i;
                        }
                    }
                }
            }
        }

        // 最终的else分支
        if (!match) {
            index = ifBranch.size() - 1;
        }

        // 获取下一个节点
        List<EdgeVo> nextEdgeVoList = edges.get(nodeInfo.getId());
        List<EdgeVo> newEdgeVoList = new LinkedList<>();
        newEdgeVoList.add(nextEdgeVoList.get(index));

        return newEdgeVoList;
    }
}