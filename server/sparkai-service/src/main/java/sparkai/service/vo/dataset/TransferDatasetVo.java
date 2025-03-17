package sparkai.service.vo.dataset;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TransferDatasetVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 知识库id
     */
    private String datasetId;

    /**
     * 转移的文档id
     */
    private String documentIds;

    /**
     * 旧的知识库id
     */
    private String oldDatasetId;
}
