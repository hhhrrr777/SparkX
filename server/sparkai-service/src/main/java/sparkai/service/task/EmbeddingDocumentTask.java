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

    /**
     * 向量化文本
     * @param documentId String
     */
    @Async
    public void executeAsyncTask(String documentId) {

        // 所属段落信息
        KnowledgeDocumentEntity documentInfo = knowledgeDocumentMapper.selectById(documentId);

        // 查询文档所属的段落
        List<KnowledgeParagraphEntity> paragraphEntityList = knowledgeParagraphMapper.selectList(new QueryWrapper<KnowledgeParagraphEntity>()
                .eq("document_id", documentId).eq("active", StatusEnum.YES.getCode()));

        if (!CollectionUtils.isEmpty(paragraphEntityList)) {
            // 默认的内存型的embedding模型
            EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

            // 删除已经向量化的数据
            knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>().eq("document_id", documentId));

            // 标记开始向量化
            KnowledgeDocumentEntity updateEntity = new KnowledgeDocumentEntity();
            updateEntity.setStatus(DocumentStatusEnum.RUNNING.getCode());
            updateEntity.setUpdateTime(Tool.nowDateTime());
            knowledgeDocumentMapper.update(updateEntity, new QueryWrapper<KnowledgeDocumentEntity>().eq("uuid", documentId));

            for (KnowledgeParagraphEntity paragraph : paragraphEntityList) {

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
                    embeddingEntity.setUuid(IdUtil.randomUUID());
                    embeddingEntity.setDatasetId(documentInfo.getDatasetId());
                    embeddingEntity.setDocumentId(documentId);
                    embeddingEntity.setParagraphId(paragraph.getUuid());
                    embeddingEntity.setEmbedding(embeddingModel.embed(content).content().vectorAsList()); // 向量化文本
                    embeddingEntity.setSearchVector(TsVectorGenerator.toTsVector(content)); // 全文检索文本
                    embeddingEntity.setActive(1);
                    embeddingEntity.setSourceType(SourceType.DOCUMENT.getCode()); // 来源文本
                    embeddingEntity.setSourceId(paragraph.getUuid()); // 来源id
                    embeddingEntity.setCreateTime(Tool.nowDateTime());

                    knowledgeEmbeddingMapper.insert(embeddingEntity);
                }
            }

            // 标记向量化完成
            String statusMeta = documentInfo.getStatusMeta();
            JSONObject jsonObject = JSONUtil.parseObj(statusMeta);
            jsonObject.put("embedding_time", Tool.nowDateTime());

            KnowledgeDocumentEntity finalUpdateEntity = new KnowledgeDocumentEntity();
            finalUpdateEntity.setStatus(DocumentStatusEnum.COMPLETE.getCode());
            finalUpdateEntity.setStatusMeta(statusMeta.toString());
            finalUpdateEntity.setUpdateTime(Tool.nowDateTime());
            knowledgeDocumentMapper.update(finalUpdateEntity, new QueryWrapper<KnowledgeDocumentEntity>().eq("uuid", documentId));
        }
    }
}