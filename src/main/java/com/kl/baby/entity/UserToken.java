package com.kl.baby.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lips
 * @since 2019-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_token")
public class UserToken implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String token;

    private Integer userId;

    private Date loginTime;

    private Date outTime;

    private String loginIp;


}
