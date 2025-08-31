package com.hmdp.utils;

import cn.hutool.core.lang.UUID;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock{

    private String name;
    private StringRedisTemplate stringRedisTemplate;

    public SimpleRedisLock(String name,StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private static final String KEY_PREFIX = "lock:";
    private static final String ID_PREFIX = UUID.randomUUID().toString(true)+"-";
    private static final DefaultRedisScript<Long> UNLOCK_SCRIOT;
    static {
        UNLOCK_SCRIOT = new DefaultRedisScript<>();
        UNLOCK_SCRIOT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIOT.setResultType(Long.class);
    }

    @Override
    public boolean tryLock(long timeoutSec) {
        //获取线程的标识
        String threadId=ID_PREFIX + Thread.currentThread().getId();
        //获取锁
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(KEY_PREFIX + name, threadId, timeoutSec, TimeUnit.SECONDS);
        return success.TRUE.equals(success);
    }


    @Override
    public void unlock() {
            // 调用lua脚本
        stringRedisTemplate.execute(
                UNLOCK_SCRIOT,
                Collections.singletonList(KEY_PREFIX + name),
                Thread.currentThread().getId()
                );
        }


//    @Override
//    public void unlock() {
//        //获取线程标识
//        String threadId=ID_PREFIX + Thread.currentThread().getId();
//        //判断标示是否一致
//        String id = stringRedisTemplate.opsForValue().get(KEY_PREFIX + name);
//        //获取锁中的标示
//        if (threadId.equals(id)) {
//            //释放锁
//            stringRedisTemplate.delete(KEY_PREFIX + name);
//        }

//    }
}
