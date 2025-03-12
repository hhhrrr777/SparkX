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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sparkai.common.core.PageResult;
import sparkai.common.enums.DocumentStatusEnum;
import sparkai.common.enums.StatusEnum;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.dataset.KnowledgeDocumentEntity;
import sparkai.service.entity.dataset.KnowledgeParagraphEntity;
import sparkai.service.fileSplitter.FileHandleFactory;
import sparkai.service.fileSplitter.FileHandleInterface;
import sparkai.service.mapper.dataset.KnowledgeDocumentMapper;
import sparkai.service.mapper.dataset.KnowledgeEmbeddingMapper;
import sparkai.service.mapper.dataset.KnowledgeParagraphMapper;
import sparkai.service.mapper.dataset.KnowledgeQuestionParagraphMapper;
import sparkai.service.service.interfaces.dataset.IKnowledgeDocumentService;
import sparkai.service.task.EmbeddingDocumentTask;
import sparkai.service.vo.document.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class KnowledgeDocumentServiceImpl implements IKnowledgeDocumentService{

    @Autowired
    KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    KnowledgeParagraphMapper knowledgeParagraphMapper;

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    @Autowired
    KnowledgeQuestionParagraphMapper knowledgeQuestionParagraphMapper;

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
            String documentId = IdUtil.randomUUID();
            knowledgeDocument.setDocumentId(documentId);
            knowledgeDocument.setFileSize(document.getFileSize());
            knowledgeDocument.setStatus(StatusEnum.YES.getCode());
            knowledgeDocument.setQuestionStatus(StatusEnum.YES.getCode());
            knowledgeDocument.setActive(StatusEnum.YES.getCode());
            knowledgeDocument.setDatasetId(documentSaveVo.getDatasetId());
            knowledgeDocument.setParagraphNum(document.getContent().size());
            knowledgeDocument.setAnswerType("model");
            knowledgeDocument.setRedirectSimilar(0.900);
            knowledgeDocument.setCreateTime(Tool.nowDateTime());

            knowledgeDocumentMapper.insert(knowledgeDocument);

            // 写入段落
            for (DocumentItemVo content : document.getContent()) {

                KnowledgeParagraphEntity paragraph = new KnowledgeParagraphEntity();
                paragraph.setParagraphId(IdUtil.randomUUID());
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
     * @param documentIds String
     */
    @Override
    public void doEmbedding(String documentIds) {

        String[] documentMap = documentIds.split(",");
        for (String documentId : documentMap) {
            // 检测应答模式
            KnowledgeDocumentEntity documentInfo = knowledgeDocumentMapper.selectById(documentId);
            if (documentInfo.getAnswerType().equals("model")) {

                // 标记开始向量化
                KnowledgeDocumentEntity updateEntity = knowledgeDocumentMapper.selectById(documentId);
                updateEntity.setStatus(DocumentStatusEnum.RUNNING.getCode());
                updateEntity.setUpdateTime(Tool.nowDateTime());
                knowledgeDocumentMapper.updateById(updateEntity);

                // 执行向量化
                task.executeAsyncTask(documentId);
            }
        }
    }

    /**
     * 设置模型
     * @param settingVo DocumentSettingVo
     */
    @Override
    public void setDocument(DocumentSettingVo settingVo) {

        String[] documentIds = settingVo.getDocumentIds().split(",");
        for (String documentId : documentIds) {

            KnowledgeDocumentEntity documentInfo = knowledgeDocumentMapper.selectById(documentId);
            documentInfo.setAnswerType(settingVo.getAnswerType());
            documentInfo.setRedirectSimilar(settingVo.getRedirectSimilar());

            knowledgeDocumentMapper.updateById(documentInfo);
        }
    }

    /**
     * 删除文档
     * @param documentIds String
     */
    @Override
    @Transactional
    public void delDocumentByIds(String documentIds) {

        List<String> documentIdsList = Collections.singletonList(documentIds);

        // 删除文档
        knowledgeDocumentMapper.deleteByIds(documentIdsList);
        // 删除文档段落
        knowledgeParagraphMapper.deleteByDocumentIds(documentIdsList);
        // 删除文档embedding数据
        knowledgeEmbeddingMapper.deleteByDocumentIds(documentIdsList);
        // 删除文档下问题数据
        knowledgeQuestionParagraphMapper.deleteByDocumentIds(documentIdsList);
    }
}
