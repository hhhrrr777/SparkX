package sparkai.service.validate.workflow;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SaveWorkflowValidate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用id
     */
    @NotEmpty(message = "关联的应用不能为空")
    private String appId;

    /**
     * 流程设计JSON
     */
    @NotEmpty(message = "设计数据不能为空")
    private String flowData;
}