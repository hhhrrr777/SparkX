package sparkai.sparkaiweb.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sparkai.common.core.AjaxResult;
import sparkai.service.service.interfaces.system.IUserService;
import sparkai.service.validate.system.UserValidate;
import sparkai.service.vo.system.UserVo;

import java.util.List;

@RequestMapping("api/user")
@RestController
public class UserController {

    @Autowired
    IUserService iUserService;

    @GetMapping("/index")
    public AjaxResult<List<UserVo>> index() {
        return null;
    }

    @PostMapping("/add")
    public AjaxResult<Object> add(@RequestBody @Validated UserValidate validate) {

        iUserService.addUser(validate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    public AjaxResult<Object> edit(@RequestBody @Validated UserValidate validate) {

        iUserService.editUser(validate);
        return AjaxResult.success();
    }

    @GetMapping("/del")
    public AjaxResult<Object> del(long id) {

        iUserService.delUser(id);
        return AjaxResult.success();
    }
}
