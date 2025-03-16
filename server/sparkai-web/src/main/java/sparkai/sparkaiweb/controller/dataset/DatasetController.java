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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sparkai.common.core.AjaxResult;
import sparkai.common.core.PageResult;
import sparkai.service.service.interfaces.dataset.IHitTestService;
import sparkai.service.service.interfaces.dataset.IKnowledgeDatasetService;
import sparkai.service.validate.dataset.DatasetValidate;
import sparkai.service.vo.dataset.DatasetQueryVo;
import sparkai.service.vo.dataset.DatasetVo;
import sparkai.service.vo.dataset.HitTestVo;
import sparkai.service.vo.dataset.SearchVo;

import java.util.List;

@RequestMapping("/api/dataset")
@RestController
public class DatasetController {

    @Autowired
    IKnowledgeDatasetService iKnowledgeDatasetService;

    @Autowired
    IHitTestService iHitTestService;

    /**
     * 知识库列表
     */
    @GetMapping("/list")
    public AjaxResult<PageResult<DatasetVo>> list(DatasetQueryVo queryVo) {

        return AjaxResult.success(iKnowledgeDatasetService.getDatasetList(queryVo));
    }

    /**
     * 创建知识库
     */
    @PostMapping("/add")
    public AjaxResult<Object> add(@RequestBody @Validated DatasetValidate validate) {

        iKnowledgeDatasetService.addDataset(validate);
        return AjaxResult.success();
    }

    @PostMapping("/hitTest")
    public AjaxResult<List<SearchVo>> hitTest(@RequestBody HitTestVo hitTestVo) {

        return AjaxResult.success(iHitTestService.search(hitTestVo));
    }
}