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
import sparkai.service.validate.tool.AddMcpToolsValidate;
import sparkai.service.validate.tool.AddToolsValidate;
import sparkai.service.validate.tool.EditMcpToolsValidate;
import sparkai.service.validate.tool.EditToolsValidate;
import sparkai.service.vo.common.QueryVo;
import sparkai.service.vo.tool.ToolQueryVo;
import sparkai.service.vo.tool.ToolsListVo;
import sparkai.service.vo.tool.ToolsSimpleListVo;

import java.util.List;

public interface IToolService {

    /**
     * 获取工具列表
     * @param queryVo ToolQueryVo
     */
    PageResult<ToolsListVo> getToolList(ToolQueryVo queryVo);

    /**
     * 添加插件
     * @param validate AddToolsValidate
     */
    void addTools(AddToolsValidate validate);

    /**
     * 添加mcp插件
     * @param validate AddMcpToolsValidate
     */
    void addMcpTools(AddMcpToolsValidate validate);

    /**
     * 编辑插件
     * @param validate EditToolsValidate
     */
    void editTools(EditToolsValidate validate);

    /**
     * 编辑MCP插件
     * @param validate EditMcpToolsValidate
     */
    void editMcpTools(EditMcpToolsValidate validate);

    /**
     * 删除插件
     * @param id Integer
     */
    void delTool(Integer id);

    /**
     * 获取全部的插件列表
     * @return List<ToolsSimpleListVo>
     */
    List<ToolsSimpleListVo> getAllToolList();
}
