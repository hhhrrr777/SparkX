// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.sparkaiweb.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sparkai.common.core.AjaxResult;
import sparkai.common.core.PageResult;
import sparkai.service.service.interfaces.system.ISystemUserService;
import sparkai.service.validate.system.UserValidate;
import sparkai.service.vo.system.UserQueryVo;
import sparkai.service.vo.system.UsersVo;

@RequestMapping("api/user")
@RestController
public class UserController {

    @Autowired
    ISystemUserService iSystemUserService;

    /**
     * 用户列表
     */
    @GetMapping("/index")
    public AjaxResult<PageResult<UsersVo>> index(UserQueryVo queryVo) {

        return AjaxResult.success(iSystemUserService.getUserList(queryVo));
    }

    /**
     * 添加用户
     */
    @PostMapping("/add")
    public AjaxResult<Object> add(@RequestBody @Validated UserValidate validate) {

        iSystemUserService.addUser(validate);
        return AjaxResult.success();
    }

    /**
     * 编辑用户
     */
    @PostMapping("/edit")
    public AjaxResult<Object> edit(@RequestBody @Validated UserValidate validate) {

        iSystemUserService.editUser(validate);
        return AjaxResult.success();
    }

    /**
     * 删除用户
     */
    @GetMapping("/del")
    public AjaxResult<Object> del(@RequestParam("userId") String userId) {

        iSystemUserService.delUser(userId);
        return AjaxResult.success();
    }
}
