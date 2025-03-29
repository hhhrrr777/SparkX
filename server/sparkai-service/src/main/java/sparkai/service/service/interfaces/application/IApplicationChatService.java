// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.interfaces.application;

import sparkai.service.vo.application.ApplicationSimpleSessionVo;
import sparkai.service.vo.application.ApplicationVo;
import sparkai.service.vo.application.SessionVo;

import java.util.List;

public interface IApplicationChatService {

    /**
     * 获取应用信息
     * @param appId String
     * @return ApplicationChatVo
     */
    ApplicationVo getChatInfo(String appId);

    /**
     * 获取会话记录
     * @param appId String
     * @return List<ApplicationSimpleSessionVo>
     */
    List<ApplicationSimpleSessionVo> getChatSesstionList(String appId);

    /**
     * 创建会话
     * @param sessionVo SessionVo
     * @return String
     */
    String createSession(SessionVo sessionVo);

    /**
     * 更新会话
     * @param sessionVo SessionVo
     */
    void updateSession(SessionVo sessionVo);
}
