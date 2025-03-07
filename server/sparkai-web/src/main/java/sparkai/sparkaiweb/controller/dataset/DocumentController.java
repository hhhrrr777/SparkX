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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparkai.common.core.AjaxResult;
import sparkai.service.service.interfaces.dataset.IKnowledgeDocumentService;
import sparkai.service.vo.document.DocumentSaveVo;
import sparkai.service.vo.document.DocumentSplitVo;
import sparkai.service.vo.document.PreviewVo;

import java.util.List;

@RequestMapping("/api/document")
@RestController
public class DocumentController {

    @Autowired
    IKnowledgeDocumentService iKnowledgeDocumentService;

    @PostMapping("/preview")
    public AjaxResult<List<DocumentSplitVo>> preview(PreviewVo previewVo) {

        return AjaxResult.success(iKnowledgeDocumentService.previewFile(previewVo));
    }

    @PostMapping("/save")
    public AjaxResult<Object> save(@RequestBody DocumentSaveVo saveVo) {

        iKnowledgeDocumentService.saveDocument(saveVo);
        return AjaxResult.success();
    }
}
