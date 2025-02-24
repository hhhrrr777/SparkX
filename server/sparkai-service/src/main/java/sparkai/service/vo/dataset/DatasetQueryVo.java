package sparkai.service.vo.dataset;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DatasetQueryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 知识库标题
     */
    private String title;

    /**
     * 分页号
     */
    private long page;

    /**
     * 每页大小
     */
    private long limit;
}