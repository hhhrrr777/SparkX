// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.entity.dataset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("public.knowledge_embedding")
public class KnowledgeEmbeddingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    private Integer id;

    /**
    * 唯一标识
    */
    @TableField(value = "uuid")
    private String uuid;

    /**
    * 所属的知识库
    */
    @TableField(value = "dataset_id")
    private String datasetId;

    /**
    * 所属文档
    */
    @TableField(value = "document_id")
    private String documentId;

    /**
    * 所属段落
    */
    @TableField(value = "paragraph_id")
    private String paragraphId;

    /**
    * 向量数据
    */
    @TableField(value = "embedding")
    private Object embedding;

    /**
    * 全文索引
    */
    @TableField(value = "search_vector")
    private Object searchVector;

    /**
    * 状态 1:正常 2:禁用
    */
    @TableField(value = "active")
    private Integer active;

    /**
    * 创建时间
    */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
    * 更新时间
    */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}