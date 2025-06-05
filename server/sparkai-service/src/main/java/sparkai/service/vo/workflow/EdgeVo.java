package sparkai.service.vo.workflow;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class EdgeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 节点id
     */
    private String id;

    /**
     * 起始节点
     */
    private String source;

    /**
     * 起始桩点
     */
    private String sourcePort;

    /**
     * 目标节点
     */
    private List<String> target;
}