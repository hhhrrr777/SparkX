package sparkai.service.service.interfaces.workflow;

import sparkai.service.validate.workflow.SaveWorkflowValidate;
import sparkai.service.vo.workflow.RuntimeContextVo;
import sparkai.service.vo.workflow.SaveWorkflowVo;

import java.util.List;

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

    /**
     * 获取执行详情
     * @param runtimeId long
     * @return List<RuntimeContextVo>
     */
    List<RuntimeContextVo> getRuntimeDetail(long runtimeId);
}