package com.chen.user.once;

import com.chen.user.entity.User;
import com.chen.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;

import static com.sun.javafx.font.FontResource.SALT;

/**
 * 插入用户
 *
 * @author Galaxy
 * @version v1.0
 * @date 2022/8/8
 */
@Component
public class InsertUsers {
    /** 用户服务 */
    @Resource
    private UserService userService;

    /**
     * 插入用户
     * 批量插入用户
     */
    //@Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM=100000;
        ArrayList<User> list = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUserAccount("假帐号");
            user.setUsername("假帐号");
            user.setAvatarUrl("https://cdn.jsdelivr.net/gh/chenshy-lq/image/img/202207302003692.png");
            user.setGender(0);
            user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + "123456").getBytes()));
            user.setTags("[\"java\",\"男\"]");
            user.setPhone("11111");
            user.setEmail("111@qq.com");
            user.setProfile("LianaiL");
            user.setUserStatus(0);
            user.setRole(0);
            user.setPlanetCode("111");
            list.add(user);
        }
        userService.saveBatch(list,1000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
        //QueryWrapper<User> wrapper = new QueryWrapper<>();
        //wrapper.gt("id", 10);
        //boolean remove = userService.remove(wrapper);
        //System.out.println(remove);
    }
}
