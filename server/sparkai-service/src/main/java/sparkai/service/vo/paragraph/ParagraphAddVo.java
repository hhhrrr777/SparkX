package sparkai.service.vo.paragraph;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ParagraphAddVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属知识库id
     */
    private String datasetId;

    /**
     * 所属文档id
     */
    private String documentId;

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