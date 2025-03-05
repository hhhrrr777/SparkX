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
@TableName("public.knowledge_document")
public class KnowledgeDocumentEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    private Integer id;

    /**
    * 文件名称
    */
    @TableField(value = "name")
    private String name;

    /**
    * 唯一标识
    */
    @TableField(value = "uuid")
    private String uuid;

    /**
    * 字符长度
    */
    @TableField(value = "char_length")
    private Integer charLength;

    /**
    * 状态 1:待索引 2:索引中 3:索引完成
    */
    @TableField(value = "status")
    private Integer status;

    /**
    * 生成问题状态 1:待生成 2:生成中 3:生成完成
    */
    @TableField(value = "question_status")
    private Integer questionStatus;

    /**
    * 状态 1:正常 2:禁用
    */
    @TableField(value = "active")
    private Integer active;

    /**
    * 所属知识库
    */
    @TableField(value = "dataset_id")
    private String datasetId;

    /**
    * 状态json数据
    */
    @TableField(value = "status_meta")
    private Object statusMeta;

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