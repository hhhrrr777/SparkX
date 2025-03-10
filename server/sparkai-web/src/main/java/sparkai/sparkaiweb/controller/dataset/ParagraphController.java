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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparkai.common.core.AjaxResult;
import sparkai.common.core.PageResult;
import sparkai.service.service.interfaces.dataset.IKnowledgeParagraphService;
import sparkai.service.vo.paragraph.ParagraphListVo;
import sparkai.service.vo.paragraph.ParagraphQueryVo;

@RequestMapping("/api/paragraph")
@RestController
public class ParagraphController {

    @Autowired
    IKnowledgeParagraphService iKnowledgeParagraphService;

    @GetMapping("/list")
    public AjaxResult<PageResult<ParagraphListVo>> list(ParagraphQueryVo queryVo) {

        return AjaxResult.success(iKnowledgeParagraphService.getParagraphList(queryVo));
    }
}
