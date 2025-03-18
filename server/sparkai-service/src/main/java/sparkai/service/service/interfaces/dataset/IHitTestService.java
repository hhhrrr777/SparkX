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

import sparkai.service.vo.dataset.HitTestVo;
import sparkai.service.vo.dataset.SearchVo;

import java.util.List;

public interface IHitTestService {

    List<SearchVo> search(HitTestVo hitTestVo);
}
