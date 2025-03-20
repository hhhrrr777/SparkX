package sparkai.service.vo.application;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SseChatVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 应用ID
     */
    private String appId;
}