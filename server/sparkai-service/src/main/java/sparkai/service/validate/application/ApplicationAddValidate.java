package sparkai.service.validate.application;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ApplicationAddValidate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用名称
     */
    @Length(min = 2, max = 25, message = "应用标题必须在2到25个字")
    @NotEmpty(message = "应用标题不能为空")
    private String name;

    /**
     * 应用描述
     */
    @Length(min = 2, max = 25, message = "应用描述必须在2到255个字")
    @NotEmpty(message = "应用描述不能为空")
    private String description;

    /**
     * 应用类型
     */
    private Integer type;
}
