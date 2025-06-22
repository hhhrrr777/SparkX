// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.extend;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;

import java.util.List;

public class SparkContentInjector extends DefaultContentInjector {

    /**
     * SparkAI重写注入方法
     * @param contents List<Content>
     * @param chatMessage ChatMessage
     * @return ChatMessage
     */
    public ChatMessage inject(List<Content> contents, ChatMessage chatMessage) {
        if (contents.isEmpty()) {
            return chatMessage;
        } else {
            Prompt prompt = this.createPrompt(chatMessage, contents);
            if (chatMessage instanceof UserMessage) {
                UserMessage message = (UserMessage)chatMessage;
                if (Utils.isNotNullOrBlank(message.name())) {
                    return prompt.toUserMessage(message.name());
                }
            }

            return prompt.toUserMessage();
        }
    }
}
