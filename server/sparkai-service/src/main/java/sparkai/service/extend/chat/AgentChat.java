// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.extend.chat;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.TokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.system.ModelsEntity;
import sparkai.service.helper.ApplicationHelper;
import sparkai.service.helper.AssistantBuildHelper;
import sparkai.service.helper.ChatModelBuildHelper;
import sparkai.service.helper.StreamChatModelBuildHelper;
import sparkai.service.mapper.system.ModelsMapper;
import sparkai.service.service.interfaces.application.IAiService;
import sparkai.service.validate.application.ApplicationChatValidate;

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

    @Autowired
    ApplicationHelper applicationHelper;

    /**
     * 普通模式聊天
     * @param applicationInfo ApplicationEntity
     * @param validate ApplicationSaveValidate
     * @return TokenStream
     */
    @Override
    public TokenStream streamChat(ApplicationEntity applicationInfo, ApplicationChatValidate validate) {

        // 获取模型信息
        ModelsEntity modelInfo = modelsMapper.selectById(applicationInfo.getModelId());

        // 查询关联的知识库信息
        validate.setDatasetList(applicationHelper.getRelationDatasetList(validate.getAppId()));
        // 查询关联的插件信息
        validate.setToolsList(applicationHelper.getRelationFullToolList(validate.getAppId()));

        // step 1 构建模型流式应答对象
        StreamingChatModel streamingChatModel = streamChatModelBuildHelper.build(modelInfo, applicationInfo);
        // step 2 构建模型普通对象，用于问题优化下使用
        ChatModel chatModel = chatModelBuildHelper.build(modelInfo, applicationInfo);
        // step 3 构建 IAiService
        IAiService assistant = assistantBuildHelper.build(applicationInfo, validate, streamingChatModel, chatModel);

        TokenStream tokenStream;
        if (applicationInfo.getPrompt().isBlank()) {
            tokenStream = assistant.chatInTokenStream(validate.getContent());
        } else {
            tokenStream = assistant.chatWithSystem(applicationInfo.getPrompt(), validate.getContent());
        }

        return tokenStream;
    }
}