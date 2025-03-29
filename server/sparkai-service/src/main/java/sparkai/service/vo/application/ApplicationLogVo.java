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

@Data
public class ApplicationLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 用户问题
     */
    private String question;

    /**
     * ai回答
     */
    private String answer;

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 耗时
     */
    private Integer time;

    /**
     * 消耗token数
     */
    private Integer tokens;

    /**
     * 召回的
     */
    private String retrievedList;
}
