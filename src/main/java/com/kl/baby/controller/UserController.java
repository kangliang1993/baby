package com.kl.baby.controller;


import com.baomidou.mybatisplus.extension.api.R;
import com.kl.baby.config.permission.annotation.Account;
import com.kl.baby.service.UserService;
import com.kl.baby.util.IpUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lips
 * @since 2019-06-10
 */
@RestController
@RequestMapping("v1/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping(value = "registerCode/{mobile}")
    @ApiOperation(value = "注册获取短信验证码", notes = "输入手机号获取短信验证码")
    @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "path")
    @Account(false)
    public R getRegisterCode(@PathVariable String mobile) {
        return userService.getRegisterCode(mobile);
    }

    @PostMapping(value = "verifyCode")
    @ApiOperation(value = "完成注册、并登录", notes = "成功会返回用户token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query")
    })
    @Account(false)
    public R verifyCode(@RequestParam String mobile, @RequestParam String code, @RequestParam String password,
                        @ApiIgnore HttpServletRequest request) {
        return userService.verifyCode(mobile, code, password, IpUtil.getIpAddr(request));
    }


    @PostMapping(value = "login")
    @ApiOperation(value = "用户登录", notes = "手机号和密码来登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query")
    })
    @Account(false)
    public R login(@RequestParam String mobile, @RequestParam String password, @ApiIgnore HttpServletRequest request) {
        return userService.login(mobile, password, IpUtil.getIpAddr(request));
    }

    @GetMapping(value = "lookBackCode/{mobile]")
    @ApiOperation(value = "找回密码获取短信验证码", notes = "输入手机号获取短信验证码")
    @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "path")
    @Account(false)
    public R lookBackCode(@PathVariable String mobile) {
        return userService.lookBackCode(mobile);
    }

    @GetMapping(value = "resetPassword")
    @ApiOperation(value = "找回密码", notes = "找回密码确认")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query")
    })
    @Account(false)
    public R resetPassword(@RequestParam String mobile, @RequestParam String code, @RequestParam String password,
                           @ApiIgnore HttpServletRequest request) {
        return userService.resetPassword(mobile, code, password, IpUtil.getIpAddr(request));
    }
}
