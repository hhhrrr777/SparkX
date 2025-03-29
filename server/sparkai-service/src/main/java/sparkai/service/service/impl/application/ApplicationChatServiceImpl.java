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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationChatSessionEntity;
import sparkai.service.entity.application.ApplicationDatasetRelationEntity;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.mapper.application.ApplicationChatSessionMapper;
import sparkai.service.mapper.application.ApplicationDatasetRelationMapper;
import sparkai.service.mapper.application.ApplicationMapper;
import sparkai.service.service.interfaces.application.IApplicationChatService;
import sparkai.service.vo.application.ApplicationSimpleSessionVo;
import sparkai.service.vo.application.ApplicationVo;
import sparkai.service.vo.application.SessionVo;
import sparkai.service.vo.application.SseChatVo;
import sparkai.service.vo.dataset.DatasetSimpleVo;

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

        // 设置会话信息
        List<ApplicationChatSessionEntity> sessionList = applicationChatSessionMapper
                .selectList(new QueryWrapper<ApplicationChatSessionEntity>()
                        .eq("user_id", "b6c67084-ad55-4ced-82c4-4d9d304e8616")
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

        ApplicationChatSessionEntity entity = new ApplicationChatSessionEntity();
        entity.setAppId(sessionVo.getAppId());
        entity.setSessionId(IdUtil.randomUUID());
        entity.setUserId("b6c67084-ad55-4ced-82c4-4d9d304e8616");
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
}