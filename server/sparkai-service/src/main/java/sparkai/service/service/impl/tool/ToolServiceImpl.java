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
import org.springframework.transaction.annotation.Transactional;
import sparkai.common.core.PageResult;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.application.ApplicationToolRelationEntity;
import sparkai.service.entity.tool.ToolsEntity;
import sparkai.service.mapper.application.ApplicationToolRelationMapper;
import sparkai.service.mapper.tool.ToolsMapper;
import sparkai.service.service.interfaces.tool.IToolService;
import sparkai.service.validate.tool.AddToolsValidate;
import sparkai.service.validate.tool.EditToolsValidate;
import sparkai.service.vo.common.QueryVo;
import sparkai.service.vo.tool.ToolsListVo;
import sparkai.service.vo.tool.ToolsSimpleListVo;

import java.util.LinkedList;
import java.util.List;

@Service
public class ToolServiceImpl implements IToolService {

    @Autowired
    ToolsMapper toolsMapper;

    @Autowired
    ApplicationToolRelationMapper applicationToolRelationMapper;

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

        ToolsEntity info = toolsMapper.selectOne(new QueryWrapper<ToolsEntity>().eq("name", validate.getName()));
        if (info != null) {
            throw new BusinessException("该插件标识已经存在");
        }

        ToolsEntity toolsEntity = new ToolsEntity();
        BeanUtils.copyProperties(validate, toolsEntity);
        toolsEntity.setCreateTime(Tool.nowDateTime());

        toolsMapper.insert(toolsEntity);
    }

    /**
     * 编辑插件
     * @param validate EditToolsValidate
     */
    @Override
    public void editTools(EditToolsValidate validate) {

        if (!validate.getName().matches("^[a-zA-Z_]+$")) {
            throw new BusinessException("插件标识只包含英文字母和下划线");
        }

        if (validate.getAuthType().equals(2) &&
                (validate.getApiKeyName().isBlank() || validate.getApiKeyValue().isBlank())) {
            throw new BusinessException("秘钥信息不能为空");
        }

        ToolsEntity info = toolsMapper.selectOne(new QueryWrapper<ToolsEntity>()
                .eq("name", validate.getName()).ne("id", validate.getId()));
        if (info != null) {
            throw new BusinessException("该插件标识已经存在");
        }

        ToolsEntity toolsEntity = new ToolsEntity();
        BeanUtils.copyProperties(validate, toolsEntity);
        toolsEntity.setUpdateTime(Tool.nowDateTime());

        toolsMapper.updateById(toolsEntity);
    }

    /**
     * 删除插件
     * @param id Integer
     */
    @Override
    @Transactional
    public void delTool(Integer id) {

        toolsMapper.deleteById(id);
        applicationToolRelationMapper.delete(new QueryWrapper<ApplicationToolRelationEntity>().eq("tool_id", id));
    }

    /**
     * 获取插件选择列表
     * @return List<ToolsSimpleListVo>
     */
    @Override
    public List<ToolsSimpleListVo> getAllToolList() {

        List<ToolsEntity> toolList = toolsMapper.selectList(new QueryWrapper<ToolsEntity>().orderByDesc("id"));
        List<ToolsSimpleListVo> toolsSimpleListVoList = new LinkedList<>();

        for (ToolsEntity entity : toolList) {
            ToolsSimpleListVo vo = new ToolsSimpleListVo();
            BeanUtils.copyProperties(entity, vo);
            toolsSimpleListVoList.add(vo);
        }

        return toolsSimpleListVoList;
    }
}
