package com.kl.baby.config.permission.vo;

import com.kl.baby.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserAccount {
    private Integer id;

    private String phone;

    private String userName;

    private Integer sex;

    private String avatar;

    private String token;

    private Date loginTime;

    private String loginIp;
}
