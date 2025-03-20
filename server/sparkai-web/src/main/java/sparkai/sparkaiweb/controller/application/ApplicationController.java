package sparkai.sparkaiweb.controller.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sparkai.common.core.AjaxResult;
import sparkai.common.core.PageResult;
import sparkai.service.service.interfaces.application.IApplicationService;
import sparkai.service.validate.application.ApplicationAddValidate;
import sparkai.service.vo.application.ApplicationListVo;
import sparkai.service.vo.application.ApplicationQueryVo;

@RequestMapping("/api/application")
@RestController
public class ApplicationController {

    @Autowired
    IApplicationService iApplicationService;

    /**
     * 应用列表
     */
    @GetMapping("/list")
    public AjaxResult<PageResult<ApplicationListVo>> list(ApplicationQueryVo queryVo) {

        return AjaxResult.success(iApplicationService.getApplicationList(queryVo));
    }

    /**
     * 添加应用
     */
    @PostMapping("/add")
    public AjaxResult<Object> add(@RequestBody @Validated ApplicationAddValidate validate) {

        iApplicationService.addApplication(validate);
        return AjaxResult.success();
    }
}