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

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.system.ModelsEntity;
import sparkai.service.mapper.system.ModelsMapper;
import sparkai.service.service.interfaces.system.IModelsService;
import sparkai.service.vo.system.ModelsInfoVo;
import sparkai.service.vo.system.ModelsVo;

import java.util.LinkedList;
import java.util.List;

@Service
public class ModelsServiceImpl implements IModelsService {

    @Autowired
    ModelsMapper modelsMapper;

    /**
     * 获取模型列表
     * @param type Integer
     * @return List<ModelsVo>
     */
    @Override
    public List<ModelsVo> getModelList(Integer type) {

        List<ModelsEntity> modelsList = modelsMapper.selectList(new QueryWrapper<ModelsEntity>().eq("type", type));

        List<ModelsVo> modelsVoList = new LinkedList<>();
        for (ModelsEntity modelsEntity : modelsList) {
            ModelsVo modelsVo = new ModelsVo();
            BeanUtils.copyProperties(modelsEntity, modelsVo);

            modelsVoList.add(modelsVo);
        }

        return modelsVoList;
    }

    /**
     * 获取模型信息
     * @param modelId String
     * @return ModelsInfoVo
     */
    @Override
    public ModelsInfoVo getModelInfo(String modelId) {

        ModelsEntity info = modelsMapper.selectById(modelId);
        ModelsInfoVo infoVo = new ModelsInfoVo();
        BeanUtils.copyProperties(info, infoVo);

        return infoVo;
    }

    /**
     * 编辑模型
     * @param modelsInfoVo ModelsInfoVo
     */
    @Override
    public void editModel(ModelsInfoVo modelsInfoVo) {

        if (modelsInfoVo.getStatus().equals(1)) {
            JSONArray credential = JSONUtil.parseArray(modelsInfoVo.getCredential());

            for (int i = 0; i < credential.size(); i ++) {
                String value = credential.getJSONObject(i).getStr("value");
                if (value.isBlank()) {
                    throw new BusinessException(credential.getJSONObject(i).getStr("field") + "不能为空");
                }
            }
        }

        ModelsEntity info = modelsMapper.selectById(modelsInfoVo.getModelId());
        BeanUtils.copyProperties(modelsInfoVo, info);
        info.setUpdateTime(Tool.nowDateTime());

        modelsMapper.updateById(info);
    }
}
