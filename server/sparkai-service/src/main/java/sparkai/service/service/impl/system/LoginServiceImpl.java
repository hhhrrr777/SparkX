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
import sparkai.common.enums.StatusEnum;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.system.SystemTeamEntity;
import sparkai.service.entity.system.SystemUsersEntity;
import sparkai.service.mapper.system.SystemTeamMapper;
import sparkai.service.mapper.system.SystemUserMapper;
import sparkai.service.service.interfaces.system.ILoginService;
import sparkai.service.vo.system.LoginVo;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    SystemUserMapper systemUserMapper;

    @Autowired
    SystemTeamMapper systemTeamMapper;

    /**
     * 登录操作
     * @param loginVo LoginVo
     * @return String
     */
    @Override
    public Map<String, String> doLogin(LoginVo loginVo) {

        if (loginVo.getUsername().isBlank() || loginVo.getPassword().isBlank()) {
            throw new BusinessException("账号或密码不能为空");
        }

        SystemUsersEntity userInfo = systemUserMapper.selectOne(new QueryWrapper<SystemUsersEntity>()
                .eq("name", loginVo.getUsername()).eq("deleted", StatusEnum.YES.getCode()));
        if (userInfo == null) {
            throw new BusinessException("账号或密码错误");
        }

        if (userInfo.getStatus().equals(StatusEnum.NO.getCode())) {
            throw new BusinessException("该账号已被禁用");
        }

        // 对比密码
        if (!Tool.verifyPassword(userInfo.getPassword(), loginVo.getPassword(), userInfo.getSalt())) {
            throw new BusinessException("账号或密码错误");
        }

        SystemTeamEntity teamInfo = systemTeamMapper.selectOne(new QueryWrapper<SystemTeamEntity>()
                .eq("user_id", userInfo.getUserId()));

        Map<String, String> returnData = new HashMap<>();
        returnData.put("token", JWT.create()
                .setPayload("userId", userInfo.getUserId())
                .setPayload("teamId", teamInfo.getTeamId())
                .setKey(SparkAIConstant.CommonData.passwordSalt.getBytes())
                .sign());
        returnData.put("name", userInfo.getNickname());

        return returnData;
    }
}
