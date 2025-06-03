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

import sparkai.common.core.AjaxResult;
import sparkai.service.validate.system.AddTeamUserValidate;
import sparkai.service.vo.system.TeamUserVo;
import sparkai.service.vo.system.UsersVo;

import java.util.List;

public interface ITeamService {

    /**
     * 获取团队成员
     * @return List<TeamUserVo>
     */
    List<TeamUserVo> getTeamUserList();

    /**
     * 搜索用户
     * @param nickname String
     * @return List<UsersVo>
     */
    List<UsersVo> searchUser(String nickname);

    /**
     * 添加团队成员
     * @param validate AddTeamUserValidate
     * @return String
     */
    String addUser(AddTeamUserValidate validate);

    /**
     * 删除用户
     * @param userId String
     */
    void delUser(String userId);
}
