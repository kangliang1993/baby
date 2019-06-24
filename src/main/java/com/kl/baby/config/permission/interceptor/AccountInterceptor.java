package com.kl.baby.config.permission.interceptor;


import com.alibaba.druid.util.StringUtils;
import com.kl.baby.config.BabyException;
import com.kl.baby.config.permission.annotation.Account;
import com.kl.baby.config.permission.vo.UserAccount;
import com.kl.baby.service.UserService;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author qiang on 2018/2/1.
 */
public class AccountInterceptor extends HandlerInterceptorAdapter {

    private static final String AUTHORIZATION = "Authorization";

    private UserService userService;

    public AccountInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        } else {
            if (request.getRequestURI().startsWith("/partner/v1")) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Account methodAnnotation = handlerMethod.getMethodAnnotation(Account.class);
                if (methodAnnotation == null || methodAnnotation.value()) {
                    UserAccount accountObject = this.getAccountObject(request);
                    if (accountObject == null) {
                        throw BabyException.create(-2, "登录失败");
                    }
                    request.setAttribute("account_object", accountObject);
                    return true;
                }
            }
            return super.preHandle(request, response, handler);
        }
    }

    private UserAccount getAccountObject(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader(AUTHORIZATION);
        if (!StringUtils.isEmpty(token)) {
            return userService.getUserAccountByToken(token);
        }
        return null;
    }


}
