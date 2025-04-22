package sparkai.service.chat;

import dev.langchain4j.service.TokenStream;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.validate.application.ApplicationSaveValidate;

public interface IAIChat {

    /**
     * 流式聊天
     * @param validate ApplicationSaveValidate
     * @param applicationInfo ApplicationEntity
     * @return TokenStream
     */
    TokenStream streamChat(ApplicationSaveValidate validate, ApplicationEntity applicationInfo);
}
