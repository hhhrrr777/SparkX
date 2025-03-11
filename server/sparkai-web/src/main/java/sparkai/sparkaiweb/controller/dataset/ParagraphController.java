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
import sparkai.service.service.interfaces.dataset.IKnowledgeParagraphService;
import sparkai.service.vo.paragraph.ParagraphListVo;
import sparkai.service.vo.paragraph.ParagraphQueryVo;
import sparkai.service.vo.paragraph.ParagraphVo;

@RequestMapping("/api/paragraph")
@RestController
public class ParagraphController {

    @Autowired
    IKnowledgeParagraphService iKnowledgeParagraphService;

    /**
     * 段落列表
     */
    @GetMapping("/list")
    public AjaxResult<PageResult<ParagraphListVo>> list(ParagraphQueryVo queryVo) {

        return AjaxResult.success(iKnowledgeParagraphService.getParagraphList(queryVo));
    }

    /**
     * 激活、关闭段落
     */
    @PostMapping("/active")
    public AjaxResult<Object> active(@RequestBody ParagraphVo paragraphVo) {

        iKnowledgeParagraphService.activeParagraph(paragraphVo);
        return AjaxResult.success();
    }

    /**
     * 编辑段落
     */
    @PostMapping("/edit")
    public AjaxResult<Object> edit(@RequestBody ParagraphVo paragraphVo) {

        iKnowledgeParagraphService.editParagraph(paragraphVo);
        return AjaxResult.success();
    }
}
