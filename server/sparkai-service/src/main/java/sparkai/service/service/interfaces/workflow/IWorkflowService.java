// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
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