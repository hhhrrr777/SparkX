package sparkai.service.vo.question;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class QuestionParagraphDataVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    private String title;

    /**
     * 段落内容
     */
    private String content;

    /**
     * 段落id
     */
    private String paragraphId;

    /**
     * 所属文档id
     */
    private String documentId;
}
