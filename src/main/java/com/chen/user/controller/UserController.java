package com.chen.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.user.common.BaseResponse;
import com.chen.user.common.ErrorCode;
import com.chen.user.common.ResultUtils;
import com.chen.user.entity.User;
import com.chen.user.entity.request.UserLoginRequest;
import com.chen.user.entity.request.UserRegisterRequest;
import com.chen.user.exception.BusinessException;
import com.chen.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.chen.user.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author Galaxy
 * @version v1.0
 * @date 2022/5/24
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return {@link BaseResponse}<{@link Long}>
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
        // return new BaseResponse<>(200, result, "ok");
        // return userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
    }

    /**
     * 做登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request          请求
     * @return {@link BaseResponse}<{@link User}>
     */
    @PostMapping("/login")
    public BaseResponse<User> doLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
        // return new BaseResponse<>(200,user,"ok");
        // return userService.userLogin(userAccount, userPassword, request);

    }

    /**
     * 获取当前用户(脱敏后)
     *
     * @param request 请求
     * @return User safetyUser
     */
    @GetMapping("/current")
    public BaseResponse<User> current(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long id = currentUser.getId();
        // TODO 校验用户是否合法
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 退出登录
     *
     * @param request 请求
     * @return logout
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int logout = userService.userLogout(request);
        return ResultUtils.success(logout);
    }

    /**
     * 获取所有使用户
     *
     * @param username username
     * @param request  request
     * @return List<User>
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUserList(String username, HttpServletRequest request) {
        // 鉴权 仅管理员查询
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            wrapper.like("username", username);
        }
        List<User> list = userService.list(wrapper);
        // 脱敏 userService.getSafetyUser(user)
        List<User> collect = list.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    /**
     * 根据id删除用户
     *
     * @param id      userId
     * @param request 请求
     * @return result
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        // 鉴权 仅管理员查询
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 搜索标签
     *
     * @param tagNameList tags
     * @return userList
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(long pageSize, long pageNum, @RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(pageSize, pageNum, tagNameList);
        return ResultUtils.success(userList);
    }

    /**
     * 主页推荐
     *
     * @return list
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("chen:user:recommend:%s", loginUser.getId());
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        // 有缓存 取缓存
        Page<User> userPage = (Page<User>) opsForValue.get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }
        // 无缓存查数据库，写入缓存
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        Page<User> page = new Page<>(pageNum, pageSize);
        userPage = userService.page(page, wrapper);
        try {
            opsForValue.set(redisKey, userPage);
        } catch (Exception e) {
            log.error("redis set key error ", e);
        }
        return ResultUtils.success(userPage);
    }

    /**
     * 修改用户信息
     *
     * @param user    用户信息
     * @param request 请求
     * @return result
     */
    @PostMapping("update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 判断请求参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取最匹配的用户
     *
     * @param num     数量
     * @param request 请求
     * @return {@link BaseResponse}<{@link List}<{@link User}>>
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request) {
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num, user));
    }
}
