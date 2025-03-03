package sparkai.service.vo.document;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DocumentItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 段落标题
     */
    private String title;

    /**
     * 段落内容
     */
    private String content;
}