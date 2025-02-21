package sparkai.common.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.crypto.SecureUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Tool {

    /**
     * 生成密码
     * @param password String
     * @param salt String
     * @return String
     */
    public static String makePassword(String password, String salt) {
        return SecureUtil.md5(SecureUtil.md5(password + salt) + salt);
    }

    /**
     * 验证密码
     * @param dbPassword String
     * @param password String
     * @param salt String
     * @return String
     */
    public static boolean verifyPassword(String dbPassword, String password, String salt) {
        return Objects.equals(dbPassword, makePassword(password, salt));
    }

    /**
     * 获取当前时间
     * @return LocalDateTime
     */
    public static LocalDateTime nowDateTime() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse((new DateTime()).toString(), df);
    }

    /**
     * 给定的时间转格式
     * @param time String
     * @ LocalDateTime
     */
    public static LocalDateTime time2LocalDateTime(String time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, df);
    }
}
