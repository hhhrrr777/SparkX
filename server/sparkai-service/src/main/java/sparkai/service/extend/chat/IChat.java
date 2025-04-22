package sparkai.service.extend.chat;

import dev.langchain4j.service.TokenStream;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.validate.application.ApplicationSaveValidate;

public interface IChat {

    TokenStream streamChat(ApplicationEntity applicationInfo, ApplicationSaveValidate validate);
}
