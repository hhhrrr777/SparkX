package sparkai.service.entity.application;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("public.application_dataset_relation")
public class ApplicationDatasetRelationEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * 应用id
    */
    @TableField(value = "app_id")
    private String appId;

    /**
    * 知识库id
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