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

import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.rag.content.injector.ContentInjector;
import lombok.extern.slf4j.Slf4j;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.content.Content;
import static dev.langchain4j.internal.Utils.copyIfNotNull;
import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.internal.Utils.isNotNullOrBlank;
import static dev.langchain4j.internal.Utils.isNullOrEmpty;
import static dev.langchain4j.internal.ValidationUtils.ensureNotEmpty;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SparkContentInjector implements ContentInjector {

    public static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = PromptTemplate.from(
            """
                    {{userMessage}}
                    
                    Answer using the following information:
                    {{contents}}"""
    );

    private final PromptTemplate promptTemplate;
    private final List<String> metadataKeysToInclude;

    public SparkContentInjector() {
        this(DEFAULT_PROMPT_TEMPLATE, null);
    }

    public SparkContentInjector(List<String> metadataKeysToInclude) {
        this(DEFAULT_PROMPT_TEMPLATE, ensureNotEmpty(metadataKeysToInclude, "metadataKeysToInclude"));
    }

    public SparkContentInjector(PromptTemplate promptTemplate) {
        this(ensureNotNull(promptTemplate, "promptTemplate"), null);
    }

    public SparkContentInjector(PromptTemplate promptTemplate, List<String> metadataKeysToInclude) {
        this.promptTemplate = getOrDefault(promptTemplate, DEFAULT_PROMPT_TEMPLATE);
        this.metadataKeysToInclude = copyIfNotNull(metadataKeysToInclude);
    }

    public static DefaultContentInjectorBuilder builder() {
        return new DefaultContentInjectorBuilder();
    }

    @Override
    public ChatMessage inject(List<Content> contents, ChatMessage chatMessage) {

        if (contents.isEmpty()) {
            return chatMessage;
        }

        Prompt prompt = createPrompt(chatMessage, contents);
        if (chatMessage instanceof UserMessage message && isNotNullOrBlank(message.name())) {
            return prompt.toUserMessage(message.name());
        }

        return prompt.toUserMessage();
    }

    protected Prompt createPrompt(ChatMessage chatMessage, List<Content> contents) {
        return createPrompt((UserMessage) chatMessage, contents);
    }

    /**
     * @deprecated use {@link #inject(List, ChatMessage)} instead.
     */
    @Override
    @Deprecated
    public UserMessage inject(List<Content> contents, UserMessage userMessage) {

        if (contents.isEmpty()) {
            return userMessage;
        }

        Prompt prompt = createPrompt(userMessage, contents);
        if (isNotNullOrBlank(userMessage.name())) {
            return prompt.toUserMessage(userMessage.name());
        }
        return prompt.toUserMessage();
    }

    /**
     * @deprecated implement/override {@link #createPrompt(ChatMessage, List)} instead.
     */
    @Deprecated
    protected Prompt createPrompt(UserMessage userMessage, List<Content> contents) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userMessage", ((TextContent)userMessage.contents().get(0)).text());
        variables.put("contents", format(contents));
        return promptTemplate.apply(variables);
    }

    protected String format(List<Content> contents) {
        return contents.stream()
                .map(this::format)
                .collect(joining("\n\n"));
    }

    protected String format(Content content) {

        TextSegment segment = content.textSegment();

        if (isNullOrEmpty(metadataKeysToInclude)) {
            return segment.text();
        }

        String segmentContent = segment.text();
        String segmentMetadata = format(segment.metadata());

        return format(segmentContent, segmentMetadata);
    }

    protected String format(Metadata metadata) {
        StringBuilder formattedMetadata = new StringBuilder();
        for (String metadataKey : metadataKeysToInclude) {
            String metadataValue = metadata.getString(metadataKey);
            if (metadataValue != null) {
                if (formattedMetadata.length() > 0) {
                    formattedMetadata.append("\n");
                }
                formattedMetadata.append(metadataKey).append(": ").append(metadataValue);
            }
        }
        return formattedMetadata.toString();
    }

    protected String format(String segmentContent, String segmentMetadata) {
        return segmentMetadata.isEmpty()
                ? segmentContent
                : String.format("content: %s\n%s", segmentContent, segmentMetadata);
    }

    public static class DefaultContentInjectorBuilder {
        private PromptTemplate promptTemplate;
        private List<String> metadataKeysToInclude;

        DefaultContentInjectorBuilder() {
        }

        public DefaultContentInjectorBuilder promptTemplate(PromptTemplate promptTemplate) {
            this.promptTemplate = promptTemplate;
            return this;
        }

        public DefaultContentInjectorBuilder metadataKeysToInclude(List<String> metadataKeysToInclude) {
            this.metadataKeysToInclude = metadataKeysToInclude;
            return this;
        }

        public SparkContentInjector build() {
            return new SparkContentInjector(this.promptTemplate, this.metadataKeysToInclude);
        }

        public String toString() {
            return "SparkContentInjector.DefaultContentInjectorBuilder(promptTemplate=" + this.promptTemplate + ", metadataKeysToInclude=" + this.metadataKeysToInclude + ")";
        }
    }
}
