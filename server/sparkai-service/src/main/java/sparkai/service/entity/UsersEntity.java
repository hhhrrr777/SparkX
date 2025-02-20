package sparkai.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("public.users")
public class UsersEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value="id", type= IdType.AUTO)
    @TableField(value = "id")
    private Integer id;

    /**
     * 登录账号
     */
    @TableField(value = "name")
    private String name;

    /**
     * 用户标识
     */
    @TableField(value = "code")
    private String code;

    /**
     * 昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 状态 1:正常 2:禁用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 头像
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
