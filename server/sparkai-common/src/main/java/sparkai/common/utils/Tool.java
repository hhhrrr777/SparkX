// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.common.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.crypto.SecureUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Pattern;

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

    /**
     * 清理文本杂质
     * @param input String
     * @return String
     */
    public static String cleanText(String input) {
        String result = input;
        for (int i = 0; i < PATTERNS.length; i++) {
            result = PATTERNS[i].matcher(result).replaceAll(REPLACEMENTS[i]);
        }
        return result;
    }

    private static final Pattern[] PATTERNS = {
            Pattern.compile("\\n+"),  // 合并多个换行
            Pattern.compile(" +"),    // 合并多个空格
            Pattern.compile("#+"),    // 移除所有井号
            Pattern.compile("\\t+")   // 移除所有制表符
    };

    private static final String[] REPLACEMENTS = {
            "\n", " ", "", ""
    };
}
