package sparkai.common.enums;

public enum StatusEnum {

    YES(1, "正常"),
    NO(2, "禁用");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;

    StatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    /**
     * 获取状态码
     *
     * @author fzr
     * @return Long
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 获取提示
     *
     * @author fzr
     * @return String
     */
    public String getMsg() {
        return this.msg;
    }
}
