package com.chen.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.user.common.BaseResponse;
import com.chen.user.common.ErrorCode;
import com.chen.user.common.ResultUtils;
import com.chen.user.entity.Team;
import com.chen.user.entity.User;
import com.chen.user.entity.UserTeam;
import com.chen.user.entity.dto.TeamQuery;
import com.chen.user.entity.request.TeamAddRequest;
import com.chen.user.entity.request.TeamJoinRequest;
import com.chen.user.entity.request.TeamQuitRequest;
import com.chen.user.entity.request.TeamUpdateRequest;
import com.chen.user.entity.vo.TeamUserVo;
import com.chen.user.exception.BusinessException;
import com.chen.user.service.TeamService;
import com.chen.user.service.UserService;
import com.chen.user.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 团队控制器
 * 任务接口
 *
 * @author chenshy
 * @version v1.0
 * @date 2022/5/24
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {
    /**
     * 团队服务
     */
    @Resource
    private TeamService teamService;
    /**
     * 用户服务
     */
    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;

    /**
     * 加入团队
     * 新增任务
     *
     * @param teamAddRequest teamAddRequest
     * @param request        请求
     * @return teamId
     */
    @PostMapping("/addTeam")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }


    /**
     * 更新团队
     *
     * @param teamUpdateRequest 团队更新请求
     * @param request           请求
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success();
    }

    /**
     * 通过id获取团队
     * 根据id查询
     *
     * @param id id
     * @return team
     */

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 团队名单
     * 查询
     *
     * @param teamQuery 查询封装类
     * @param request   请求
     * @return teamList
     */

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVo>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVo> teamList = teamService.listTeams(teamQuery, isAdmin);
        // 查询当前用户是否已加入队伍
        final List<Long> teamIdList = teamList.stream().map(TeamUserVo::getId).collect(Collectors.toList());
        LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
        try {
            User loginUser = userService.getLoginUser(request);
            wrapper.eq(UserTeam::getUserId, loginUser.getId());
            wrapper.in(UserTeam::getTeamId, teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(wrapper);
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e) {
        }
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserTeam::getTeamId, teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        Map<Long, List<UserTeam>> collect = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getUserId));
        teamList.forEach(team -> {
            team.setHasJoinNum(collect.getOrDefault(team.getId(), new ArrayList<>()).size());
        });

        return ResultUtils.success(teamList);
    }

    /**
     * 团队列表页面
     * 查询带分页
     *
     * @param teamQuery 查询封装类
     * @return teamPageList
     */

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        // Params:  current – 当前页   size – 每页显示条数
        Page<Team> pageParam = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>(team);
        Page<Team> teamPageList = teamService.page(pageParam, wrapper);
        return ResultUtils.success(teamPageList);
    }

    /**
     * 加入团队
     *
     * @param teamJoinRequest 团队加入请求
     * @param request         请求
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 退出团队
     *
     * @param teamQuitRequest 团队辞职请求
     * @param request         请求
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 删除团队
     * 根据id删除任务
     *
     * @param id id
     * @return null
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success();
    }

    /**
     * 我创建团队名单
     *
     * @param teamQuery 团队查询
     * @param request   请求
     * @return {@link BaseResponse}<{@link List}<{@link TeamUserVo}>>
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVo>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVo> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList);
    }

    /**
     * 获取我加入的队伍
     *
     * @param teamQuery 查询封装类
     * @param request   请求
     * @return teamList
     */

    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVo>> listJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTeam::getId, loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(wrapper);
        // 取出不重复的id
        // teamId  userId
        //   1,       2
        //   1,       3
        //   2,       3
        //   1=> 2,3
        //   2=> 3
        Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamUserVo> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList);
    }
}
