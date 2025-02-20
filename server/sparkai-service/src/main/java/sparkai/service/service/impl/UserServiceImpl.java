package sparkai.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.service.entity.UsersEntity;
import sparkai.service.mapper.UserMapper;
import sparkai.service.service.IUserService;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<UsersEntity> getUserList() {
        System.out.println(userMapper);
        QueryWrapper<UsersEntity> queryWrapper = new QueryWrapper<>();

        return userMapper.selectList(queryWrapper);
    }
}
