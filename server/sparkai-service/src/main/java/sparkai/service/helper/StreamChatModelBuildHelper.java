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

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.community.model.qianfan.QianfanStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import org.springframework.stereotype.Component;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.system.ModelsEntity;

@Component
public class StreamChatModelBuildHelper {

    /**
     * 构建流输出model
     * @param modelInfo ModelsEntity
     * @param applicationInfo ApplicationEntity
     * @return StreamingChatLanguageModel
     */
    public StreamingChatLanguageModel build(ModelsEntity modelInfo, ApplicationEntity applicationInfo) {

        // 百度千帆
        if (modelInfo.getModelFlag().equals("qianfan")) {
            return buildQianfan(modelInfo, applicationInfo);
        }

        return null;
    }

    /**
     * 构建千帆
     * @param modelInfo ModelsEntity
     * @param applicationInfo ApplicationEntity
     * @return StreamingChatLanguageModel
     */
    private StreamingChatLanguageModel buildQianfan(ModelsEntity modelInfo, ApplicationEntity applicationInfo) {

        JSONArray jsonConfig = JSONUtil.parseArray(modelInfo.getCredential());
        String key = jsonConfig.getJSONObject(0).getStr("value");
        String secret = jsonConfig.getJSONObject(1).getStr("value");

        return QianfanStreamingChatModel.builder()
                .apiKey(key)
                .secretKey(secret)
                .temperature(applicationInfo.getTemperature()) // 温度
                .modelName(applicationInfo.getModelName())
                .build();
    }
}
