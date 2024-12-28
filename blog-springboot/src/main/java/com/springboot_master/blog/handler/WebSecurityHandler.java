package com.springboot_master.blog.handler;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.springboot_master.blog.annotation.AccessFilter;
import com.springboot_master.blog.annotation.AccessLimit;
import com.springboot_master.blog.service.RedisService;
import com.springboot_master.blog.util.IpUtils;
import com.springboot_master.blog.vo.Result;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.springboot_master.blog.constant.CommonConst.APPLICATION_JSON;

/**
 * @author hnz
 * @date 2022/3/23 11:21
 * @description
 */
@Log4j2
public class WebSecurityHandler implements HandlerInterceptor {
    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        // 如果请求输入方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            // 获取方法中的注解,看是否有该注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit != null) {
                long seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                // 关于key的生成规则可以自己定义 本项目需求是对每个方法都加上限流功能，如果你只是针对ip地址限流，那么key只需要只用ip就好
                String key = IpUtils.getIpAddress(httpServletRequest) + hm.getMethod().getName();
                // 从redis中获取用户访问的次数
                try {
                    // 此操作代表获取该key对应的值自增1后的结果
                    long q = redisService.incrExpire(key, seconds);
                    if (q > maxCount) {
                        render(httpServletResponse, Result.fail("请求过于频繁，请稍候再试"));
                        log.warn(key + "请求次数超过每" + seconds + "秒" + maxCount + "次");
                        return false;
                    }
                    return true;
                } catch (RedisConnectionFailureException e) {
                    log.warn("redis错误: " + e.getMessage());
                    return false;
                }
            }
            AccessFilter accessFilter = hm.getMethodAnnotation(AccessFilter.class);
            if (accessFilter != null) {
                int second = accessFilter.second();
                int count = accessFilter.count();
                int filterTime = accessFilter.filterTime();
                String key = IpUtils.getIpAddress(httpServletRequest) + hm.getMethod().getName();
                String smallCountkey = "small_count:" + key;
                String smallBlackKey = "small_black:" + key;
                String bigCountKey = "big_count:" + key;
                String bigBlackKey = "big_black:" + key;
                String lockKey = "lock:" + key;
                String bigLockKey = "big_lock:" + key;
                // 从redis中获取用户访问的次数
                // 刚开始就已经被拉黑 直接拦截
                if (redisService.hasKey(smallBlackKey) || redisService.hasKey(bigBlackKey)) {
                    render(httpServletResponse, Result.fail("请求过于频繁，请稍候再试"));
                    return false;
                }
                try {
                    String value = UUID.randomUUID().toString(true) + "-" + Thread.currentThread().getId();
                    if (redisService.tryLock(lockKey, value, 3)) {
                        // 记录3s内多少次
                        long smallCount = redisService.incrExpire(smallCountkey, second);
                        if (smallCount > count) {
                            render(httpServletResponse, Result.fail("请求过于频繁，请稍候再试"));
                            log.warn(key + "请求次数为每" + second + "秒" + smallCount + "次," + "进入小黑屋");
                            redisService.del(smallCountkey);
                            redisService.set(smallBlackKey, "true", filterTime);
                            // 记录900s内进入了多少次小黑屋 若进入了2次 则拉黑3600s
                            Long bigCount = redisService.incrExpire(bigCountKey, 900);
                            if (bigCount > 1) {
                                log.warn(key + "请求进入小黑屋次数为每" + "900秒" + bigCount + "次," + "进入大黑屋");
                                redisService.del(bigCountKey);
                                redisService.del(smallBlackKey);
                                redisService.set(bigBlackKey, "true", 3600);
                                return false;
                            }
                        }
                        redisService.unlock(lockKey, value);
                        return true;
                    } else {
                        // 未获取到锁，可能是因为其他线程正在执行关键代码段，这里可以选择重试或者直接返回失败
                        render(httpServletResponse, Result.fail("系统繁忙，请稍候再试"));
                        return false;
                    }
                } catch (RedisConnectionFailureException e) {
                    log.warn("redis错误: " + e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, Result<?> result) throws Exception {
        response.setContentType(APPLICATION_JSON);
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(result);
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }

}
