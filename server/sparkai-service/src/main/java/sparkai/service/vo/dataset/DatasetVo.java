// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.vo.dataset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class DatasetVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * uuid
    */
    private String datasetId;

    /**
    * 知识库标题
    */
    private String title;

    /**
    * 知识库描述
    */
    private String description;

    /**
    * 类型 1:通用 2:web站点
    */
    private Integer type;

    /**
    * 模型的uuid
    */
    @JsonProperty(value = "embedding_mode_id")
    private String embeddingModeId;

    /**
     * 创建人
     */
    private String author;

    /**
     * 文档数
     */
    private long documentNum;

    /**
     * 字符数
     */
    private long fileSize;

    /**
     * 关联的应用数
     */
    private long appNum;
}