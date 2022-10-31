package com.chen.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.user.common.ErrorCode;
import com.chen.user.entity.User;
import com.chen.user.exception.BusinessException;
import com.chen.user.mapper.UserMapper;
import com.chen.user.service.UserService;
import com.chen.user.utils.AlgorithmUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.chen.user.constant.UserConstant.ADMIN_ROLE;
import static com.chen.user.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 针对表【user(用户)】的数据库操作Service实现
 *
 * @author Galaxy
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2022-05-19 15:33:53
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 盐值混淆密码
     */
    private static final String SALT = "galaxy";

    /**
     * 用户注册
     *
     * @param userAccount   账号
     * @param userPassword  密码
     * @param checkPassword 确认密码
     * @param planetCode    地球上代码
     * @return userId
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 判空
        // isAnyBlank：检查是否有任何字符序列为空或空或仅空格。
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入的字符为空");
        }
        // 账号不小于4位,密码不小于6位
        if (userAccount.length() < 4 && userPassword.length() > 6 && checkPassword.length() > 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不小于4位,密码不小于6位");

        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");

        }
//        //账户不包含特殊字符
//        String validPattern = "\\pP|\\pS|\\s+";
//        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
//        if (!matcher.find()) {
//            return -1;
//        }
        // 密码和检验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码与确认密码不一致");

        }
        // 账户不能重复
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_account", userAccount);
        Long count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户重复");

        }
        // 星球id不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planet_code", planetCode);
        count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号重复 ");

        }
        // 密码加密
        String keyPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(keyPassword);
        user.setPlanetCode(planetCode);
        // 插入数据库
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @param request      HttpServletRequest
     * @return userId
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 判空
        // isAnyBlank：检查是否有任何字符序列为空或空或仅空格。
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        // 账号不小于4位,密码不小于6位
        if (userAccount.length() < 4 && userPassword.length() > 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        // 密码加密
        String keyPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 账户不能重复
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_account", userAccount);
        wrapper.eq("user_password", keyPassword);
        User user = baseMapper.selectOne(wrapper);
        // 账户密码不存在
        if (user == null) {
            log.info("user login failed 账户密码不匹配");
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        User safetyUser = getSafetyUser(user);
        // 记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 得到安全用户
     * 用户脱敏
     *
     * @param user 脱敏前
     * @return newUser 脱敏后
     */
    @Override
    public User getSafetyUser(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setUserAccount(user.getUserAccount());
        newUser.setUsername(user.getUsername());
        newUser.setAvatarUrl(user.getAvatarUrl());
        newUser.setGender(user.getGender());
        newUser.setPhone(user.getPhone());
        newUser.setEmail(user.getEmail());
        newUser.setUserStatus(user.getUserStatus());
        newUser.setCreateTime(user.getCreateTime());
        newUser.setRole(user.getRole());
        newUser.setPlanetCode(user.getPlanetCode());
        newUser.setTags(user.getTags());
        return newUser;
    }

    /**
     * 用户注销
     *
     * @param request 请求
     * @return int
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 搜索用户标签
     * 根据标签查询用户（内存过滤）
     *
     * @param tagNameList 标签名
     * @param pageSize    页面大小
     * @param pageNum     页面num
     * @return 查询后的用户列表
     */
    @Override
    public List<User> searchUsersByTags(long pageSize, long pageNum, List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.内存查询
        List<User> userList = baseMapper.selectList(null);
        // List<User> userList = baseMapper.selectPage(null);
        Gson gson = new Gson();
        return userList.stream().filter(user -> {
            // 拿出用户的tags
            String tagsStr = user.getTags();
            // 将用户的string类型的tags转换成json集合(Set)
            Set<User> tempTags = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            // if(CollectionUtils.isEmpty(tempTags)){
            //    return false;
            //}
            // 通过ofNullable判断tempTags是否为空，如果为空赋一个默认值 new HashSet<>()
            tempTags = Optional.ofNullable(tempTags).orElse(new HashSet<>());
            // 循环查询的标签
            for (String tag : tagNameList) {
                // 如果 转换后的tempTags不包含用户所需要查询的tag
                if (!tempTags.contains(tag)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 搜索用户通过标签sql
     * 根据标签查询用户
     *
     * @param tagNameList 标签名  sql版本
     * @return 查询后的用户列表
     */
    @Deprecated  // 表示过期的方法
    private List<User> searchUsersByTagsBySql(List<String> tagNameList) {
        // sql查询
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        for (String tagName : tagNameList) {
            wrapper.like("tags", tagName);
        }
        List<User> userList = baseMapper.selectList(wrapper);
        // return userList.stream().map(user -> getSafetyUser(user)).collect(Collectors.toList());
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 获取登录用户
     * 获取当前登录用户
     *
     * @param request 请求
     * @return user
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userLogin = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userLogin == null) {
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        return (User) userLogin;
    }

    /**
     * 是管理
     *
     * @param request 请求
     * @return true:是管理员 false:不是管理员
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getRole() == ADMIN_ROLE;
    }

    /**
     * 是管理
     *
     * @param loginUser 当前用户
     * @return true:是管理员 false:不是管理员
     */
    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getRole() == ADMIN_ROLE;
    }

    /**
     * 匹配用户
     *
     * @param num       num
     * @param loginUser 登录用户
     * @return {@code List<User>}
     */
    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || loginUser.getId().equals(user.getId())) {
                // if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream().sorted((a, b) -> (int) (a.getValue() - b.getValue())).limit(num).collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper).stream()
                .map(this::getSafetyUser).collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }

    /**
     * 更新用户
     * 修改用户信息
     *
     * @param user      用户信息
     * @param loginUser 当前用户信息
     * @return result
     */
    @Override
    public int updateUser(User user, User loginUser) {
        Long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!isAdmin(loginUser) && !userId.equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        User oldUser = baseMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // TODO 加校验，没有修改
        return baseMapper.updateById(user);
    }
}
