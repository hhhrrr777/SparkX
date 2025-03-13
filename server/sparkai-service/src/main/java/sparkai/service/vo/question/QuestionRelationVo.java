package sparkai.service.vo.question;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class QuestionRelationVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联的知识库
     */
    private String datasetId;

    /**
     * 关联的文档
     */
    private String documentId;

    /**
     * 关联的段落
     */
    private String paragraphId;

    /**
     * 关联的问题
     */
    private String questionId;
}