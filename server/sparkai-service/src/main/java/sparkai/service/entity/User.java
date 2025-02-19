package sparkai.service.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id")
    private String id;

    private String email;

    private String phone;

    private String username;
}
