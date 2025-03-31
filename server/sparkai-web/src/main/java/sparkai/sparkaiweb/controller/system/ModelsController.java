package sparkai.sparkaiweb.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sparkai.common.core.AjaxResult;
import sparkai.service.service.interfaces.system.IModelsService;
import sparkai.service.vo.system.ModelsInfoVo;
import sparkai.service.vo.system.ModelsVo;

import java.util.List;

@RequestMapping("api/models")
@RestController
public class ModelsController {

    @Autowired
    IModelsService iModelsService;

    /**
     * 模型列表
     */
    @GetMapping("/list")
    public AjaxResult<List<ModelsVo>> modelList(@RequestParam("type") Integer type) {

        return AjaxResult.success(iModelsService.getModelList(type));
    }

    /**
     * 模型信息
     */
    @GetMapping("/info")
    public AjaxResult<ModelsInfoVo> modeInfo(@RequestParam("modelId") String modelId) {

        return AjaxResult.success(iModelsService.getModelInfo(modelId));
    }

    /**
     * 编辑模型
     */
    @PostMapping("/edit")
    public AjaxResult<Object> edit(@RequestBody ModelsInfoVo modelsInfoVo) {

        iModelsService.editModel(modelsInfoVo);
        return AjaxResult.success();
    }
}