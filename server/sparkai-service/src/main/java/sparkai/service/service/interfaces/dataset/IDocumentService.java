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

import org.springframework.web.multipart.MultipartFile;
import sparkai.service.vo.document.DocumentSplitVo;

import java.io.IOException;
import java.util.List;

public interface IDocumentService {

    /**
     * 上传文件
     * @param files MultipartFile[]
     * @return List<DocumentSplitVo>
     */
    List<DocumentSplitVo> uploadFile(MultipartFile[] files);
}
