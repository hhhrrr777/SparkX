package sparkai.service.vo.question;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class QuestionQueryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属知识库id
     */
    private String datasetId;

    /**
     * 文档问题
     */
    private String content;

    /**
     * 分页号
     */
    private long page;

    /**
     * 每页大小
     */
    private long limit;
}
