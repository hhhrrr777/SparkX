package sparkai.service.vo.question;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class QuestionContentVo implements Serializable {

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
}
