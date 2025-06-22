// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.impl.application;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationChatLogEntity;
import sparkai.service.entity.application.ApplicationChatSessionEntity;
import sparkai.service.entity.application.ApplicationDatasetRelationEntity;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.helper.UserContextHelper;
import sparkai.service.mapper.application.ApplicationChatLogMapper;
import sparkai.service.mapper.application.ApplicationChatSessionMapper;
import sparkai.service.mapper.application.ApplicationDatasetRelationMapper;
import sparkai.service.mapper.application.ApplicationMapper;
import sparkai.service.service.interfaces.application.IApplicationChatService;
import sparkai.service.vo.application.*;
import sparkai.service.vo.dataset.DatasetSimpleVo;
import sparkai.service.vo.system.LocalUserVo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class ApplicationChatServiceImpl implements IApplicationChatService {

    @Autowired
    ApplicationMapper applicationMapper;

    @Autowired
    ApplicationChatSessionMapper applicationChatSessionMapper;

    @Autowired
    ApplicationDatasetRelationMapper applicationDatasetRelationMapper;

    @Autowired
    ApplicationChatLogMapper applicationChatLogMapper;

    /**
     * 获取应用信息
     * @param appId String
     * @return ApplicationChatVo
     */
    @Override
    public ApplicationVo getChatInfo(String appId) {

        // 设置应用信息
        ApplicationEntity application = applicationMapper.selectById(appId);
        ApplicationVo applicationVo = new ApplicationVo();
        BeanUtils.copyProperties(application, applicationVo);

        // 关联的知识库
        List<DatasetSimpleVo> datasetVoList = new ArrayList<>();
        List<ApplicationDatasetRelationEntity> relationEntityList = applicationDatasetRelationMapper.selectList(
                new QueryWrapper<ApplicationDatasetRelationEntity>().eq("app_id", appId));
        for (ApplicationDatasetRelationEntity entity : relationEntityList) {
            DatasetSimpleVo datasetSimpleVo = new DatasetSimpleVo();
            BeanUtils.copyProperties(entity, datasetSimpleVo);

            datasetVoList.add(datasetSimpleVo);
        }
        applicationVo.setDatasetList(datasetVoList);

        // TODO 未发布的话，则判断是否应有权限

        return applicationVo;
    }

    /**
     * 获取会话记录
     * @param appId String
     * @return List<ApplicationSimpleSessionVo>
     */
    @Override
    public List<ApplicationSimpleSessionVo> getChatSesstionList(String appId) {

        LocalUserVo userData = UserContextHelper.getUser();
        // 设置会话信息
        List<ApplicationChatSessionEntity> sessionList = applicationChatSessionMapper
                .selectList(new QueryWrapper<ApplicationChatSessionEntity>()
                        .eq("user_id", userData.getUserId())
                        .eq("app_id", appId).orderByDesc("create_time").last("LIMIT 20"));
        List<ApplicationSimpleSessionVo> sessionVoList = new LinkedList<>();
        for (ApplicationChatSessionEntity entity : sessionList) {
            ApplicationSimpleSessionVo vo = new ApplicationSimpleSessionVo();
            BeanUtils.copyProperties(entity, vo);

            sessionVoList.add(vo);
        }

        return sessionVoList;
    }

    /**
     * 创建会话
     * @param sessionVo SessionVo
     * @return String
     */
    @Override
    public String createSession(SessionVo sessionVo) {

        LocalUserVo userData = UserContextHelper.getUser();
        ApplicationChatSessionEntity entity = new ApplicationChatSessionEntity();
        entity.setAppId(sessionVo.getAppId());
        entity.setSessionId(IdUtil.randomUUID());
        entity.setUserId(userData.getUserId());
        entity.setCreateTime(Tool.nowDateTime());

        applicationChatSessionMapper.insert(entity);

        return entity.getSessionId();
    }

    /**
     * 更新会话
     * @param sessionVo SessionVo
     */
    @Override
    public void updateSession(SessionVo sessionVo) {

        ApplicationChatSessionEntity entity = applicationChatSessionMapper.selectById(sessionVo.getSessionId());

        String title = sessionVo.getTitle();
        if (title.length() > 25) {
            title = title.substring(0, 25);
        }
        entity.setTitle(title);
        entity.setUpdateTime(Tool.nowDateTime());

        applicationChatSessionMapper.updateById(entity);
    }

    /**
     * 记录对话日志
     * @param logVo ApplicationLogVo
     */
    @Override
    public Integer writeLog(ApplicationLogVo logVo) {

        LocalUserVo userData = UserContextHelper.getUser();

        ApplicationChatLogEntity entity = new ApplicationChatLogEntity();
        entity.setAppId(logVo.getAppId());
        entity.setUserId(userData.getUserId());
        entity.setSessionId(logVo.getSessionId());
        entity.setQuestion(logVo.getQuestion());
        entity.setContent(JSONUtil.toJsonStr(logVo.getAnswer()));
        entity.setTime(logVo.getTime());
        entity.setTokens(logVo.getTokens());
        entity.setRetrievedList(logVo.getRetrievedList());
        entity.setCreateTime(Tool.nowDateTime());

        applicationChatLogMapper.insert(entity);

        return entity.getLogId();
    }

    /**
     * 评价回答
     * @param appraiseVo AppraiseVo
     */
    @Override
    public void appraise(AppraiseVo appraiseVo) {

        ApplicationChatLogEntity entity = applicationChatLogMapper.selectOne(
                new QueryWrapper<ApplicationChatLogEntity>()
                        .eq("log_id", appraiseVo.getLogId()).eq("session_id", appraiseVo.getSessionId()));
        entity.setAppraise(appraiseVo.getAppraise());
        entity.setUpdateTime(Tool.nowDateTime());

        applicationChatLogMapper.updateById(entity);
    }

    /**
     * 删除会话
     * @param sessionId String
     */
    @Override
    @Transactional
    public void delSession(String sessionId) {

        int num = applicationChatSessionMapper.delete(new QueryWrapper<ApplicationChatSessionEntity>()
                .eq("session_id", sessionId).eq("user_id", "b6c67084-ad55-4ced-82c4-4d9d304e8616"));

        if (num > 0) {
            applicationChatLogMapper.delete(new QueryWrapper<ApplicationChatLogEntity>().eq("session_id", sessionId));
        }
    }

    /**
     * 获取聊天记录
     * @param sessionId String
     * @return List<ApplicationLogVo>
     */
    @Override
    public List<ApplicationLogVo> getChatLog(String sessionId) {

        ApplicationChatSessionEntity sessionInfo = applicationChatSessionMapper.selectOne(new QueryWrapper<ApplicationChatSessionEntity>()
                .eq("session_id", sessionId).eq("user_id", "b6c67084-ad55-4ced-82c4-4d9d304e8616"));
        if (sessionInfo == null) {
            throw new BusinessException("暂无记录");
        }

        List<ApplicationChatLogEntity> resultList = applicationChatLogMapper.selectList(new QueryWrapper<ApplicationChatLogEntity>()
                .eq("session_id", sessionId).orderByAsc("log_id"));
        List<ApplicationLogVo> voList = new LinkedList<>();
        for (ApplicationChatLogEntity entity : resultList) {

            ApplicationLogVo vo = new ApplicationLogVo();
            BeanUtils.copyProperties(entity, vo);
            vo.setAnswer(JSONUtil.toList(entity.getContent(), AnswerVo.class));

            voList.add(vo);
        }

        return voList;
    }
}