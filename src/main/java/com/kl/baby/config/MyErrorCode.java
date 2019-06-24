package com.kl.baby.config;

import com.baomidou.mybatisplus.extension.api.IErrorCode;

/**
 * @author 59780
 */
public enum MyErrorCode implements IErrorCode {
    FAILED(-1L, "操作失败"),
    SUCCESS(0L, "执行成功"),
    NOAUTH(-2L, "尚未登录");
    private final long code;
    private final String msg;

    MyErrorCode(final long code, final String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static MyErrorCode fromCode(long code) {
        MyErrorCode[] ecs = values();
        MyErrorCode[] var3 = ecs;
        int var4 = ecs.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            MyErrorCode ec = var3[var5];
            if (ec.getCode() == code) {
                return ec;
            }
        }

        return SUCCESS;
    }

    @Override
    public long getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public String toString() {
        return String.format(" ErrorCode:{code=%s, msg=%s} ", this.code, this.msg);
    }
}
