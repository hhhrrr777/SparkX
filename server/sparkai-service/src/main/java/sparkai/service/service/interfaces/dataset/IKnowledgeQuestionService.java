// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.interfaces.dataset;

import sparkai.common.core.PageResult;
import sparkai.service.vo.question.QuestionListVo;
import sparkai.service.vo.question.QuestionQueryVo;
import sparkai.service.vo.question.QuestionSaveVo;

/**
 * <p>
 * 知识库问题表 服务类
 * </p>
 *
 * @author NickBai
 * @since 2025-03-05
 */
public interface IKnowledgeQuestionService {

    /**
     * 获取问题列表
     * @param queryVo QuestionQueryVo
     * @return PageResult<QuestionListVo>
     */
    PageResult<QuestionListVo> getQuestionList(QuestionQueryVo queryVo);

    /**
     * 添加问题
     * @param questionSaveVo QuestionSaveVo
     */
    void addQuestion(QuestionSaveVo questionSaveVo);
}
