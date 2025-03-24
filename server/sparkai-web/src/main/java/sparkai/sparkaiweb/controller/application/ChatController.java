package sparkai.sparkaiweb.controller.application;

import dev.langchain4j.community.model.qianfan.QianfanChatModel;
import dev.langchain4j.community.model.qianfan.QianfanStreamingChatModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.service.extend.SparkEmbeddingStoreContentRetriever;
import sparkai.service.service.interfaces.application.IAiService;
import sparkai.service.service.interfaces.application.ISseChatService;
import sparkai.service.service.interfaces.dataset.IHitTestService;
import sparkai.service.vo.application.SseChatVo;
import sparkai.service.vo.dataset.HitTestVo;

@RequestMapping("/api/chat")
@RestController
public class ChatController {

    @Autowired
    ISseChatService iSseChatService;

    @Autowired
    IHitTestService iHitTestService;

    /**
     * 流式聊天
     */
    @PostMapping(value = "/sseChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseChat(@RequestBody @Validated SseChatVo chatVo) {

        return iSseChatService.sseChat(chatVo);
    }

    @GetMapping("/test")
    public void test() {

        QianfanStreamingChatModel qianfanStreamingChatModel = QianfanStreamingChatModel.builder()
                .apiKey("DYATIgV0vT2W118kz2spXAj3")
                .secretKey("NEVr9XhWa0T8WB3e9INUwYgjPUEXiFas")
                .modelName("ERNIE-Speed-128K")
                .build();

        QianfanChatModel chatModel = QianfanChatModel.builder()
                .apiKey("DYATIgV0vT2W118kz2spXAj3")
                .secretKey("NEVr9XhWa0T8WB3e9INUwYgjPUEXiFas")
                .modelName("ERNIE-Speed-128K")
                .build();

        // 问题压缩
        QueryTransformer queryTransformer = new CompressingQueryTransformer(chatModel);

        // 内容召回
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()
                .host("127.0.0.1")
                .port(6432)
                .database("sparkai")
                .user("sparkai")
                .password("123123")
                .table("knowledge_embedding")
                .dimension(384)
                .build();

        HitTestVo searchDataVo = new HitTestVo();
        searchDataVo.setType("embedding");
        searchDataVo.setDatasetIds("b961cf24-d25b-49f9-8b07-da38d6786482");

        // 内容检索
        ContentRetriever contentRetriever = SparkEmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .searchService(iHitTestService)
                .searchDataVo(searchDataVo)
                .maxResults(2)
                .minScore(0.6)
                .build();

        // 检索增强
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer) // 问题压缩
                .contentRetriever(contentRetriever) // 内容检索
                .build();

        IAiService assistant = AiServices.builder(IAiService.class)
                .streamingChatLanguageModel(qianfanStreamingChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(3)) // 聊天上下文
                .retrievalAugmentor(retrievalAugmentor)
                .build();

        TokenStream tokenStream = assistant.chatInTokenStream("叶凡是谁"); // assistant.chatWithSystem(systemMessage, prompt.toUserMessage().toString());
        tokenStream.onPartialResponse(System.out::println)
                .onError(Throwable::printStackTrace)
                .start();
    }
}