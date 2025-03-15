// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.sparkaiweb.controller.dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sparkai.common.core.AjaxResult;
import sparkai.common.core.PageResult;
import sparkai.service.service.interfaces.dataset.IKnowledgeQuestionService;
import sparkai.service.vo.question.*;

import java.util.List;

@RequestMapping("/api/question")
@RestController
public class QuestionController {

    @Autowired
    IKnowledgeQuestionService iKnowledgeQuestionService;

    /**
     * 问题列表
     */
    @GetMapping("/list")
    public AjaxResult<PageResult<QuestionListVo>> list(QuestionQueryVo queryVo) {

        return AjaxResult.success(iKnowledgeQuestionService.getQuestionList(queryVo));
    }

    /**
     * 添加问题
     */
    @PostMapping("/add")
    public AjaxResult<Object> add(@RequestBody QuestionSaveVo questionSaveVo) {

        iKnowledgeQuestionService.addQuestion(questionSaveVo);
        return AjaxResult.success();
    }

    /**
     * 问题关联的文档、段落
     */
    @GetMapping("/getRelation")
    public AjaxResult<List<QuestionRelationListVo>> getRelation(@RequestParam("questionIds") String questionIds,
                                                                @RequestParam("datasetId") String datasetId) {

        return AjaxResult.success(iKnowledgeQuestionService.getRelationList(questionIds, datasetId));
    }

    /**
     * 问题关联的文档、段落
     */
    @PostMapping("/doRelation")
    public AjaxResult<Object> doRelation(@RequestBody QuestionRelationVo relationVo) {

        iKnowledgeQuestionService.doRelation(relationVo);
        return AjaxResult.success();
    }

    /**
     * 问题关联的文档、段落
     */
    @GetMapping("/getRelationData")
    public AjaxResult<List<QuestionParagraphDataVo>> getRelationData(@RequestParam("questionId") String questionId,
                                                                @RequestParam("datasetId") String datasetId) {

        return AjaxResult.success(iKnowledgeQuestionService.getRelationDataList(questionId, datasetId));
    }

    /**
     * 编辑问题内容
     */
    @PostMapping("/edit")
    public AjaxResult<Object> update(@RequestBody QuestionContentVo questionContentVo) {

        iKnowledgeQuestionService.updateContent(questionContentVo);
        return AjaxResult.success();
    }

    /**
     * 删除问题
     */
    @GetMapping("/del")
    public AjaxResult<Object> del(@RequestParam("questionIds") String questionIds) {

        iKnowledgeQuestionService.deleteQuestions(questionIds);
        return AjaxResult.success();
    }
}
