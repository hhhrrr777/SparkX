package sparkai.service.service.impl.dataset;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sparkai.common.core.PageResult;
import sparkai.common.enums.DocumentStatusEnum;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.dataset.KnowledgeDocumentEntity;
import sparkai.service.entity.dataset.KnowledgeParagraphEntity;
import sparkai.service.fileSplitter.FileHandleFactory;
import sparkai.service.fileSplitter.FileHandleInterface;
import sparkai.service.mapper.dataset.KnowledgeDocumentMapper;
import sparkai.service.mapper.dataset.KnowledgeParagraphMapper;
import sparkai.service.service.interfaces.dataset.IKnowledgeDocumentService;
import sparkai.service.task.EmbeddingDocumentTask;
import sparkai.service.vo.document.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class KnowledgeDocumentServiceImpl implements IKnowledgeDocumentService{

    @Autowired
    KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    KnowledgeParagraphMapper knowledgeParagraphMapper;

    @Autowired
    EmbeddingDocumentTask task;

    /**
     * 知识库下文档列表
     * @param queryVo DocumentQueryVo
     * @return PageResult<DocumentListVo>
     */
    @Override
    public PageResult<DocumentListVo> getDocumentList(DocumentQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<KnowledgeDocumentEntity> queryWrapper = new QueryWrapper<>();

        if (!queryVo.getName().isBlank()) {
            queryWrapper.like("name", queryVo.getName());
        }
        queryWrapper.eq("dataset_id", queryVo.getDatasetId());

        queryWrapper.orderByDesc("create_time");
        IPage<KnowledgeDocumentEntity> datasetListRes = knowledgeDocumentMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        List<DocumentListVo> datasetVoList = new LinkedList<>();
        for (KnowledgeDocumentEntity entity : datasetListRes.getRecords()) {
            DocumentListVo vo = new DocumentListVo();
            BeanUtils.copyProperties(entity, vo);

            // 分段数
            vo.setParagraphNum(entity.getParagraphNum());
            // 命中处理方式
            vo.setHitDealType(entity.getAnswerType());

            datasetVoList.add(vo);
        }

        return PageResult.iPageHandle(datasetListRes.getTotal(), pageNo, pageSize, datasetVoList);
    }

    /**
     * 预览文件
     * @param previewVo PreviewVo
     * @return List<DocumentSplitVo>
     */
    @Override
    public List<DocumentSplitVo> previewFile(PreviewVo previewVo) {

        try {

            List<DocumentSplitVo> splitList = new LinkedList<>();
            for (MultipartFile file : previewVo.getFiles()) {

                DocumentSplitVo vo = new DocumentSplitVo();
                // 文本标题
                String originalFilename = file.getOriginalFilename();
                vo.setName(originalFilename);

                byte[] bytes = file.getBytes(); // 获取文件的字节数组

                // 文本大小
                vo.setFileSize(file.getSize());

                FileHandleFactory fileHandleFactory = new FileHandleFactory();

                // 选择文件处理器
                String ext = originalFilename.split("\\.")[1];
                FileHandleInterface fileHandle = fileHandleFactory.getSplitter(ext);
                List<DocumentItemVo> itemListVo = fileHandle.handle(bytes, previewVo);

                vo.setContent(itemListVo);
                splitList.add(vo);
            }

            return splitList;

        } catch (IllegalStateException | IOException e) {
            throw new BusinessException("上传失败" + e.getMessage());
        }
    }

    /**
     * 保存文档
     * @param documentSaveVo DocumentSaveVo
     */
    @Override
    public void saveDocument(DocumentSaveVo documentSaveVo) {

        for (DocumentSplitVo document : documentSaveVo.getDocumentList()) {

            // 写入文档
            KnowledgeDocumentEntity knowledgeDocument = new KnowledgeDocumentEntity();
            knowledgeDocument.setName(document.getName());
            String documentId = IdUtil.simpleUUID();
            knowledgeDocument.setUuid(documentId);
            knowledgeDocument.setFileSize(document.getFileSize());
            knowledgeDocument.setStatus(1);
            knowledgeDocument.setQuestionStatus(1);
            knowledgeDocument.setActive(1);
            knowledgeDocument.setDatasetId(documentSaveVo.getDatasetId());
            knowledgeDocument.setParagraphNum(document.getContent().size());
            knowledgeDocument.setAnswerType("model");
            knowledgeDocument.setRedirectSimilar(0.900);
            knowledgeDocument.setCreateTime(Tool.nowDateTime());

            knowledgeDocumentMapper.insert(knowledgeDocument);

            // 写入段落
            for (DocumentItemVo content : document.getContent()) {

                KnowledgeParagraphEntity paragraph = new KnowledgeParagraphEntity();
                paragraph.setUuid(IdUtil.randomUUID());
                paragraph.setTitle(content.getTitle());
                paragraph.setContent(content.getContent());
                paragraph.setDatasetId(documentSaveVo.getDatasetId());
                paragraph.setDocumentId(documentId);
                paragraph.setStatus(DocumentStatusEnum.PENDING.getCode());
                paragraph.setActive(DocumentStatusEnum.PENDING.getCode());
                paragraph.setCreateTime(Tool.nowDateTime());

                knowledgeParagraphMapper.insert(paragraph);
            }
        }
    }

    /**
     * 向量化文本
     * @param documentId String
     */
    @Override
    public void doEmbedding(String documentId) {

        task.executeAsyncTask(documentId);
    }
}
