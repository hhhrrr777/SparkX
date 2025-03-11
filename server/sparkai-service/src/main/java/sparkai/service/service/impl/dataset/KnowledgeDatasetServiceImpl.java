// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.impl.dataset;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.common.core.PageResult;
import sparkai.common.utils.Tool;
import sparkai.service.entity.dataset.KnowledgeDatasetEntity;
import sparkai.service.entity.system.SystemUsersEntity;
import sparkai.service.mapper.dataset.KnowledgeDatasetMapper;
import sparkai.service.mapper.system.SystemUserMapper;
import sparkai.service.service.interfaces.dataset.IKnowledgeDatasetService;
import sparkai.service.validate.dataset.DatasetValidate;
import sparkai.service.vo.dataset.DatasetQueryVo;
import sparkai.service.vo.dataset.DatasetVo;

import java.util.LinkedList;
import java.util.List;

@Service
public class KnowledgeDatasetServiceImpl implements IKnowledgeDatasetService {

    @Autowired
    KnowledgeDatasetMapper datasetMapper;

    @Autowired
    SystemUserMapper userMapper;

    /**
     * 获取知识库列表
     * @param queryVo DatasetQueryVo
     * @return PageResult<DatasetVo>
     */
    @Override
    public PageResult<DatasetVo> getDatasetList(DatasetQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<KnowledgeDatasetEntity> queryWrapper = new QueryWrapper<>();

        if (!queryVo.getTitle().isBlank()) {
            queryWrapper.like("title", queryVo.getTitle());
        }

        // TODO 查询属于自己的知识库
        queryWrapper.eq("user_id", "b6c67084-ad55-4ced-82c4-4d9d304e8616");

        queryWrapper.orderByDesc("create_time");
        IPage<KnowledgeDatasetEntity> datasetListRes = datasetMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        List<DatasetVo> datasetVoList = new LinkedList<>();

        for (KnowledgeDatasetEntity entity : datasetListRes.getRecords()) {
            DatasetVo vo = new DatasetVo();
            BeanUtils.copyProperties(entity, vo);

            SystemUsersEntity userInfo = userMapper.selectById(entity.getUserId());
            vo.setAuthor(userInfo.getNickname());

            datasetVoList.add(vo);
        }

        return PageResult.iPageHandle(datasetListRes.getTotal(), pageNo, pageSize, datasetVoList);
    }

    /**
     * 添加知识库模型
     * @param validate DatasetValidate
     */
    @Override
    public void addDataset(DatasetValidate validate) {

        KnowledgeDatasetEntity datasetEntity = new KnowledgeDatasetEntity();
        BeanUtils.copyProperties(validate, datasetEntity);

        // TODO 此处的uuid随机生成
        datasetEntity.setType(1); // 写死通用类型
        datasetEntity.setUserId("b6c67084-ad55-4ced-82c4-4d9d304e8616");
        datasetEntity.setDatasetId(IdUtil.randomUUID());
        datasetEntity.setEmbeddingModeId(IdUtil.randomUUID());
        datasetEntity.setCreateTime(Tool.nowDateTime());

        datasetMapper.insert(datasetEntity);
    }
}