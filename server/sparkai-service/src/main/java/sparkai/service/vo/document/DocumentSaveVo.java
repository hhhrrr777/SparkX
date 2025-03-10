package sparkai.service.vo.document;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class DocumentSaveVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文档分段列表
     */
    private List<DocumentSplitVo> documentList;

    /**
     * 知识库datasetId
     */
    private String datasetId;
}