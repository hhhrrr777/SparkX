package sparkai.service.vo.workflow;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class EdgeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 节点id
     */
    private String id;

    /**
     * 目标节点
     */
    private String target;
}