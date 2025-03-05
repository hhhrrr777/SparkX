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
    @TableId(value="id", type= IdType.AUTO)
    @TableField(value = "id")
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
    private Short active;

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