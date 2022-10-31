package com.chen.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.user.common.ErrorCode;
import com.chen.user.entity.Team;
import com.chen.user.entity.User;
import com.chen.user.entity.UserTeam;
import com.chen.user.entity.dto.TeamQuery;
import com.chen.user.entity.request.TeamJoinRequest;
import com.chen.user.entity.request.TeamQuitRequest;
import com.chen.user.entity.request.TeamUpdateRequest;
import com.chen.user.entity.vo.TeamUserVo;
import com.chen.user.entity.vo.UserVo;
import com.chen.user.enums.TeamStatusEnum;
import com.chen.user.exception.BusinessException;
import com.chen.user.mapper.TeamMapper;
import com.chen.user.service.TeamService;
import com.chen.user.service.UserService;
import com.chen.user.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 针对表【team(队伍表)】的数据库操作Service实现
 *
 * @author chenshy
 * @createDate 2022-08-21 23:10:26
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;

    /**
     * 创建队伍
     *
     * @param team      team
     * @param loginUser loginUser
     * @return long teamId
     * @description @Transactional 注解 开启数据库事务，要么成功要么回滚
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTeam(Team team, User loginUser) {
        final long userId = loginUser.getId();
        // 1、请求参数为空
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 2、检验信息
        //   1、队伍人数 >1 <=20
        Integer maxNum = team.getMaxNum();
        if (maxNum < 1 || maxNum >= 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "人数不满足要求");
        }
        //  2、 队伍标题 不能为空 且 <=20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() >= 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        //  3、 描述为空 队伍描述 <=512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() >= 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        //  4. status 是否公开  不传默认 0
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        //   5. 如果 status 是加密状态，一定要有密码，且密码 <= 32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.length() >= 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }
        // 6. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
        }
        // 7. 校验用户最多创建 5 个队伍
        // TODO 有 bug，可能同时创建 100 个队伍
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getUserId, userId);
        Long count = baseMapper.selectCount(wrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建 5 个队伍");
        }
        // 8. 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean save = this.save(team);
        Long teamId = team.getId();
        if (!save || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        // 9. 插入用户  => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        return teamId;
    }

    /**
     * 团队名单
     * 查询队伍
     *
     * @param teamQuery 团队查询
     * @param isAdmin   是管理
     * @return {@code List<TeamUserVo>}
     */
    @Override
    public List<TeamUserVo> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                wrapper.eq(Team::getId, id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                wrapper.in(Team::getId, idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                wrapper.like(Team::getName, searchText).or().like(Team::getDescription, searchText);
            }
            // 名称
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                wrapper.like(Team::getName, name);
            }
            // 描述
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                wrapper.like(Team::getDescription, description);
            }
            // 最大人数
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                wrapper.eq(Team::getMaxNum, maxNum);
            }
            // 创建者
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                wrapper.eq(Team::getUserId, userId);
            }
            // 状态
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NOT_AUTH, "无权限");
            }
            wrapper.eq(Team::getStatus, statusEnum.getValue());
        }
        // 不展示已过期的队伍
        wrapper.gt(Team::getExpireTime, new Date()).or().isNull(Team::getExpireTime);
        List<Team> teamList = this.list(wrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        ArrayList<TeamUserVo> teamUserVos = new ArrayList<>();
        // 关联查询用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            User user = userService.getById(userId);
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            // 脱敏用户信息
            if (user != null) {
                UserVo userVo = new UserVo();
                BeanUtils.copyProperties(user, userVo);
                teamUserVo.setCreateUser(userVo);
            }
            teamUserVos.add(teamUserVo);
        }
        return teamUserVos;
    }

    /**
     * 更新团队
     * 修改队伍
     *
     * @param teamUpdateRequest 团队更新请求
     * @param loginUser         登录用户
     * @return boolean
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        Team oldTeam = getTeamById(id);
        if (!oldTeam.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须要设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }

    /**
     * 加入团队
     *
     * @param teamJoinRequest 团队加入请求
     * @param loginUser       登录用户
     * @return boolean
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        //TODO 分布式锁
        Long userId = loginUser.getId();
        LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTeam::getUserId, userId);
        long hasJoinNum = userTeamService.count(wrapper);
        if (hasJoinNum > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入 5 个队伍");
        }
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTeam::getUserId, userId).eq(UserTeam::getTeamId, teamId);
        long hasUserJoinTeam = userTeamService.count(wrapper);
        if (hasUserJoinTeam > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍");
        }
        long teamHasJoinNum = getTeamUserByTeamId(teamId);
        if (teamHasJoinNum >= team.getMaxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    /**
     * 退出团队
     *
     * @param teamQuitRequest 团队辞职请求
     * @param loginUser       登录用户
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询出队伍的id
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        // 获取当前登录用户的id
        long userId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>(userTeam);
        // 查询当前用户已经入的队伍数量
        long count = userTeamService.count(wrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }
        // 根据队伍id查询加入队伍的人数
        long teamJoinNum = getTeamUserByTeamId(teamId);
        // 只剩余一人
        if (teamJoinNum == 1) {
            // 删除队伍
            // deleteTeam(teamId,loginUser);
            this.removeById(teamId);
        } else {
            // 判断是否为队长
            if (team.getUserId() == userId) {
                // 转移队伍给最早加入的人
                wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserTeam::getTeamId, teamId).last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(wrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamUserId = nextUserTeam.getUserId();
                // 更新队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamUserId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队长失败");
                }
            }
        }
        // 删除队伍关系关系
        return userTeamService.remove(wrapper);
    }


    /**
     * 删除团队
     *
     * @param id        id
     * @param loginUser 登录用户
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id, User loginUser) {
        Team team = this.getTeamById(id);
        Long teamId = team.getId();
        if (!team.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NOT_AUTH, "无访问权限");
        }
        LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTeam::getTeamId, teamId);
        boolean result = userTeamService.remove(wrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除队伍失败");
        }
        return this.removeById(teamId);
    }

    /**
     * 获取队伍人数
     *
     * @param teamId 团队id
     * @return long
     */
    private long getTeamUserByTeamId(long teamId) {
        LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTeam::getTeamId, teamId);
        return userTeamService.count(wrapper);
    }

    /**
     * 通过id获取团队
     *
     * @param teamId 团队id
     * @return {@code Team}
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 根据队伍id查询出队伍
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }
}
