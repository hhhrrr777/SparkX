// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.interfaces.system;

import sparkai.service.vo.system.AuthLoginVo;
import sparkai.service.vo.system.LoginVo;

import java.util.Map;

public interface ILoginService {

    /**
     * 登录操作
     * @param loginVo LoginVo
     * @return Map<String, String>
     */
    Map<String, String> doLogin(LoginVo loginVo);

    /**
     * 部署模式下的鉴权登录
     * @param loginVo AuthLoginVo
     * @return Map<String, String>
     */
    Map<String, Object> doAuthLogin(AuthLoginVo loginVo);
}
