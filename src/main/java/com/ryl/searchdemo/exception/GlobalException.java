package com.ryl.searchdemo.exception;

import com.ryl.searchdemo.constants.GlobalCodeEnum;
import lombok.Data;

/**
 * @author: ryl
 * @description:
 * @date: 2020-08-04 20:04:16
 */
@Data
public class GlobalException extends RuntimeException {

    /**
     * 错误码
     */
    private int code;

    GlobalException(GlobalCodeEnum codeEnum) {
        this(codeEnum.getCode(),codeEnum.getMsg());
    }

    GlobalException(GlobalCodeEnum codeEnum, String msg) {
        this(codeEnum.getCode(), msg);
    }

    GlobalException(int code, String msg) {
        super(msg);
        this.code = code;
    }
}
