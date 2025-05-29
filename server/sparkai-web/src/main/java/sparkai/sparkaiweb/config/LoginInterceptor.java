package sparkai.sparkaiweb.config;

import cn.hutool.jwt.JWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import sparkai.common.constant.SparkAIConstant;
import sparkai.service.helper.UserContextHelper;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    // 创建登录身份校验拦截器
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 令牌验证
        String token = request.getHeader("Authorization");
        try {

            token = token.replace("Bearer ", "");
            boolean validate = JWT.of(token).setKey(SparkAIConstant.CommonData.passwordSalt.getBytes()).validate(0);
            if (!validate) {
               throw new Exception();
            }

            // 放行
            return true;

        } catch (Exception e) {
            // 设置响应状态码
            response.setStatus(401);
            // 设置响应字符集和响应内容
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            String errorMessage = "未登录";
            response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
            // 不放行
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除 ThreadLocal 中的用户数据，避免内存泄漏
        UserContextHelper.clearUser();
    }
}
