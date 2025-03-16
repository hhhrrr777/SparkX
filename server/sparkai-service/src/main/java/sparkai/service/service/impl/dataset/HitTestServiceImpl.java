package sparkai.service.service.impl.dataset;

import cn.hutool.json.JSONUtil;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sparkai.common.utils.TsVectorGenerator;
import sparkai.service.entity.dataset.KnowledgeDocumentEntity;
import sparkai.service.entity.dataset.KnowledgeParagraphEntity;
import sparkai.service.mapper.dataset.KnowledgeDocumentMapper;
import sparkai.service.mapper.dataset.KnowledgeEmbeddingMapper;
import sparkai.service.mapper.dataset.KnowledgeParagraphMapper;
import sparkai.service.service.interfaces.dataset.IHitTestService;
import sparkai.service.vo.dataset.HitTestVo;
import sparkai.service.vo.dataset.SearchVo;

import java.util.*;

@Service
public class HitTestServiceImpl implements IHitTestService {

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    @Autowired
    KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    KnowledgeParagraphMapper knowledgeParagraphMapper;

    /**
     * 命中测试
     * @param hitTestVo HitTestVo
     */
    @Override
    public List<SearchVo> search(HitTestVo hitTestVo) {

        List<SearchVo> searchRes = new LinkedList<>();

        // 默认的内存型的embedding模型
        AllMiniLmL6V2EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
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
