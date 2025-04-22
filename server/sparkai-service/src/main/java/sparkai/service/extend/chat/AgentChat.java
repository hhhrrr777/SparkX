package sparkai.service.extend.chat;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.TokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.system.ModelsEntity;
import sparkai.service.helper.AssistantBuildHelper;
import sparkai.service.helper.ChatModelBuildHelper;
import sparkai.service.helper.StreamChatModelBuildHelper;
import sparkai.service.mapper.system.ModelsMapper;
import sparkai.service.service.interfaces.application.IAiService;
import sparkai.service.validate.application.ApplicationSaveValidate;

@Component
public class AgentChat implements IChat {

    @Autowired
    ModelsMapper modelsMapper;

    @Autowired
    AssistantBuildHelper assistantBuildHelper;

    @Autowired
    StreamChatModelBuildHelper streamChatModelBuildHelper;

    @Autowired
    ChatModelBuildHelper chatModelBuildHelper;

    /**
     * 普通模式聊天
     * @param applicationInfo ApplicationEntity
     * @param validate ApplicationSaveValidate
     * @return TokenStream
     */
    @Override
    public TokenStream streamChat(ApplicationEntity applicationInfo, ApplicationSaveValidate validate) {

        // 获取模型信息
        ModelsEntity modelInfo = modelsMapper.selectById(validate.getModelId());

        // step 1 构建模型流式应答对象
        StreamingChatLanguageModel streamingChatModel = streamChatModelBuildHelper.build(modelInfo, applicationInfo);
        // step 2 构建模型普通对象，用于问题优化下使用
        ChatLanguageModel chatLanguageModel = chatModelBuildHelper.build(modelInfo, applicationInfo);
        // step 3 构建 IAiService
        IAiService assistant = assistantBuildHelper.build(validate, streamingChatModel, chatLanguageModel);

        TokenStream tokenStream;
        if (validate.getPrompt().isBlank()) {
            tokenStream = assistant.chatInTokenStream(validate.getContent());
        } else {
            tokenStream = assistant.chatWithSystem(validate.getPrompt(), validate.getContent());
        }

        return tokenStream;
    }
}