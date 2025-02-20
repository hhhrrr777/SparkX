package sparkai.sparkaiweb.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sparkai.common.core.AjaxResult;
import sparkai.common.enums.ExceptionEnum;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.ErrorUtil;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerConfig {

    /**
     * 业务异常处理
     *
     * @param e 业务异常
     * @return AjaxResult<Object>
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public AjaxResult<Object> exceptionHandler(BusinessException e) {
        log.info("业务信息" + e.getErrorMsg());
        return AjaxResult.failed(e.getCode(), e.getErrorMsg());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public AjaxResult<Object> exceptionHandler(NullPointerException e) {
        log.error(ErrorUtil.errorInfoToString(e));
        return AjaxResult.failed(Integer.valueOf(ExceptionEnum.INTERNAL_SERVER_ERROR.getCode()),
                ExceptionEnum.INTERNAL_SERVER_ERROR.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    private AjaxResult<Object> handleIllegalArgumentException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        String message = "";
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            if (errors != null) {
                errors.forEach(p -> {
                    FieldError fieldError = (FieldError) p;
                    log.error("Data check failure : object{" + fieldError.getObjectName() + "},field{" + fieldError.getField() +
                            "},errorMessage{" + fieldError.getDefaultMessage() + "}");

                });
                if (errors.size() > 0) {
                    FieldError fieldError = (FieldError) errors.get(0);
                    message = fieldError.getDefaultMessage();
                }
            }
        }

        return AjaxResult.failed(Integer.valueOf(ExceptionEnum.BAD_REQUEST.getCode()), message);
    }

    /**
     * 未知异常处理
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public AjaxResult<Object> exceptionHandler(Exception e) {
        // 把错误信息输入到日志中
        log.error(ErrorUtil.errorInfoToString(e));
        return AjaxResult.failed(Integer.valueOf(ExceptionEnum.UNKNOWN.getCode()), ExceptionEnum.UNKNOWN.getMsg());
    }
}
