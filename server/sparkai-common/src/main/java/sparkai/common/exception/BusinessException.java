package sparkai.common.exception;

import sparkai.common.enums.ExceptionEnum;

public class BusinessException extends RuntimeException {

    private ExceptionEnum exceptionEnum;
    private Integer code;
    private String errorMsg;

    public BusinessException() {
        super();
    }

    public BusinessException(ExceptionEnum exceptionEnum) {
        super("{code:" + exceptionEnum.getCode() + ",errorMsg:" + exceptionEnum.getMsg() + "}");
        this.exceptionEnum = exceptionEnum;
        this.code = exceptionEnum.getCode();
        this.errorMsg = exceptionEnum.getMsg();
    }

    public BusinessException(Integer code, String errorMsg) {
        super("{code:" + code + ",errorMsg:" + errorMsg + "}");
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public BusinessException(String errorMsg) {
        super("{code:" + ExceptionEnum.BAD_REQUEST.getCode() + ",errorMsg:" + errorMsg + "}");
        this.code = ExceptionEnum.BAD_REQUEST.getCode();
        this.errorMsg = errorMsg;
    }

    public BusinessException(Integer code, String errorMsg, Object... args) {
        super("{code:" + code + ",errorMsg:" + String.format(errorMsg, args) + "}");
        this.code = code;
        this.errorMsg = String.format(errorMsg, args);
    }

    public ExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
