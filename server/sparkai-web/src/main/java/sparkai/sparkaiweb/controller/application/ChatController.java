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
import org.springframework.web.bind.annotation.*;
import sparkai.common.core.AjaxResult;
import sparkai.service.service.interfaces.application.IApplicationChatService;
import sparkai.service.vo.application.ApplicationSimpleSessionVo;
import sparkai.service.vo.application.ApplicationVo;
import sparkai.service.vo.application.SessionVo;

import java.util.List;

@RequestMapping("/api/chat")
@RestController
public class ChatController {

    @Autowired
    IApplicationChatService iApplicationChatService;

    /**
     * 应用聊天详情
     */
    @GetMapping("/info")
    public AjaxResult<ApplicationVo> info(@RequestParam("appId") String appId) {

        return AjaxResult.success(iApplicationChatService.getChatInfo(appId));
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/sessionList")
    public AjaxResult<List<ApplicationSimpleSessionVo>> sessionList(@RequestParam("appId") String appId) {

        return AjaxResult.success(iApplicationChatService.getChatSesstionList(appId));
    }

    /**
     * 创建会话
     */
    @PostMapping("/createSession")
    public AjaxResult<Object> createSession(@RequestBody SessionVo sessionVo) {

        return AjaxResult.success(iApplicationChatService.createSession(sessionVo));
    }

    /**
     * 更新会话
     */
    @PostMapping("/updateSession")
    public AjaxResult<Object> updateSession(@RequestBody SessionVo sessionVo) {

        iApplicationChatService.updateSession(sessionVo);
        return AjaxResult.success();
    }
}