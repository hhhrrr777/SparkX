package sparkai.service.helper;

import dev.langchain4j.community.model.qianfan.QianfanStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import org.springframework.stereotype.Component;
import sparkai.service.validate.application.ApplicationSaveValidate;

@Component
public class StreamChatModelBuildHelper {

    /**
     * 构建流输出model
     * @param modelId String
     * @return StreamingChatLanguageModel
     */
    public StreamingChatLanguageModel build(String modelId) {

        return QianfanStreamingChatModel.builder()
                .apiKey("DYATIgV0vT2W118kz2spXAj3")
                .secretKey("NEVr9XhWa0T8WB3e9INUwYgjPUEXiFas")
                .modelName("ERNIE-Speed-128K")
                .build();
    }
}
