// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.impl.system;

import cn.hutool.core.lang.ObjectId;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.common.core.PageResult;
import sparkai.common.enums.StatusEnum;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.system.UsersEntity;
import sparkai.service.mapper.system.UserMapper;
import sparkai.service.service.interfaces.system.IUserService;
import sparkai.service.validate.system.UserValidate;
import sparkai.service.vo.system.UserQueryVo;
import sparkai.service.vo.system.UsersVo;

import java.util.LinkedList;
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
    public PageResult<UsersVo> getUserList(UserQueryVo queryVo) {

        QueryWrapper<UsersEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", StatusEnum.YES.getCode());

        if (!queryVo.getName().isBlank()) {
            queryWrapper.like("nickname", queryVo.getName());
        }

        if (queryVo.getStatus() != null && queryVo.getStatus() > 0) {
            queryWrapper.eq("status", queryVo.getStatus());
        }

        queryWrapper.orderByDesc("id");

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        IPage<UsersEntity> userListRes = userMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        List<UsersVo> usersList = new LinkedList<>();

        for (UsersEntity entity : userListRes.getRecords()) {
            UsersVo vo = new UsersVo();
            BeanUtils.copyProperties(entity, vo);

            usersList.add(vo);
        }

        return PageResult.iPageHandle(userListRes.getTotal(), pageNo, pageSize, usersList);
    }

    /**
     * 添加用户
     * @param validate UserValidate
     */
    @Override
    public void addUser(UserValidate validate) {

        if (validate.getPassword().isBlank()) {
            throw new BusinessException("密码不能为空");
        }

        int pwdLength = validate.getPassword().length();
        if (pwdLength < 6 || pwdLength > 15) {
            throw new BusinessException("密码长度应该在6~15位");
        }

        // 检测账号
        QueryWrapper<UsersEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", validate.getName());
        UsersEntity userRes = userMapper.selectOne(queryWrapper);
        if (userRes != null) {
            throw new BusinessException("该账号已经被使用");
        }

        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setName(validate.getName());
        usersEntity.setNickname(validate.getNickname());
        usersEntity.setAvatar(validate.getAvatar());
        usersEntity.setStatus(validate.getStatus());
        String salt = ObjectId.next();
        usersEntity.setPassword(Tool.makePassword(validate.getPassword(), salt));
        usersEntity.setSalt(salt);
        usersEntity.setCode(IdUtil.randomUUID());
        usersEntity.setCreateTime(Tool.nowDateTime());

        userMapper.insert(usersEntity);
    }

    /**
     * 编辑用户
     * @param validate UserValidate
     */
    @Override
    public void editUser(UserValidate validate) {

        UsersEntity usersEntity = new UsersEntity();
        BeanUtils.copyProperties(validate, usersEntity);

        // 检测账号
        QueryWrapper<UsersEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", validate.getName());
        queryWrapper.ne("id", validate.getId());
        UsersEntity userRes = userMapper.selectOne(queryWrapper);
        if (userRes != null) {
            throw new BusinessException("该账号已经被使用");
        }

        // 重置密码
        if (!validate.getPassword().isBlank()) {

            int pwdLength = validate.getPassword().length();
            if (pwdLength < 6 || pwdLength > 15) {
                throw new BusinessException("密码长度应该在6~15位");
            }

            String salt = ObjectId.next();
            usersEntity.setPassword(Tool.makePassword(validate.getPassword(), salt));
            usersEntity.setSalt(salt);
        }

        usersEntity.setUpdateTime(Tool.nowDateTime());

        userMapper.updateById(usersEntity);
    }

    /**
     * 删除用户
     * @param id long
     */
    @Override
    public void delUser(long id) {

        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setId(id);
        usersEntity.setDeleted(StatusEnum.NO.getCode());

        userMapper.updateById(usersEntity);
    }
}
