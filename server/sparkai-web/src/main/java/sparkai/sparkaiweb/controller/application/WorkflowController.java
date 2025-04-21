// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.sparkaiweb.controller.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sparkai.common.core.AjaxResult;
import sparkai.service.service.interfaces.workflow.IWorkflowService;
import sparkai.service.validate.workflow.SaveWorkflowValidate;
import sparkai.service.vo.workflow.SaveWorkflowVo;

@RequestMapping("/api/workflow")
@RestController
public class WorkflowController {

    @Autowired
    IWorkflowService iWorkflowService;

    /**
     * 获取流程数据
     * @param appId String
     */
    @GetMapping("/info")
    public AjaxResult<SaveWorkflowVo> info(String appId) {

        return AjaxResult.success(iWorkflowService.getFlowInfo(appId));
    }

    /**
     * 保存流程设计
     */
    @PostMapping("/save")
    public AjaxResult<Object> save(@RequestBody @Validated SaveWorkflowValidate validate) {

        iWorkflowService.saveWorkflow(validate);
        return AjaxResult.success();
    }
}