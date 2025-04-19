// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.sparkaiweb.controller.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sparkai.common.core.AjaxResult;
import sparkai.service.service.interfaces.home.IHomeService;

@RestController
@RequestMapping("/api/index")
public class IndexController {

    @Autowired
    IHomeService iHomeService;

    /**
     * 上传图片
     */
    @PostMapping("/upload")
    public AjaxResult<Object> image(@RequestParam("file") MultipartFile file) {

        return AjaxResult.success(iHomeService.uploadImage(file));
    }
}
