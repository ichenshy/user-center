package com.chen.user.mapper;

import com.chen.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Galaxy
 * @description 针对表【user(用户)】的数据库操作Mapper
 * @createDate 2022-05-19 15:33:53
 * @Entity com.chen.user.domain.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




