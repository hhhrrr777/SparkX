// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.mapper.dataset;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import sparkai.common.core.IBaseMapper;
import sparkai.service.entity.dataset.KnowledgeEmbeddingEntity;
import sparkai.service.vo.dataset.SearchVo;

import java.util.List;

/**
 * 向量索引表 Mapper
 */
@Mapper
public interface KnowledgeEmbeddingMapper extends IBaseMapper<KnowledgeEmbeddingEntity> {

    @Delete({
            "<script>",
            "DELETE FROM knowledge_embedding",
            "WHERE document_id IN",
            "<foreach item='id' collection='list' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    void deleteByDocumentIds(@Param("list") List<String> documentIds);

    @Select({
        "<script>",
            "SELECT paragraph_id,document_id,comprehensive_score,comprehensive_score as similarity FROM (SELECT DISTINCT ON (\"paragraph_id\") ( similarity ),* ,similarity AS comprehensive_score",
            " FROM ( SELECT *, ( 1 - ( knowledge_embedding.embedding <![CDATA[ <=>  ]]> #{vector} ) ) AS similarity FROM knowledge_embedding WHERE knowledge_embedding.dataset_id IN ",
            "<foreach item='datasetId' collection='datasetIds' open='(' separator=',' close=')'>",
            "#{datasetId}",
            "</foreach>",
            " AND knowledge_embedding.active = 1) TEMP",
            " ORDER BY paragraph_id,similarity DESC) DISTINCT_TEMP",
            " WHERE comprehensive_score > #{score} ORDER BY comprehensive_score DESC LIMIT #{limit}",
        "</script>"
    })
    List<SearchVo> embeddingSearch(@Param("vector") String vector, @Param("datasetIds") List<String> datasetIds,
                                   @Param("score") double score, @Param("limit") int limit);
}
