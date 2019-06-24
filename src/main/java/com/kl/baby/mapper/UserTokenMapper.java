package com.kl.baby.mapper;

import com.kl.baby.entity.UserToken;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lips
 * @since 2019-06-10
 */
public interface UserTokenMapper extends BaseMapper<UserToken> {

    UserToken selectByToken(@Param("token") String token);
}
