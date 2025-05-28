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

import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.common.constant.SparkAIConstant;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.system.SystemUsersEntity;
import sparkai.service.mapper.system.SystemUserMapper;
import sparkai.service.service.interfaces.system.ILoginService;
import sparkai.service.vo.system.LoginVo;

@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    SystemUserMapper systemUserMapper;

    /**
     * 登录操作
     * @param loginVo LoginVo
     * @return String
     */
    @Override
    public String doLogin(LoginVo loginVo) {

        if (loginVo.getUsername().isBlank() || loginVo.getPassword().isBlank()) {
            throw new BusinessException("账号或密码不能为空");
        }

        SystemUsersEntity useInfo = systemUserMapper.selectOne(new QueryWrapper<SystemUsersEntity>()
                .eq("name", loginVo.getUsername()));
        if (useInfo == null) {
            throw new BusinessException("账号或密码错误");
        }

        // 对比密码
        if (!Tool.verifyPassword(useInfo.getPassword(), loginVo.getPassword(), useInfo.getSalt())) {
            throw new BusinessException("账号或密码错误");
        }

        return JWT.create()
                .setPayload("userId", useInfo.getUserId())
                .setKey(SparkAIConstant.CommonData.passwordSalt.getBytes())
                .sign();
    }
}
