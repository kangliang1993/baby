package com.kl.baby.config;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 59780
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public R<Object> handle(Exception e) {
        log.error(e + "");
        if (e instanceof BabyException) {
            return R.restResult(null, MyErrorCode.fromCode(((BabyException) e)
                    .getStatus())).setMsg(e.getMessage());
        }
        //自己需要实现的异常处理
        return R.failed(e.getMessage());
    }
}