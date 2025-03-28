// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.fileSplitter;

import sparkai.service.vo.document.DocumentItemVo;
import sparkai.service.vo.document.PreviewVo;

import java.util.List;

public interface FileHandleInterface {

    List<DocumentItemVo> handle(byte[] bytes, PreviewVo previewVo);
}
