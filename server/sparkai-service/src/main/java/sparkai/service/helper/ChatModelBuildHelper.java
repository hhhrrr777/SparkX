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
import dev.langchain4j.community.model.qianfan.QianfanChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.system.ModelsEntity;

import java.time.Duration;

@Component
public class ChatModelBuildHelper {

    private ModelsEntity modelInfo;

    private ApplicationEntity applicationInfo;

    /**
     * 构建model
     * @param modelInfo ModelsEntity
     * @param applicationInfo ApplicationEntity
     * @return ChatModel
     */
    public ChatModel build(ModelsEntity modelInfo, ApplicationEntity applicationInfo) {

        this.modelInfo = modelInfo;
        this.applicationInfo = applicationInfo;

        return switch (modelInfo.getModelFlag()) {
            // 百度千帆
            case "qianfan" -> buildQianfan();
            // 清华智普
            case "zhipu" -> buildZhiPu();
            // 千问、豆包、GPT
            case "qwen", "doubao", "gpt" -> buildOpenAI();
            default -> null;
        };
    }

    /**
     * 构建千帆
     * @return ChatModel
     */
    private ChatModel buildQianfan() {

        JSONArray jsonConfig = JSONUtil.parseArray(modelInfo.getCredential());
        String key = jsonConfig.getJSONObject(0).getStr("value");
        String secret = jsonConfig.getJSONObject(1).getStr("value");

        Integer maxOutputTokens = applicationInfo.getMaxReplyToken() == null ? 4096 : applicationInfo.getMaxReplyToken();

        return QianfanChatModel.builder()
                .apiKey(key)
                .secretKey(secret)
                .temperature(applicationInfo.getTemperature()) // 温度
                .maxOutputTokens(maxOutputTokens)
                .modelName(applicationInfo.getModelName())
                .build();
    }

    /**
     * 构建智普
     * @return StreamingChatModel
     */
    private ChatModel buildZhiPu() {

        JSONArray jsonConfig = JSONUtil.parseArray(modelInfo.getCredential());
        String key = jsonConfig.getJSONObject(0).getStr("value");

        return ZhipuAiChatModel.builder()
                .apiKey(key)
                .temperature(applicationInfo.getTemperature()) // 温度
                .model(applicationInfo.getModelName())
                .connectTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .build();
    }

    /**
     * 通过标准openai结构构建对象
     * @return ChatModel
     */
    private ChatModel buildOpenAI() {

        JSONArray jsonConfig = JSONUtil.parseArray(modelInfo.getCredential());
        String key = jsonConfig.getJSONObject(0).getStr("value");

        JSONArray jsonOptions = JSONUtil.parseArray(modelInfo.getOptions());
        String url = jsonOptions.getJSONObject(1).getStr("value");

        return OpenAiChatModel.builder()
                .baseUrl(url)
                .apiKey(key)
                .modelName(applicationInfo.getModelName())
                .build();
    }
}
