// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.sparkaiweb.controller.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sparkai.common.core.AjaxResult;
import sparkai.common.core.PageResult;
import sparkai.service.service.interfaces.tool.IToolService;
import sparkai.service.validate.tool.AddMcpToolsValidate;
import sparkai.service.validate.tool.AddToolsValidate;
import sparkai.service.validate.tool.EditMcpToolsValidate;
import sparkai.service.validate.tool.EditToolsValidate;
import sparkai.service.vo.common.QueryVo;
import sparkai.service.vo.tool.ToolsListVo;
import sparkai.service.vo.tool.ToolsSimpleListVo;

import java.util.List;

@RequestMapping("api/tool")
@RestController
public class ToolsController {

    @Autowired
    IToolService iToolService;

    @GetMapping("/list")
    public AjaxResult<PageResult<ToolsListVo>> index(QueryVo queryVo) {

        return AjaxResult.success(iToolService.getToolList(queryVo));
    }

    /**
     * 创建插件
     */
    @PostMapping("/add")
    public AjaxResult<Object> add(@RequestBody AddToolsValidate validate) {

        iToolService.addTools(validate);
        return AjaxResult.success();
    }

    /**
     * 创建MCP插件
     */
    @PostMapping("/addMcp")
    public AjaxResult<Object> addMcp(@RequestBody AddMcpToolsValidate validate) {

        iToolService.addMcpTools(validate);
        return AjaxResult.success();
    }

    /**
     * 编辑插件
     */
    @PostMapping("/edit")
    public AjaxResult<Object> edit(@RequestBody EditToolsValidate validate) {

        iToolService.editTools(validate);
        return AjaxResult.success();
    }

    /**
     * 编辑MCP插件
     */
    @PostMapping("/editMcp")
    public AjaxResult<Object> editMcp(@RequestBody EditMcpToolsValidate validate) {

        iToolService.editMcpTools(validate);
        return AjaxResult.success();
    }

    /**
     * 删除插件
     */
    @GetMapping("/del")
    public AjaxResult<Object> del(@RequestParam("id") Integer id) {

        iToolService.delTool(id);
        return AjaxResult.success();
    }

    /**
     * 插件选择列表
     */
    @GetMapping("/toolList")
    public AjaxResult<List<ToolsSimpleListVo>> toolsList() {

        return AjaxResult.success(iToolService.getAllToolList());
    }
}