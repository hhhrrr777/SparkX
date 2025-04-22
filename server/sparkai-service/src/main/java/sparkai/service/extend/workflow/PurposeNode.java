package sparkai.service.extend.workflow;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class PurposeNode implements IWorkflowNode {

    @Override
    public void handle(JSONObject nodeObject) {

        System.out.println("------------------------");
        System.out.println(nodeObject);
        System.out.println("------------------------");
    }
}