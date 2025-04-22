package sparkai.service.vo.workflow;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class NodeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 节点id
     */
    private String id;

    /**
     * 类型
     */
    private String shape;

    /**
     * 节点数据
     */
    private JSONObject data;
}