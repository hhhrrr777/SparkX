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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.common.core.PageResult;
import sparkai.service.entity.dataset.DatasetEntity;
import sparkai.service.mapper.dataset.DatasetMapper;
import sparkai.service.service.interfaces.dataset.IDatasetService;
import sparkai.service.vo.dataset.DatasetQueryVo;
import sparkai.service.vo.dataset.DatasetVo;

import java.util.LinkedList;
import java.util.List;

@Service
public class DatasetServiceImpl implements IDatasetService {

    @Autowired
    DatasetMapper datasetMapper;

    /**
     * 获取知识库列表
     * @param queryVo DatasetQueryVo
     * @return PageResult<DatasetVo>
     */
    @Override
    public PageResult<DatasetVo> getDatasetList(DatasetQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<DatasetEntity> queryWrapper = new QueryWrapper<>();

        if (!queryVo.getTitle().isBlank()) {
            queryWrapper.like("title", queryVo.getTitle());
        }

        // TODO 查询属于自己的知识库
        queryWrapper.eq("user_id", 1);

        queryWrapper.orderByDesc("id");
        IPage<DatasetEntity> datasetListRes = datasetMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        List<DatasetVo> datasetVoList = new LinkedList<>();

        for (DatasetEntity entity : datasetListRes.getRecords()) {

            DatasetVo vo = new DatasetVo();
            BeanUtils.copyProperties(entity, vo);

            datasetVoList.add(vo);
        }

        return PageResult.iPageHandle(datasetListRes.getTotal(), pageNo, pageSize, datasetVoList);
    }
}