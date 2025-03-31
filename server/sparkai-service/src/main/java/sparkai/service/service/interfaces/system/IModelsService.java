// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.interfaces.system;

import sparkai.service.vo.system.ModelsInfoVo;
import sparkai.service.vo.system.ModelsVo;

import java.util.List;

public interface IModelsService {

    /**
     * 获取模型列表
     * @return List<ModelsVo>
     */
    List<ModelsVo> getModelList(Integer type);

    /**
     * 获取模型信息
     * @param modelId String
     * @return ModelsInfoVo
     */
    ModelsInfoVo getModelInfo(String modelId);

    /**
     * 编辑模型
     * @param modelsInfoVo ModelsInfoVo
     */
    void editModel(ModelsInfoVo modelsInfoVo);
}
