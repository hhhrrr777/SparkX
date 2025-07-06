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
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.dataset.KnowledgeDatasetEntity;
import sparkai.service.entity.tool.ToolsEntity;
import sparkai.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.extend.SparkEmbeddingStoreContentRetriever;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.mapper.dataset.KnowledgeDatasetMapper;
import sparkai.service.service.interfaces.application.IAiService;
import sparkai.service.service.interfaces.dataset.IDatasetSearchService;
import sparkai.service.validate.application.ApplicationChatValidate;
import sparkai.service.vo.dataset.DatasetSearchVo;
import sparkai.service.vo.dataset.DatasetSimpleVo;

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
     * @param streamingModel StreamingChatModel
     * @param chatModel ChatModel
     * @return IAiService
     */
    public IAiService build(ApplicationEntity applicationInfo, ApplicationChatValidate validate,
                            StreamingChatModel streamingModel, ChatModel chatModel) {

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

        // 构建服务
        AiServices<IAiService> builder =
                        AiServices.builder(IAiService.class)
                        .streamingChatModel(streamingModel)
                        .chatMemoryProvider(chatMemoryProvider);
        // 未关联知识库
        if (validate.getDatasetList().isEmpty()) {

            // 检测是否使用了插件
            if (!CollectionUtils.isEmpty(validate.getToolsList())) {
                return buildToolAiService(validate, streamingModel, chatMemoryProvider, null);
            }

            return builder.build();
        }

        // TODO 空召回策略

        // 关联了知识库
        QueryTransformer queryTransformer = null;
        // 开启问题优化
        if (applicationInfo.getCompressingQuery().equals(1)) {
            queryTransformer = new CompressingQueryTransformer(chatModel);
        }

        // 构建交互数据
        DatasetSearchVo searchDataVo = new DatasetSearchVo();
        searchDataVo.setType(applicationInfo.getSearchMode());
        String[] datasetIds = validate.getDatasetList().stream().map(DatasetSimpleVo::getDatasetId).toArray(String[]::new);
        searchDataVo.setDatasetIds(String.join(",", datasetIds));

        // 取第一条知识库的embedding模型当做全应用的embedding模型
        KnowledgeDatasetEntity datasetInfo = knowledgeDatasetMapper.selectById(datasetIds[0]);
        // embedding模型
        EmbeddingModel embeddingModel = embeddingModelBuildHelper.build(datasetInfo);

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
                    .build();
        } else {
            retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                    .contentRetriever(contentRetriever) // 内容检索
                    .build();
        }

        // 检测是否使用了插件
        if (!CollectionUtils.isEmpty(validate.getToolsList())) {
            return buildToolAiService(validate, streamingModel, chatMemoryProvider, retrievalAugmentor);
        }

        return builder
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }

    /**
     * 构建ai调用服务
     * @param validate ApplicationChatValidate
     * @param streamingModel StreamingChatModel
     * @param chatMemoryProvider ChatMemoryProvider
     * @param retrievalAugmentor RetrievalAugmentor
     * @return IAiService
     */
    private IAiService buildToolAiService(ApplicationChatValidate validate,
                                          StreamingChatModel streamingModel,
                                          ChatMemoryProvider chatMemoryProvider, RetrievalAugmentor retrievalAugmentor) {

        // 插件执行器
        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {

            return "";
        };

        // 构建插件
        ToolProvider toolProvider = (toolProviderRequest) -> {

            ToolProviderResult.Builder builder = ToolProviderResult.builder();

            for (ToolsEntity entity : validate.getToolsList()) {

                ToolSpecification.Builder specificationBuilder = ToolSpecification.builder();
                specificationBuilder.name(entity.getName()); // 方法标识
                specificationBuilder.description(entity.getDescription()); // 方法描述
                // 构建字段

                ToolSpecification toolSpecification = specificationBuilder.build();
                builder.add(toolSpecification, toolExecutor);
            }

            return builder.build();
        };

        // 构建服务
        AiServices<IAiService> builder =
                AiServices.builder(IAiService.class)
                .streamingChatModel(streamingModel)
                .chatMemoryProvider(chatMemoryProvider)
                .toolProvider(toolProvider);

        if (retrievalAugmentor == null) {
            return builder.build();
        }

        return builder
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }
}
