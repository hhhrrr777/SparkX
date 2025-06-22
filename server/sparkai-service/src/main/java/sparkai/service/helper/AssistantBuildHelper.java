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

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.dataset.KnowledgeDatasetEntity;
import sparkai.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.extend.SparkContentInjector;
import sparkai.service.extend.SparkEmbeddingStoreContentRetriever;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.mapper.dataset.KnowledgeDatasetMapper;
import sparkai.service.service.interfaces.application.IAiService;
import sparkai.service.service.interfaces.dataset.IDatasetSearchService;
import sparkai.service.validate.application.ApplicationChatValidate;
import sparkai.service.vo.dataset.DatasetSimpleVo;
import sparkai.service.vo.dataset.DatasetSearchVo;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

@Component
@Slf4j
public class AssistantBuildHelper {

    @Autowired
    IDatasetSearchService iDatasetSearchService;

    @Autowired
    EmbeddingModelBuildHelper embeddingModelBuildHelper;

    @Autowired
    KnowledgeDatasetMapper knowledgeDatasetMapper;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    MemoryBuildHelper memoryBuildHelper;

    /**
     * 构建 assistant
     * @param validate ApplicationSaveValidate
     * @param streamingChatLanguageModel StreamingChatLanguageModel
     * @param chatLanguageModel ChatLanguageModel
     * @return IAiService
     */
    public IAiService build(ApplicationEntity applicationInfo, ApplicationChatValidate validate,
                            StreamingChatLanguageModel streamingChatLanguageModel, ChatLanguageModel chatLanguageModel) {

        // 自定义构建上下文记忆
        String memoryKey = validate.getSessionId() + applicationInfo.getUserId() + validate.getCell();
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryKey)
                .maxMessages(applicationInfo.getMemoryNum())
                .chatMemoryStore(memoryBuildHelper)
                .build();

        // 更新上下文记忆
        if (validate.getContextId() != 0) {
            ApplicationWorkflowRuntimeContextEntity runtimeContextEntity
                    = applicationWorkflowRuntimeContextMapper.selectById(validate.getContextId());
            JSONObject outputData = JSONUtil.parseObj(runtimeContextEntity.getOutputData());
            outputData.set("log.context", messagesToJson(memoryBuildHelper.getMessages(memoryKey)));
            runtimeContextEntity.setOutputData(outputData.toString());
            applicationWorkflowRuntimeContextMapper.updateById(runtimeContextEntity);
        }

        // 未关联知识库
        if (validate.getDatasetList().isEmpty()) {

            return AiServices.builder(IAiService.class)
                    .streamingChatLanguageModel(streamingChatLanguageModel)
                    .chatMemoryProvider(chatMemoryProvider) // 聊天上下文
                    .build();
        }

        // TODO 空召回策略

        // 关联了知识库
        QueryTransformer queryTransformer = null;
        // 开启问题优化
        if (applicationInfo.getCompressingQuery().equals(1)) {
            queryTransformer = new CompressingQueryTransformer(chatLanguageModel);
        }

        // 构建交互数据
        DatasetSearchVo searchDataVo = new DatasetSearchVo();
        searchDataVo.setType(applicationInfo.getSearchMode());
        String[] datasetIds = validate.getDatasetList().stream().map(DatasetSimpleVo::getDatasetId).toArray(String[]::new);
        searchDataVo.setDatasetIds(String.join(",", datasetIds));

        // 取第一条知识库的embedding模型当做全应用的embedding模型
        KnowledgeDatasetEntity datasetInfo = knowledgeDatasetMapper.selectById(datasetIds[0]);
        // embedding模型
        EmbeddingModel embeddingModel = embeddingModelBuildHelper.build(datasetInfo.getEmbeddingModeId());

        // 内容检索
        ContentRetriever contentRetriever = SparkEmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .searchService(iDatasetSearchService)
                .searchDataVo(searchDataVo)
                .maxResults(applicationInfo.getTopRank()) // 召回条数
                .minScore(applicationInfo.getSimilarity().doubleValue()) // 相似度
                .build();

        // 检索增强
        RetrievalAugmentor retrievalAugmentor;
        if (queryTransformer != null) {
            retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                    .queryTransformer(queryTransformer) // 问题压缩
                    .contentRetriever(contentRetriever) // 内容检索
                    .contentInjector(SparkContentInjector.builder()
                            .promptTemplate(PromptTemplate.from("{{userMessage}}\n{{contents}}"))
                            .build()) // 内容注入
                    .build();
        } else {
            retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                    .contentRetriever(contentRetriever) // 内容检索
                    .contentInjector(SparkContentInjector.builder()
                            .promptTemplate(PromptTemplate.from("{{userMessage}}\n{{contents}}"))
                            .build()) // 内容注入
                    .build();
        }

        return AiServices.builder(IAiService.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider) // 聊天上下文
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }
}
