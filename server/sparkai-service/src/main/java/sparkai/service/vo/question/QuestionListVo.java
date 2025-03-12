package sparkai.service.vo.question;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class QuestionListVo  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 问题id
     */
    private String questionId;

    /**
     * 问题内容
     */
    private String content;

    /**
     * 关联分段数
     */
    private long linkNum;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
