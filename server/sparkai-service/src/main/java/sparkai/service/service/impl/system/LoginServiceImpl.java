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
import sparkai.service.entity.application.ApplicationCustomerEntity;
import sparkai.service.entity.system.SystemTeamEntity;
import sparkai.service.entity.system.SystemUsersEntity;
import sparkai.service.mapper.application.ApplicationCustomerMapper;
import sparkai.service.mapper.system.SystemTeamMapper;
import sparkai.service.mapper.system.SystemUserMapper;
import sparkai.service.service.interfaces.system.ILoginService;
import sparkai.service.vo.system.AuthLoginVo;
import sparkai.service.vo.system.LoginVo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    SystemUserMapper systemUserMapper;

    @Autowired
    SystemTeamMapper systemTeamMapper;

    @Autowired
    ApplicationCustomerMapper applicationCustomerMapper;

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
        returnData.put("customerId", userInfo.getUserId());

        return returnData;
    }

    /**
     * 部署模式下的鉴权登录
     * @param loginVo AuthLoginVo
     * @return Map<String, String>
     */
    @Override
    public Map<String, Object> doAuthLogin(AuthLoginVo loginVo) {

        ApplicationCustomerEntity customerInfo = new ApplicationCustomerEntity();
        boolean resetToken = false;
        // 第一次进入，写入随机用户
        if (loginVo.getCustomerId() == null) {

            customerInfo = regCustomer(loginVo);
            resetToken = true;
        } else {

            // 系统登录用户
            if (loginVo.getCustomerId().contains("-")) {

                SystemUsersEntity userInfo = systemUserMapper.selectOne(new QueryWrapper<SystemUsersEntity>()
                        .eq("user_id", loginVo.getCustomerId()).eq("deleted", StatusEnum.YES.getCode()));
                customerInfo.setCustomerId(userInfo.getUserId());
            } else {
                customerInfo = applicationCustomerMapper.selectOne(
                        new QueryWrapper<ApplicationCustomerEntity>()
                                .eq("customer_id", loginVo.getCustomerId()).eq("app_token", loginVo.getToken()));
                if (customerInfo == null) {
                    customerInfo = regCustomer(loginVo);
                }
                resetToken = true;
            }
        }

        Map<String, Object> returnData = new HashMap<>();
        returnData.put("token", JWT.create()
                .setPayload("userId", customerInfo.getCustomerId())
                .setKey(SparkAIConstant.CommonData.passwordSalt.getBytes())
                .sign());
        returnData.put("customerId", customerInfo.getCustomerId());
        returnData.put("resetToken", resetToken);

        return returnData;
    }

    /**
     * 注册访客
     * @param loginVo AuthLoginVo
     * @return ApplicationCustomerEntity
     */
    private ApplicationCustomerEntity regCustomer(AuthLoginVo loginVo) {

        ApplicationCustomerEntity customerInfo = new ApplicationCustomerEntity();
        customerInfo.setCustomerId(Tool.makeToken());
        try {

            InetAddress inetAddress = InetAddress.getLocalHost();
            customerInfo.setCustomerIp(inetAddress.getHostAddress());
        } catch (UnknownHostException e) {
            customerInfo.setCustomerIp(SparkAIConstant.CommonData.defaultIp);
        }
        customerInfo.setAppToken(loginVo.getToken());
        customerInfo.setCreateTime(Tool.nowDateTime());

        applicationCustomerMapper.insert(customerInfo);

        return customerInfo;
    }
}
