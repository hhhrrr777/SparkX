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
import sparkai.common.core.IBaseMapper;
import sparkai.service.entity.dataset.KnowledgeQuestionParagraphEntity;

import java.util.List;

/**
 * 段落问题关联表 Mapper
 */
@Mapper
public interface KnowledgeQuestionParagraphMapper extends IBaseMapper<KnowledgeQuestionParagraphEntity> {

    @Delete({
            "<script>",
            "DELETE FROM knowledge_question_paragraph",
            "WHERE document_id IN",
            "<foreach item='id' collection='list' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    void deleteByDocumentIds(@Param("list") List<String> documentIds);
}
