package sparkai.service.vo.document;

import lombok.Data;

import java.util.List;

@Data
public class DocumentSaveVo {

    /**
     * 文档分段列表
     */
    private List<DocumentSplitVo> documentList;

    /**
     * 知识库uuid
     */
    private String uuid;
}