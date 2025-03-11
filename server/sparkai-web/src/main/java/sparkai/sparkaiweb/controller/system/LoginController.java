package sparkai.sparkaiweb.controller.system;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparkai.common.core.AjaxResult;

@RequestMapping("api/login")
@RestController
public class LoginController {

    /**
     * 登录
     */
    @PostMapping("/doLogin")
    public AjaxResult<Object> login() {
        return AjaxResult.success();
    }
}
