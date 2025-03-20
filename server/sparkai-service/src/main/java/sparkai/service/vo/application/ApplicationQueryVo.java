package sparkai.service.vo.application;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ApplicationQueryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 分页号
     */
    private long page;

    /**
     * 每页大小
     */
    private long limit;
}
