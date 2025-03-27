// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.impl.application;

import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.service.helper.SseEmitterHelper;
import sparkai.service.helper.StreamChatModelBuildHelper;
import sparkai.service.service.interfaces.application.IAiService;
import sparkai.service.service.interfaces.application.ISseChatService;
import sparkai.service.vo.application.SseChatVo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseChatServiceImpl implements ISseChatService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Autowired
    StreamChatModelBuildHelper streamChatModelBuildHelper;

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    /**
     * 流式与ai交互
     * @param chatVo SseChatVo
     * @return SseEmitter
     */
    @Override
    public SseEmitter sseChat(SseChatVo chatVo) {

        SseEmitter emitter = new SseEmitter();

        String word = chatVo.getContent();
        // todo modelId
        StreamingChatLanguageModel model = streamChatModelBuildHelper.build("");
        IAiService assistant = AiServices.create(IAiService.class, model);
        TokenStream tokenStream = assistant.chatInTokenStream(word);

        // 执行异步发送
        sseEmitterHelper.asyncSend2Client(tokenStream, emitter);

        return emitter;
    }
}