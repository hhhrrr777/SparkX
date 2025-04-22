package sparkai.service.chat;

import dev.langchain4j.service.TokenStream;
import org.springframework.stereotype.Service;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.validate.application.ApplicationSaveValidate;

@Service
public class WorkflowChat implements IAIChat {


    @Override
    public TokenStream streamChat(ApplicationSaveValidate validate, ApplicationEntity applicationInfo) {

        // 查询流程数据

        return null;
    }
}
