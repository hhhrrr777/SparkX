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
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.core.AjaxResult;
import sparkai.common.core.PageResult;
import sparkai.service.service.interfaces.application.IApplicationService;
import sparkai.service.validate.application.ApplicationAddValidate;
import sparkai.service.validate.application.ApplicationSaveValidate;
import sparkai.service.vo.application.ApplicationListVo;
import sparkai.service.vo.application.ApplicationQueryVo;
import sparkai.service.vo.application.ApplicationVo;

@RequestMapping("/api/application")
@RestController
public class ApplicationController {

    @Autowired
    IApplicationService iApplicationService;

    /**
     * 应用列表
     */
    @GetMapping("/list")
    public AjaxResult<PageResult<ApplicationListVo>> list(ApplicationQueryVo queryVo) {

        return AjaxResult.success(iApplicationService.getApplicationList(queryVo));
    }

    /**
     * 添加应用
     */
    @PostMapping("/add")
    public AjaxResult<Object> add(@RequestBody @Validated ApplicationAddValidate validate) {

        return AjaxResult.success(iApplicationService.addApplication(validate));
    }

    /**
     * 获取应用详情
     */
    @GetMapping("/detail")
    public AjaxResult<ApplicationVo> applicationInfo(@RequestParam("appId") String appId) {

        return AjaxResult.success(iApplicationService.getApplicationInfo(appId));
    }

    /**
     * 保存应用设置
     */
    @PostMapping("/save")
    public AjaxResult<Object> save(@RequestBody @Validated ApplicationSaveValidate validate) {

        iApplicationService.saveApplication(validate);
        return AjaxResult.success();
    }

    /**
     * 应用内聊天测试
     */
    @PostMapping(value = "/testChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter testChat(@RequestBody ApplicationSaveValidate validate) {

        return iApplicationService.testChat(validate);
    }
}