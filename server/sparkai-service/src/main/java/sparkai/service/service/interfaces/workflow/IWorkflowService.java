package sparkai.service.service.interfaces.workflow;

import sparkai.service.validate.workflow.SaveWorkflowValidate;
import sparkai.service.vo.workflow.SaveWorkflowVo;

public interface IWorkflowService {

    /**
     * 获取流程数据
     * @param appId String
     * @return SaveWorkflowVo
     */
    SaveWorkflowVo getFlowInfo(String appId);

    /**
     * 保存流程设计
     * @param validate SaveWorkflowValidate
     */
    void saveWorkflow(SaveWorkflowValidate validate);
}