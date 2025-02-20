package sparkai.service.service.interfaces.system;

import sparkai.service.entity.system.UsersEntity;
import sparkai.service.validate.system.UserValidate;

import java.util.List;

public interface IUserService {

    /**
     * 用户列表
     * @return List<UsersEntity>
     */
    List<UsersEntity> getUserList();

    /**
     * 添加用户
     * @param validate UserValidate
     */
    void addUser(UserValidate validate);

    /**
     * 编辑用户
     * @param validate UserValidate
     */
    void editUser(UserValidate validate);

    /**
     * 删除用户
     * @param id long
     */
    void delUser(long id);
}
