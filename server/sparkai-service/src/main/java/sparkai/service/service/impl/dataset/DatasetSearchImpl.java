// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.impl.dataset;

import cn.hutool.json.JSONUtil;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.TsVectorGenerator;
import sparkai.service.entity.dataset.KnowledgeDatasetEntity;
import sparkai.service.entity.dataset.KnowledgeDocumentEntity;
import sparkai.service.entity.dataset.KnowledgeParagraphEntity;
import sparkai.service.helper.EmbeddingModelBuildHelper;
import sparkai.service.mapper.dataset.KnowledgeDatasetMapper;
import sparkai.service.mapper.dataset.KnowledgeDocumentMapper;
import sparkai.service.mapper.dataset.KnowledgeEmbeddingMapper;
import sparkai.service.mapper.dataset.KnowledgeParagraphMapper;
import sparkai.service.service.interfaces.dataset.IDatasetSearchService;
import sparkai.service.vo.dataset.HitTestVo;
import sparkai.service.vo.dataset.SearchVo;

import java.util.*;

@Service
public class DatasetSearchImpl implements IDatasetSearchService {

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    @Autowired
    KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    KnowledgeParagraphMapper knowledgeParagraphMapper;

    @Autowired
    KnowledgeDatasetMapper knowledgeDatasetMapper;

    @Autowired
    EmbeddingModelBuildHelper embeddingModelBuildHelper;

    /**
     * 命中测试
     * @param hitTestVo HitTestVo
     */
    @Override
    public List<SearchVo> search(HitTestVo hitTestVo) {

        if (hitTestVo.getKeyword().isBlank()) {
            throw new BusinessException("输入的问题不能为空");
        }

        if (hitTestVo.getSimilarity() < 0) {
            throw new BusinessException("设信度应该大于0");
        }

        if (hitTestVo.getTopRank() < 1) {
            throw new BusinessException("召回数应该大于1");
        }

        List<SearchVo> searchRes = new LinkedList<>();

        // 取对应的embedding模型
        String datasetId = hitTestVo.getDatasetIds().split(",")[0];
        KnowledgeDatasetEntity datasetInfo = knowledgeDatasetMapper.selectById(datasetId);
        EmbeddingModel embeddingModel = embeddingModelBuildHelper.build(datasetInfo.getEmbeddingModeId());
        if (hitTestVo.getType().equals("embedding")) {

            List<Float> vector = embeddingModel.embed(hitTestVo.getKeyword()).content().vectorAsList();
            searchRes = embeddingSearch(hitTestVo, vector);
        } else if (hitTestVo.getType().equals("text")) {

            searchRes = textSearch(hitTestVo);
        } else if (hitTestVo.getType().equals("mix")) {

            List<Float> vector = embeddingModel.embed(hitTestVo.getKeyword()).content().vectorAsList();
            searchRes = mixSearch(hitTestVo, vector);
        }

        return searchRes;
    }

    /**
     * 向量检索
     * @param hitTestVo HitTestVo
     * @param vector List<Float>
     * @return List<SearchVo>
     */
    private List<SearchVo> embeddingSearch(HitTestVo hitTestVo, List<Float> vector) {

        List<String> datasetIds = Arrays.stream(hitTestVo.getDatasetIds().split(",")).toList();
        List<SearchVo> searchRes = knowledgeEmbeddingMapper.embeddingSearch(JSONUtil.toJsonStr(vector), datasetIds,
                hitTestVo.getSimilarity(), hitTestVo.getTopRank());

        if (!CollectionUtils.isEmpty(searchRes)) {
            return buildFinalRes(searchRes);
        }

        return searchRes;
    }

    /**
     * 全文检索
     * @param hitTestVo HitTestVo
     * @return List<SearchVo>
     */
    private List<SearchVo> textSearch(HitTestVo hitTestVo) {

        String searchKeywords = TsVectorGenerator.toTsQuery(hitTestVo.getKeyword());
        List<String> datasetIds = Arrays.stream(hitTestVo.getDatasetIds().split(",")).toList();
        List<SearchVo> searchRes = knowledgeEmbeddingMapper.textSearch(searchKeywords, datasetIds, hitTestVo.getSimilarity(),
                hitTestVo.getTopRank());

        if (!CollectionUtils.isEmpty(searchRes)) {
            return buildFinalRes(searchRes);
        }

        return searchRes;
    }

    /**
     * 混合检索
     * @param hitTestVo HitTestVo
     * @param vector List<Float>
     * @return List<SearchVo>
     */
    private List<SearchVo> mixSearch(HitTestVo hitTestVo, List<Float> vector) {

        List<String> datasetIds = Arrays.stream(hitTestVo.getDatasetIds().split(",")).toList();
        String searchKeywords = TsVectorGenerator.toTsQuery(hitTestVo.getKeyword());
        List<SearchVo> searchRes = knowledgeEmbeddingMapper.mixSearch(JSONUtil.toJsonStr(vector), searchKeywords, datasetIds,
                hitTestVo.getSimilarity(), hitTestVo.getTopRank());

        if (!CollectionUtils.isEmpty(searchRes)) {
            return buildFinalRes(searchRes);
        }

        return searchRes;
    }

    /**
     * 构建最终的信息
     * @param searchRes List<SearchVo>
     * @return List<SearchVo>
     */
    private List<SearchVo> buildFinalRes(List<SearchVo> searchRes) {
        // 所有的文档
        List<String> documentIds = searchRes.stream().map(SearchVo::getDocumentId).toList();
        // 所有的段落
        List<String> paragraphIds = searchRes.stream().map(SearchVo::getParagraphId).toList();

        List<KnowledgeDocumentEntity> documentList = knowledgeDocumentMapper.selectByIds(documentIds);
        Map<String, String> documentId2Name = new HashMap<>();
        for (KnowledgeDocumentEntity knowledgeDocumentEntity : documentList) {
            documentId2Name.put(knowledgeDocumentEntity.getDocumentId(), knowledgeDocumentEntity.getName());
        }

        List<KnowledgeParagraphEntity> paragraphList = knowledgeParagraphMapper.selectByIds(paragraphIds);
        Map<String, KnowledgeParagraphEntity> paragraphId2Info = new HashMap<>();
        for (KnowledgeParagraphEntity knowledgeParagraphEntity : paragraphList) {
            paragraphId2Info.put(knowledgeParagraphEntity.getParagraphId(), knowledgeParagraphEntity);
        }

        for (SearchVo searchVo : searchRes) {
            // 文档标题
            searchVo.setDocumentName(documentId2Name.get(searchVo.getDocumentId()));
            // 段落内容
            searchVo.setTitle(paragraphId2Info.get(searchVo.getParagraphId()).getTitle());
            searchVo.setContent(paragraphId2Info.get(searchVo.getParagraphId()).getContent());
        }

        return searchRes;
    }
}
