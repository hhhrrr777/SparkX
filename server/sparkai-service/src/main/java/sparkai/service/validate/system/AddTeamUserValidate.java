package sparkai.service.validate.system;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AddTeamUserValidate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ids
     */
    @NotEmpty(message = "设置的用户不能为空")
    private String userIds;
}
