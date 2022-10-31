package com.chen.user.service;

import com.chen.user.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.user.entity.User;
import com.chen.user.entity.dto.TeamQuery;
import com.chen.user.entity.request.TeamJoinRequest;
import com.chen.user.entity.request.TeamQuitRequest;
import com.chen.user.entity.request.TeamUpdateRequest;
import com.chen.user.entity.vo.TeamUserVo;

import java.util.List;

/**
 * 团队服务
 *
 * @author Chenchenx
 * @description 针对表【team(队伍表)】的数据库操作Service
 * @createDate 2022-08-21 23:10:26
 * @date 2022/09/05
 */
public interface TeamService extends IService<Team> {

    /**
     * 加入团队
     * 创建队伍
     *
     * @param team      team
     * @param loginUser loginUser
     * @return long teamId
     */
    Long addTeam(Team team, User loginUser);

    /**
     * 团队名单
     * 搜索队伍
     *
     * @param teamQuery 团队查询
     * @param isAdmin   是管理
     * @return {@code List<TeamUserVo>}
     */
    List<TeamUserVo> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新团队
     * 更新队伍
     *
     * @param teamUpdateRequest 团队更新请求
     * @param loginUser         登录用户
     * @return boolean
     */

    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入团队
     *
     * @param teamJoinRequest 团队加入请求
     * @param loginUser       登录用户
     * @return boolean
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出团队
     *
     * @param teamQuitRequest 团队辞职请求
     * @param loginUser       登录用户
     * @return boolean
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除团队
     *
     * @param id        id
     * @param loginUser 登录用户
     * @return boolean
     */
    boolean deleteTeam(long id, User loginUser);
}
