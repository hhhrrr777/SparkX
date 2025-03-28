// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.vo.application;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class ApplicationChatVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用信息
     */
    private ApplicationVo applicationInfo;

    /**
     * 会话列表
     */
    private List<ApplicationSimpleSessionVo> sessionList;
}