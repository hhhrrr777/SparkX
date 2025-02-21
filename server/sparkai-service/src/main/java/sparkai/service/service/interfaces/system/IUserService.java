// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.interfaces.system;

import sparkai.service.entity.system.UsersEntity;
import sparkai.service.validate.system.UserValidate;

import java.util.List;

public interface IUserService {

    /**
     * 用户列表
     * @return List<UsersEntity>
     */
    List<UsersEntity> getUserList();

    /**
     * 添加用户
     * @param validate UserValidate
     */
    void addUser(UserValidate validate);

    /**
     * 编辑用户
     * @param validate UserValidate
     */
    void editUser(UserValidate validate);

    /**
     * 删除用户
     * @param id long
     */
    void delUser(long id);
}
