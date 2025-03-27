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

import dev.langchain4j.community.model.qianfan.QianfanChatModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparkai.service.extend.SparkEmbeddingStoreContentRetriever;
import sparkai.service.service.interfaces.application.IAiService;
import sparkai.service.service.interfaces.dataset.IHitTestService;
import sparkai.service.validate.application.ApplicationSaveValidate;
import sparkai.service.vo.dataset.DatasetSimpleVo;
import sparkai.service.vo.dataset.HitTestVo;

@Component
public class AssistantBuildHelper {

    @Autowired
    IHitTestService iHitTestService;

    /**
     * 构建 assistant
     * @param validate ApplicationSaveValidate
     * @param streamingChatLanguageModel StreamingChatLanguageModel
     * @return IAiService
     */
    public IAiService build(ApplicationSaveValidate validate, StreamingChatLanguageModel streamingChatLanguageModel) {

        // 未关联知识库
        if (validate.getDatasetList().isEmpty()) {

            return AiServices.builder(IAiService.class)
                    .streamingChatLanguageModel(streamingChatLanguageModel)
                    .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(validate.getMemoryNum())) // 聊天上下文
                    .build();
        }

        // TODO 空召回策略

        // 关联了知识库
        QueryTransformer queryTransformer = null;

        QianfanChatModel chatModel = QianfanChatModel.builder()
                .apiKey("DYATIgV0vT2W118kz2spXAj3")
                .secretKey("NEVr9XhWa0T8WB3e9INUwYgjPUEXiFas")
                .modelName("ERNIE-Speed-128K")
                .build();

        // 开启问题优化
        if (validate.getCompressingQuery().equals(1)) {
            queryTransformer = new CompressingQueryTransformer(chatModel);
        }

        // embedding模型
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        // 构建交互数据
        HitTestVo searchDataVo = new HitTestVo();
        searchDataVo.setType(validate.getSearchMode());
        String[] datasetIds = validate.getDatasetList().stream().map(DatasetSimpleVo::getDatasetId).toArray(String[]::new);
        searchDataVo.setDatasetIds(String.join(",", datasetIds));

        // 内容检索
        ContentRetriever contentRetriever = SparkEmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .searchService(iHitTestService)
                .searchDataVo(searchDataVo)
                .maxResults(validate.getTopRank()) // 召回条数
                .minScore(validate.getSimilarity()) // 相似度
                .build();

        // 检索增强
        RetrievalAugmentor retrievalAugmentor;
        if (queryTransformer != null) {
            retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                    .queryTransformer(queryTransformer) // 问题压缩
                    .contentRetriever(contentRetriever) // 内容检索
                    .build();
        } else {
            retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                    .contentRetriever(contentRetriever) // 内容检索
                    .build();
        }

        return AiServices.builder(IAiService.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(validate.getMemoryNum())) // 聊天上下文
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }
}
