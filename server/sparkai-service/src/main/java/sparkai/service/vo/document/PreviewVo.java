package sparkai.service.vo.document;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PreviewVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 上传的文件
     */
    private MultipartFile[] files;

    /**
     * 拆分规则
     */
    private String patternList;

    /**
     * 切割长度
     */
    private Integer splitLen;

    /**
     * 导入时把标题关联成问题
     */
    private boolean addTitle;

    /**
     * 查分类型 1:默认 2:自定义
     */
    private Integer splitType;

    /**
     * 自动清洗
     */
    private boolean autoClean;
}
