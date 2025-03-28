// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.helper;

import dev.langchain4j.community.model.qianfan.QianfanStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import org.springframework.stereotype.Component;

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
