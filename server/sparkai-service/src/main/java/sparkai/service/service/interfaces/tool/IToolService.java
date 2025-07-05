// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.interfaces.tool;

import sparkai.common.core.PageResult;
import sparkai.service.validate.tool.AddToolsValidate;
import sparkai.service.validate.tool.EditToolsValidate;
import sparkai.service.vo.common.QueryVo;
import sparkai.service.vo.tool.ToolsListVo;

public interface IToolService {

    /**
     * 获取工具列表
     * @param queryVo QueryVo
     */
    PageResult<ToolsListVo> getToolList(QueryVo queryVo);

    /**
     * 添加插件
     * @param validate AddToolsValidate
     */
    void addTools(AddToolsValidate validate);

    /**
     * 编辑插件
     * @param validate EditToolsValidate
     */
    void editTools(EditToolsValidate validate);
}
