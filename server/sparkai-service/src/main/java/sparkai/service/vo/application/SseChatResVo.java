package sparkai.service.vo.application;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SseChatResVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消耗token数
     */
    private long tokens;

    /**
     * 耗时
     */
    private long useTime;

    /**
     * ai返回内容
     */
    private String content;
}