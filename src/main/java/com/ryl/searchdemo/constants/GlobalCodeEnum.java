package com.ryl.searchdemo.constants;

/**
 * @author: ryl
 * @description:
 * @date: 2020-08-04 20:06:18
 */
public enum GlobalCodeEnum {

    RETURN_CODE_401(401,"参数错误"),
    RETURN_CODE_500(500,"服务器内部错误")
    ;



    private final int code;

    private final String msg;

    GlobalCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
