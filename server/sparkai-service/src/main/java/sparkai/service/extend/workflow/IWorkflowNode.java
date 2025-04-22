package sparkai.service.extend.workflow;

import cn.hutool.json.JSONObject;

public interface IWorkflowNode {

    void handle(JSONObject nodeObject);
}
