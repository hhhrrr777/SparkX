package sparkai.service.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.service.entity.system.UsersEntity;
import sparkai.service.mapper.system.UserMapper;
import sparkai.service.service.interfaces.system.IUserService;
import sparkai.service.validate.system.UserValidate;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;

    /**
     * 获取用户列表
     * @return List<UsersEntity>
     */
    @Override
    public List<UsersEntity> getUserList() {
        System.out.println(userMapper);
        QueryWrapper<UsersEntity> queryWrapper = new QueryWrapper<>();

        return userMapper.selectList(queryWrapper);
    }

    /**
     * 添加用户
     * @param validate UserValidate
     */
    @Override
    public void addUser(UserValidate validate) {

    }

    /**
     * 编辑用户
     * @param validate UserValidate
     */
    @Override
    public void editUser(UserValidate validate) {

    }

    /**
     * 删除用户
     * @param id long
     */
    @Override
    public void delUser(long id) {

    }
}
