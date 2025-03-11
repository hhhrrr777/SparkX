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
import sparkai.service.service.interfaces.dataset.IKnowledgeDocumentService;
import sparkai.service.vo.common.QueryVo;
import sparkai.service.vo.document.*;

import java.util.List;

@RequestMapping("/api/document")
@RestController
public class DocumentController {

    @Autowired
    IKnowledgeDocumentService iKnowledgeDocumentService;

    /**
     * 知识库文档列表
     */
    @GetMapping("/list")
    public AjaxResult<PageResult<DocumentListVo>> list(DocumentQueryVo queryVo) {

        return AjaxResult.success(iKnowledgeDocumentService.getDocumentList(queryVo));
    }

    /**
     * 文档分段预览
     */
    @PostMapping("/preview")
    public AjaxResult<List<DocumentSplitVo>> preview(PreviewVo previewVo) {

        return AjaxResult.success(iKnowledgeDocumentService.previewFile(previewVo));
    }

    /**
     * 保存文档入库
     */
    @PostMapping("/save")
    public AjaxResult<Object> save(@RequestBody DocumentSaveVo saveVo) {

        iKnowledgeDocumentService.saveDocument(saveVo);
        return AjaxResult.success();
    }

    /**
     * 向量化文档
     */
    @GetMapping("/embedding")
    public AjaxResult<Object> embeddings(@RequestParam("documentIds") String documentIds) {

        iKnowledgeDocumentService.doEmbedding(documentIds);
        return AjaxResult.success();
    }
}
