package com.ryl.searchdemo.handler;

import com.ryl.framework.base.ResultModel;
import com.ryl.searchdemo.constants.GlobalCodeEnum;
import com.ryl.searchdemo.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: ryl
 * @description:
 * @date: 2020-08-04 20:01:20
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获全局异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultModel exceptionHandle(Exception e) {
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            BindingResult bindingResult = exception.getBindingResult();
            FieldError fieldError = bindingResult.getFieldError();
            return ResultModel.fail(GlobalCodeEnum.RETURN_CODE_401.getCode(),fieldError.getDefaultMessage());
        } else if (e instanceof GlobalException) {
            return ResultModel.fail(((GlobalException) e).getCode(), e.getMessage());
        } else {
            return ResultModel.fail(GlobalCodeEnum.RETURN_CODE_500.getCode(),GlobalCodeEnum.RETURN_CODE_500.getMsg());
        }
    }
}
