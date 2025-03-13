package sparkai.service.task;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sparkai.common.enums.SourceType;
import sparkai.common.enums.StatusEnum;
import sparkai.common.utils.Tool;
import sparkai.common.utils.TsVectorGenerator;
import sparkai.service.entity.dataset.KnowledgeEmbeddingEntity;
import sparkai.service.entity.dataset.KnowledgeQuestionEntity;
import sparkai.service.entity.dataset.KnowledgeQuestionParagraphEntity;
import sparkai.service.mapper.dataset.KnowledgeEmbeddingMapper;
import sparkai.service.mapper.dataset.KnowledgeQuestionMapper;
import sparkai.service.mapper.dataset.KnowledgeQuestionParagraphMapper;

@Component
public class EmbeddingQuestionTask {

    @Autowired
    KnowledgeQuestionParagraphMapper knowledgeQuestionParagraphMapper;

    @Autowired
    KnowledgeQuestionMapper knowledgeQuestionMapper;

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    /**
     * 向量化 问题-段落
     * @param questionId String 问题id
     * @param paragraphId String 段落id
     * @param documentId String 文档id
     */
    @Async
    public void executeAsyncTask(String questionId, String paragraphId, String documentId) {

        // 默认的内存型的embedding模型
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        KnowledgeQuestionEntity questionInfo = knowledgeQuestionMapper.selectById(questionId);
        if (questionInfo != null) {

            // 删除旧的关联
            knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>()
                    .eq("source_type", SourceType.QUESTION.getCode())
                    .eq("question_id", questionId)
                    .eq("paragraph_id", paragraphId));

            // 开始向量化，并入库
            KnowledgeEmbeddingEntity embeddingEntity = new KnowledgeEmbeddingEntity();
            embeddingEntity.setEmbeddingId(IdUtil.randomUUID());
            embeddingEntity.setDatasetId(questionInfo.getDatasetId());
            embeddingEntity.setDocumentId(documentId);
            embeddingEntity.setParagraphId(paragraphId);
            embeddingEntity.setEmbedding(embeddingModel.embed(questionInfo.getContent()).content().vectorAsList()); // 向量化文本
            embeddingEntity.setSearchVector(TsVectorGenerator.toTsVector(questionInfo.getContent())); // 全文检索文本
            embeddingEntity.setActive(StatusEnum.YES.getCode());
            embeddingEntity.setSourceType(SourceType.QUESTION.getCode()); // 来源问题
            embeddingEntity.setSourceId(questionId); // 来源id
            embeddingEntity.setCreateTime(Tool.nowDateTime());

            knowledgeEmbeddingMapper.insert(embeddingEntity);
        }
    }
}
