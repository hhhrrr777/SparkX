package sparkai.service.vo.application;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SessionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 会话标题
     */
    private String title;
}
