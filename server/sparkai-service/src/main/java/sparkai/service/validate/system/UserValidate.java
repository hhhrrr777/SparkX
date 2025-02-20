package sparkai.service.validate.system;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserValidate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 登录账号
     */
    @NotEmpty(message = "登录账号不能为空")
    @Min(value = 6, message = "登录账号最少6个字符")
    @Max(value = 55, message = "登录账号最大55个字符")
    private String name;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 密码
     */
    private String password;

    /**
     * 状态 1:正常 2:禁用
     */
    @Range(min = 1, max = 2, message = "状态值不对")
    private Integer status;

    /**
     * 头像
     */
    private String avatar;
}
