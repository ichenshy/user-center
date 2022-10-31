package com.chen.user.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.user.entity.User;
import com.chen.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 缓存工作之前
 *
 * @author Galaxy
 * @version v1.0
 * @date 2022/8/8
 */
@Component
@Slf4j
public class PreCacheJob {
    /** 用户服务 */
    @Resource
    private UserService userService;
    /** 复述,模板 */
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /** 主要用户列表 */
    private List<Long> mainUserList = Arrays.asList(1L);

    /**
     * 缓存推荐用户
     * 每天执行
     */
    @Scheduled(cron = "0 58 23 * * *")
    public void doCacheRecommendUser() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        Page<User> page = new Page<>(1, 20);
        Page<User> userPage = userService.page(page, wrapper);
        for (Long userId : mainUserList) {
            String redisKey = String.format("chen:user:recommend:%s", userId);
            ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
            try {
                opsForValue.set(redisKey, userPage);
            } catch (Exception e) {
                log.error("redis set key error ", e);
            }
        }
    }
}
