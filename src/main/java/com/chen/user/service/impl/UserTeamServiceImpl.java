package com.chen.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.user.entity.UserTeam;
import com.chen.user.service.UserTeamService;
import com.chen.user.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Chenchenx
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2022-08-21 23:10:26
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




