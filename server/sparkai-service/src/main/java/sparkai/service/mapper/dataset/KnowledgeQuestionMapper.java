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

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import sparkai.common.core.IBaseMapper;
import sparkai.service.entity.dataset.KnowledgeQuestionEntity;

import java.util.List;

/**
 * 知识库问题表 Mapper
 */
@Mapper
public interface KnowledgeQuestionMapper extends IBaseMapper<KnowledgeQuestionEntity> {

}
