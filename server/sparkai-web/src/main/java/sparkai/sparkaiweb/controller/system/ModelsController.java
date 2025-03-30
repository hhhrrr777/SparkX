package sparkai.sparkaiweb.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sparkai.common.core.AjaxResult;
import sparkai.service.service.interfaces.system.IModelsService;
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
    public AjaxResult<List<ModelsVo>> getModelList(@RequestParam("type") Integer type) {

        return AjaxResult.success(iModelsService.getModelList(type));
    }
}