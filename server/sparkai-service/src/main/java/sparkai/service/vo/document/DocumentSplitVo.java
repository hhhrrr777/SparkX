package sparkai.service.vo.document;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class DocumentSplitVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件分段
     */
    private List<DocumentItemVo> content;

    /**
     * 文件标题
     */
    private String name;

    /**
     * 文本字符数
     */
    private long fileSize;
}