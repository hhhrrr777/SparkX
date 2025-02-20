package sparkai.sparkaiweb.controller.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparkai.common.core.AjaxResult;
import sparkai.service.entity.system.UsersEntity;
import sparkai.service.service.interfaces.system.IUserService;

import java.util.List;

@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    IUserService iUserService;

    @GetMapping("/index")
    public AjaxResult<List<UsersEntity>> index() {

        List<UsersEntity> res = iUserService.getUserList();
        return AjaxResult.success(res);
    }
}
