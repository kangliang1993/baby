package com.kl.baby.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.kl.baby.config.permission.vo.UserAccount;
import com.kl.baby.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lips
 * @since 2019-06-10
 */
public interface UserService extends IService<User> {

    UserAccount getUserAccountByToken(String token);

    R getRegisterCode(String mobile);

    R verifyCode(String mobile, String code, String password, String ip);

    R login(String mobile, String password, String ip);

    R lookBackCode(String mobile);

    R resetPassword(String mobile, String code, String password, String ip);
}
