package com.kl.baby.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kl.baby.config.BabyException;
import com.kl.baby.config.permission.vo.UserAccount;
import com.kl.baby.entity.User;
import com.kl.baby.entity.UserToken;
import com.kl.baby.mapper.UserMapper;
import com.kl.baby.mapper.UserTokenMapper;
import com.kl.baby.model.MobileCode;
import com.kl.baby.service.UserService;
import com.kl.baby.type.RedisKey;
import com.kl.baby.util.Md5Util;
import com.kl.baby.util.SmsService;
import com.kl.baby.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lips
 * @since 2019-06-10
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Integer MAX_TOKEN_DAY = 15;

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserTokenMapper userTokenMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private SmsService smsService;

    @Override
    public UserAccount getUserAccountByToken(String token) {
        String userCache = redisTemplate.opsForValue().get(token);
        UserAccount userAccount;
        if (!StringUtils.isEmpty(userCache)) {
            userAccount = (UserAccount) JSONUtils.parse(userCache);
        } else {
            UserToken userToken = userTokenMapper.selectByToken(token);
            if (null == userToken ||
                    System.currentTimeMillis() - userToken.getLoginTime().getTime() > 15 * 24 * 3600 * 1000) {
                return null;
            }
            User user = userMapper.selectById(userToken.getUserId());
            if (null == user) {
                return null;
            }
            userAccount = buildUserAccount(token, user, userToken.getLoginIp());
        }
        return userAccount;
    }

    @Override
    public R getRegisterCode(String mobile) {
        if (!ValidUtils.isMobile(mobile)) {
            return R.failed("手机号格式错误");
        }
        if (userMapper.selectCount(new QueryWrapper<User>().eq("phone", mobile)) > 0) {
            return R.failed("该手机号已注册");
        }
        sendSmsCode(mobile, RedisKey.REGISTER_CODE);
        return R.ok("发送成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R verifyCode(String mobile, String code, String password, String ip) {

        checkCode(mobile, RedisKey.REGISTER_CODE, code);

        User user = new User();
        user.setCreateTime(new Date());
        user.setPassword(Md5Util.md5(password));
        user.setPhone(mobile);
        userMapper.insert(user);

        return R.ok(buildUserAccount(generateToken(), user, ip));
    }

    @Override
    public R login(String mobile, String password, String ip) {
        User user = userMapper.selectByPhone(mobile);
        if (null == user) {
            return R.failed("该手机号未注册");
        }
        if (!Md5Util.md5(password).equals(user.getPassword())) {
            return R.failed("账户或密码错误！");
        }

        return R.ok(buildUserAccount(generateToken(), user, ip));
    }

    @Override
    public R lookBackCode(String mobile) {
        if (userMapper.selectCount(new QueryWrapper<User>().eq("phone", mobile)) == 0) {
            return R.failed("该手机号未注册");
        }
        sendSmsCode(mobile, RedisKey.RESET_PASSWORD_CODE);
        return R.ok("发送成功");
    }

    @Override
    public R resetPassword(String mobile, String code, String password, String ip) {

        checkCode(mobile, RedisKey.RESET_PASSWORD_CODE, code);

        return R.ok(buildUserAccount(generateToken(), userMapper.selectByPhone(mobile), ip));
    }

    private String generateToken() {
        return Md5Util.md5(UUID.randomUUID().toString());
    }

    @Transactional(rollbackFor = Exception.class)
    public UserAccount buildUserAccount(String token, User user, String ip) {
        boolean newFlag = false;
        if (null == token) {
            token = generateToken();
            newFlag = true;
        }
        UserAccount userAccount = new UserAccount();
        BeanUtils.copyProperties(user, userAccount);
        userAccount.setToken(token);
        userAccount.setLoginTime(new Date());
        userAccount.setLoginIp(ip);
        if (newFlag) {
            UserToken userToken = new UserToken();
            BeanUtils.copyProperties(userAccount, userToken);
            userTokenMapper.insert(userToken);
        }
        redisTemplate.opsForValue().set(token, JSONUtils.toJSONString(userAccount),
                MAX_TOKEN_DAY, TimeUnit.DAYS);
        return userAccount;
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendSmsCode(String mobile, String key) {
        long current = System.currentTimeMillis();
        String cache = redisTemplate.opsForValue().get(key + mobile);
        MobileCode mobileCode = null;
        if (StringUtils.isNotBlank(cache)) {
            mobileCode = JSON.parseObject(cache, MobileCode.class);
            if (current - mobileCode.getSendTime().getTime() < 60000) {
                throw BabyException.create("请" + (60 - (current - mobileCode.getSendTime().getTime()) / 1000) +
                        "秒后再发送短信验证码");
            }
            if (mobileCode.getCount() >= 3) {
                String nextTime = new DateTime(
                        new Date(mobileCode.getSendTime().getTime() - current + 24 * 3600 * 1000))
                        .toString("yyyy-MM-dd HH:mm:ss");
                throw BabyException.create("该手机号今天已发送超过三次，请【" + nextTime + "】后再试");
            }
        }
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        Map<String, String> param = new HashMap<>(1);
        param.put("code", code);
        try {
            smsService.sendSMSByAliTemp(mobile, param, "SMS_168305174");
        } catch (Exception e) {
            log.error("短信发送失败", e);
            throw BabyException.create("短信发送失败");
        }
        if (null == mobileCode) {
            mobileCode = new MobileCode(mobile, code, new Date(), key, 1);
        } else {
            mobileCode.setCount(mobileCode.getCount() + 1);
            mobileCode.setCode(code);
            mobileCode.setSendTime(new Date());
        }
        redisTemplate.opsForValue().set(key + mobile, JSONUtils.toJSONString(mobileCode));
    }

    public void checkCode(String mobile, String key, String code) {
        long current = System.currentTimeMillis();
        String cache = redisTemplate.opsForValue().get(RedisKey.REGISTER_CODE + mobile);
        if (StringUtils.isBlank(cache)) {
            throw BabyException.create("请发送短信验证码");
        }
        MobileCode mobileCode = JSON.parseObject(cache, MobileCode.class);
        if (mobileCode.getSendTime().getTime() - current > 1800000) {
            throw BabyException.create("验证码已过期,请重新发送");
        }
        if (!code.equals(mobileCode.getCode())) {
            throw BabyException.create("验证码不匹配！");
        }
    }

}
