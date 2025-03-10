package sparkai.service.vo.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class QueryVo implements Serializable {

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