package sparkai.service.vo.dataset;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class OtherDatasetVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 知识库id
     */
    private String datasetId;

    /**
     * 知识库名
     */
    private String title;
}
