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
@TableName("public.knowledge_question")
public class KnowledgeQuestionEntity implements Serializable {

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
    * 问题内容
    */
    @TableField(value = "content")
    private String content;

    /**
    * 命中次数
    */
    @TableField(value = "hit_nums")
    private Integer hitNums;

    /**
    * 所属知识库
    */
    @TableField(value = "dataset_id")
    private String datasetId;

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