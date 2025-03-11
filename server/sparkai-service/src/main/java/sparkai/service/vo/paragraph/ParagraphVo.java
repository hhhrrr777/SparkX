package sparkai.service.vo.paragraph;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ParagraphVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 段落id
     */
    private String paragraphId;

    /**
     * 段落标题
     */
    private String title;

    /**
     * 段落内容
     */
    private String content;

    /**
     * 段落状态
     */
    private Integer active;
}