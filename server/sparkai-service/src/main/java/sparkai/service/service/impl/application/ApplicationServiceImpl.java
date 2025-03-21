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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sparkai.common.core.PageResult;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationDatasetRelationEntity;
import sparkai.service.entity.application.ApplicationEntity;
import sparkai.service.entity.dataset.KnowledgeDatasetEntity;
import sparkai.service.entity.system.SystemUsersEntity;
import sparkai.service.mapper.application.ApplicationDatasetRelationMapper;
import sparkai.service.mapper.application.ApplicationMapper;
import sparkai.service.mapper.dataset.KnowledgeDatasetMapper;
import sparkai.service.mapper.system.SystemUserMapper;
import sparkai.service.service.interfaces.application.IApplicationService;
import sparkai.service.validate.application.ApplicationAddValidate;
import sparkai.service.vo.application.ApplicationListVo;
import sparkai.service.vo.application.ApplicationQueryVo;
import sparkai.service.vo.application.ApplicationVo;
import sparkai.service.vo.dataset.DatasetSimpleVo;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统应用表 服务实现类
 * </p>
 *
 * @author NickBai
 * @since 2025-03-18
 */
@Service
public class ApplicationServiceImpl implements IApplicationService {

    @Autowired
    ApplicationMapper applicationMapper;

    @Autowired
    SystemUserMapper systemUserMapper;

    @Autowired
    ApplicationDatasetRelationMapper applicationDatasetRelationMapper;

    @Autowired
    KnowledgeDatasetMapper knowledgeDatasetMapper;

    /**
     * 应用列表
     * @param queryVo ApplicationQueryVo
     * @return PageResult<ApplicationListVo>
     */
    @Override
    public PageResult<ApplicationListVo> getApplicationList(ApplicationQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<ApplicationEntity> queryWrapper = new QueryWrapper<>();

        if (!queryVo.getName().isBlank()) {
            queryWrapper.like("name", queryVo.getName());
        }

        // TODO 查询属于自己的应用
        queryWrapper.eq("user_id", "b6c67084-ad55-4ced-82c4-4d9d304e8616");

        queryWrapper.orderByDesc("create_time");
        IPage<ApplicationEntity> applicationListRes = applicationMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        List<ApplicationListVo> applicationVoList = new LinkedList<>();

        for (ApplicationEntity entity : applicationListRes.getRecords()) {
            ApplicationListVo vo = new ApplicationListVo();
            BeanUtils.copyProperties(entity, vo);

            SystemUsersEntity userInfo = systemUserMapper.selectById(entity.getUserId());
            vo.setAuthor(userInfo.getNickname());

            applicationVoList.add(vo);
        }

        return PageResult.iPageHandle(applicationListRes.getTotal(), pageNo, pageSize, applicationVoList);
    }

    /**
     * 添加应用
     * @param validate ApplicationAddValidate
     */
    @Override
    public String addApplication(ApplicationAddValidate validate) {

        ApplicationEntity entity = new ApplicationEntity();
        entity.setAppId(IdUtil.randomUUID());
        entity.setName(validate.getName());
        entity.setUserId("b6c67084-ad55-4ced-82c4-4d9d304e8616");
        entity.setDescription(validate.getDescription());
        entity.setType(validate.getType());
        entity.setCreateTime(Tool.nowDateTime());

        applicationMapper.insert(entity);

        return entity.getAppId();
    }

    /**
     * 获取应用信息
     * @param appId String
     * @return ApplicationVo
     */
    @Override
    public ApplicationVo getApplicationInfo(String appId) {

        ApplicationVo applicationVo = new ApplicationVo();
        ApplicationEntity info = applicationMapper.selectOne(
                new QueryWrapper<ApplicationEntity>()
                        .eq("user_id", "b6c67084-ad55-4ced-82c4-4d9d304e8616")
                        .eq("app_id", appId));
        BeanUtils.copyProperties(info, applicationVo);

        // 查询关联的知识库信息
        List<ApplicationDatasetRelationEntity> relationEntityList =
                applicationDatasetRelationMapper.selectList(new QueryWrapper<ApplicationDatasetRelationEntity>()
                        .eq("app_id", appId));
        if (!CollectionUtils.isEmpty(relationEntityList)) {

            List<String> datasetIds = relationEntityList.stream().map(ApplicationDatasetRelationEntity::getDatasetId).toList();
            List<KnowledgeDatasetEntity> datasetList = knowledgeDatasetMapper.selectByIds(datasetIds);
            List<DatasetSimpleVo> datasetSimpleVoList = new LinkedList<>();
            for (KnowledgeDatasetEntity entity : datasetList) {

                DatasetSimpleVo vo = new DatasetSimpleVo();
                BeanUtils.copyProperties(entity, vo);

                datasetSimpleVoList.add(vo);
            }
            applicationVo.setDatasetList(datasetSimpleVoList);
        }

        return applicationVo;
    }
}
