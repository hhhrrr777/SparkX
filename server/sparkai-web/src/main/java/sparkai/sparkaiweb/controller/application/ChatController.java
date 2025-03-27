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
import sparkai.service.service.interfaces.application.ISseChatService;
import sparkai.service.service.interfaces.dataset.IHitTestService;
import sparkai.service.vo.application.SseChatVo;

@RequestMapping("/api/chat")
@RestController
public class ChatController {

    @Autowired
    ISseChatService iSseChatService;

    /**
     * 流式聊天
     */
    @PostMapping(value = "/sseChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseChat(@RequestBody @Validated SseChatVo chatVo) {

        return iSseChatService.sseChat(chatVo);
    }
}