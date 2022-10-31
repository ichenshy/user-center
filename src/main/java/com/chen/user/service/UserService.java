package com.chen.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.user.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author Galaxy
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2022-05-19 15:33:53
 * @date 2022/09/05
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * 注册接口
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球id
     * @return 新用户的id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     * 登录接口
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      请求
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 得到安全用户
     * 用户脱敏
     *
     * @param user 脱敏前
     * @return newUser 脱敏后
     */
    User getSafetyUser(User user);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return int
     */
    int userLogout(HttpServletRequest request);

    /**
     * 搜索用户标签
     * 根据标签查询用户
     *
     * @param tagNameList 标签名
     * @param pageSize    页面大小
     * @param pageNum     页面num
     * @return 查询后的用户列表
     */
    List<User> searchUsersByTags(long pageSize, long pageNum, List<String> tagNameList);

    /**
     * 获取登录用户
     * 获取当前登录用户
     *
     * @param request 请求
     * @return user
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 更新用户
     * 修改用户信息
     *
     * @param user      用户信息
     * @param loginUser 当前用户信息
     * @return result
     */
    int updateUser(User user, User loginUser);

    /**
     * 是管理
     * 判断是否是管理员
     *
     * @param request 请求
     * @return true:是管理员 false:不是管理员
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是管理
     * 判断是否是管理员
     *
     * @param loginUser 当前用户
     * @return true:是管理员 false:不是管理员
     */
    boolean isAdmin(User loginUser);

    /**
     * 匹配用户
     *
     * @param num
     * @param user 用户
     * @return {@link Object}
     */
    List<User> matchUsers(long num, User user);
}
