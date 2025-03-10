package sparkai.service.vo.document;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class DocumentListVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文档名称
     */
    private String name;

    /**
     * 唯一标识
     */
    private String uuid;

    /**
     * 文件大小
     */
    private long fileSize;

    /**
     * 向量化状态
     */
    private Integer status;

    /**
     * 生成问题状态
     */
    private Integer questionStatus;

    /**
     * 是否启用
     */
    private Integer active;

    /**
     * 扩充信息
     */
    private String statusMeta;

    /**
     * 分段数
     */
    private Long paragraphNum;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
