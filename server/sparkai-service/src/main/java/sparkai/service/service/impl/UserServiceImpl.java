package sparkai.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import sparkai.service.entity.User;
import sparkai.service.mapper.UserMapper;
import sparkai.service.service.IUserService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Resource
    UserMapper userMapper;

    @Override
    public List<User> getUserList() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        return userMapper.selectList(queryWrapper);
    }
}
