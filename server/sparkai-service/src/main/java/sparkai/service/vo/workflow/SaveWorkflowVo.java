package sparkai.service.vo.workflow;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SaveWorkflowVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 流程设计JSON
     */
    private String flowData;
}