// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.impl.tool;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.common.core.PageResult;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.tool.ToolsEntity;
import sparkai.service.mapper.tool.ToolsMapper;
import sparkai.service.service.interfaces.tool.IToolService;
import sparkai.service.validate.tool.AddToolsValidate;
import sparkai.service.vo.common.QueryVo;
import sparkai.service.vo.tool.ToolsListVo;

import java.util.LinkedList;
import java.util.List;

@Service
public class ToolServiceImpl implements IToolService {

    @Autowired
    ToolsMapper toolsMapper;

    /**
     * 获取工具列表
     * @param queryVo QueryVo
     */
    @Override
    public PageResult<ToolsListVo> getToolList(QueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<ToolsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<ToolsEntity> toolsListRes = toolsMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        List<ToolsListVo> toolsVoList = new LinkedList<>();

        for (ToolsEntity entity : toolsListRes.getRecords()) {

            ToolsListVo vo = new ToolsListVo();
            BeanUtils.copyProperties(entity, vo);

            toolsVoList.add(vo);
        }

        return PageResult.iPageHandle(toolsListRes.getTotal(), pageNo, pageSize, toolsVoList);
    }

    /**
     * 添加插件
     * @param validate AddToolsValidate
     */
    @Override
    public void addTools(AddToolsValidate validate) {

        if (!validate.getName().matches("^[a-zA-Z_]+$")) {
            throw new BusinessException("插件标识只包含英文字母和下划线");
        }

        if (validate.getAuthType().equals(2) &&
                (validate.getApiKeyName().isBlank() || validate.getApiKeyValue().isBlank())) {
            throw new BusinessException("秘钥信息不能为空");
        }

        ToolsEntity toolsEntity = new ToolsEntity();
        BeanUtils.copyProperties(validate, toolsEntity);
        toolsEntity.setCreateTime(Tool.nowDateTime());

        toolsMapper.insert(toolsEntity);
    }
}
