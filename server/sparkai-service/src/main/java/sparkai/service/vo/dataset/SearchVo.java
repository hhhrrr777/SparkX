package sparkai.service.vo.dataset;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SearchVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 段落id
     */
    private String paragraphId;

    /**
     * 相似度
     */
    private double similarity;

    /**
     * 综合得分
     */
    private double comprehensiveScore;

    /**
     * 段落标题
     */
    private String title;

    /**
     * 段落内容
     */
    private String content;

    /**
     * 文档id
     */
    private String documentId;

    /**
     * 文档名称
     */
    private String documentName;
}
