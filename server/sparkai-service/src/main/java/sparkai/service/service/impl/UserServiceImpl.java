package sparkai.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.service.entity.UserEntity;
import sparkai.service.mapper.UserMapper;
import sparkai.service.service.IUserService;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<UserEntity> getUserList() {
        System.out.println(userMapper);
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();

        return userMapper.selectList(queryWrapper);
    }
}
