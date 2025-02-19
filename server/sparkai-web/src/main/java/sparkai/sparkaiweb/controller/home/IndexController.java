package sparkai.sparkaiweb.controller.home;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparkai.common.core.AjaxResult;
import sparkai.service.entity.User;
import sparkai.service.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("index")
public class IndexController {

    @Resource
    IUserService userService;

    @GetMapping("/index")
    public AjaxResult<List<User>> index() {

        List<User> res = userService.getUserList();
        return AjaxResult.success(res);
    }
}
