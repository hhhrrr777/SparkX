// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.helper;

import org.springframework.stereotype.Component;
import sparkai.service.vo.system.LocalUserVo;

@Component
public class UserContextHelper {

    // 创建一个 ThreadLocal 实例来存储 User 对象
    private static final ThreadLocal<LocalUserVo> userThreadLocal = new ThreadLocal<>();

    // 设置用户数据
    public static void setUser(LocalUserVo user) {
        userThreadLocal.set(user);
    }

    // 获取用户数据
    public static LocalUserVo getUser() {
        return userThreadLocal.get();
    }

    // 删除用户数据
    public static void clearUser() {
        userThreadLocal.remove();
    }
}