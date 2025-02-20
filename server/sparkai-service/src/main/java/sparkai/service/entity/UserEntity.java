package sparkai.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("public.User")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String email;

    private String phone;

    private String username;
}
