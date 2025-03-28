package sparkai.service.vo.application;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class ApplicationChatVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用信息
     */
    private ApplicationVo applicationInfo;

    /**
     * 会话列表
     */
    private List<ApplicationSimpleSessionVo> sessionList;
}