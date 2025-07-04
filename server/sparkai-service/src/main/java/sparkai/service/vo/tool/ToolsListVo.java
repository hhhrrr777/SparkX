// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.vo.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ToolsListVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 插件标识
     */
    private String name;

    /**
     * 插件名称
     */
    private String title;

    /**
     * 插件描述
     */
    private String description;

    /**
     * 创建时间
     */
    @JsonProperty(value = "create_time")
    private LocalDateTime createTime;
}