package sparkai.service.task;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sparkai.common.enums.DocumentStatusEnum;
import sparkai.common.enums.SourceType;
import sparkai.common.enums.StatusEnum;
import sparkai.common.utils.MarkChunk;
import sparkai.common.utils.Tool;
import sparkai.common.utils.TsVectorGenerator;
import sparkai.service.entity.dataset.KnowledgeDocumentEntity;
import sparkai.service.entity.dataset.KnowledgeEmbeddingEntity;
import sparkai.service.entity.dataset.KnowledgeParagraphEntity;
import sparkai.service.mapper.dataset.KnowledgeDocumentMapper;
import sparkai.service.mapper.dataset.KnowledgeEmbeddingMapper;
import sparkai.service.mapper.dataset.KnowledgeParagraphMapper;

import java.util.LinkedList;
import java.util.List;

@Service
public class EmbeddingDocumentTask {

    @Autowired
    KnowledgeParagraphMapper knowledgeParagraphMapper;

    @Autowired
    KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    @Autowired
    MarkChunk markChunk;

    private EmbeddingModel embeddingModel;

    /**
     * 向量化文本
     * @param documentId String
     */
    @Async
    public void executeAsyncTask(String documentId) {

        // 查询文档所属的段落
        List<KnowledgeParagraphEntity> paragraphEntityList = knowledgeParagraphMapper.selectList(new QueryWrapper<KnowledgeParagraphEntity>()
                .eq("document_id", documentId).eq("active", StatusEnum.YES.getCode()));

        if (!CollectionUtils.isEmpty(paragraphEntityList)) {
            // 删除已经向量化的数据
            knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>().eq("document_id", documentId));

            // 标记开始向量化
            KnowledgeDocumentEntity updateEntity = knowledgeDocumentMapper.selectById(documentId);
            updateEntity.setStatus(DocumentStatusEnum.RUNNING.getCode());
            updateEntity.setUpdateTime(Tool.nowDateTime());
            knowledgeDocumentMapper.updateById(updateEntity);

            // 默认的内存型的embedding模型
            embeddingModel = new AllMiniLmL6V2EmbeddingModel();

            for (KnowledgeParagraphEntity paragraph : paragraphEntityList) {
                this.embeddingSingleParagraph(paragraph);
            }

            // 标记向量化完成
            KnowledgeDocumentEntity finalUpdateEntity = knowledgeDocumentMapper.selectById(documentId);
            finalUpdateEntity.setStatus(DocumentStatusEnum.COMPLETE.getCode());
            finalUpdateEntity.setEmbeddingTime(Tool.nowDateTime());
            finalUpdateEntity.setUpdateTime(Tool.nowDateTime());
            knowledgeDocumentMapper.updateById(finalUpdateEntity);
        }
    }

    /**
     * 向量化单个段落
     * @param paragraphId String
     */
    @Async
    public void executeAsyncParagraphTask(String paragraphId) {

        KnowledgeParagraphEntity paragraphInfo = knowledgeParagraphMapper.selectById(paragraphId);
        // 删除已经向量化的数据
        knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>().eq("paragraph_id", paragraphId));

        // 默认的内存型的embedding模型
        embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        this.embeddingSingleParagraph(paragraphInfo);
    }

    /**
     * 处理数据向量化
     * @param paragraph KnowledgeParagraphEntity
     */
    private void embeddingSingleParagraph(KnowledgeParagraphEntity paragraph) {

        // 拆分段落长度，防止截取256的长度，去进行向量化，有一些embedding模型要求的最大上下文是256
        String paragraphStr = paragraph.getTitle() + paragraph.getContent();
        List<String> subParagraph = new LinkedList<>();
        if (paragraphStr.length() > 256) {
            subParagraph = markChunk.handle(paragraphStr);
        } else {
            subParagraph.add(paragraphStr);
        }

        for (String content : subParagraph) {

            // 开始向量化，并入库
            KnowledgeEmbeddingEntity embeddingEntity = new KnowledgeEmbeddingEntity();
            embeddingEntity.setEmbeddingId(IdUtil.randomUUID());
            embeddingEntity.setDatasetId(paragraph.getDatasetId());
            embeddingEntity.setDocumentId(paragraph.getDocumentId());
            embeddingEntity.setParagraphId(paragraph.getParagraphId());
            embeddingEntity.setEmbedding(embeddingModel.embed(content).content().vectorAsList()); // 向量化文本
            embeddingEntity.setSearchVector(TsVectorGenerator.toTsVector(content)); // 全文检索文本
            embeddingEntity.setActive(StatusEnum.YES.getCode());
            embeddingEntity.setSourceType(SourceType.DOCUMENT.getCode()); // 来源文本
            embeddingEntity.setSourceId(paragraph.getParagraphId()); // 来源id
            embeddingEntity.setCreateTime(Tool.nowDateTime());

            knowledgeEmbeddingMapper.insert(embeddingEntity);
        }

        // TODO 段落关联的问题，也得重新索引
    }
}