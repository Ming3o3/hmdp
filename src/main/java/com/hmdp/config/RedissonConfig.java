package com.hmdp.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
//        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
//        // 创建RedissonClient对象
//        return Redisson.create(config);

        // 确认这里使用的是配置文件中的地址，而不是硬编码的 127.0.0.1
        config.useSingleServer()
                .setAddress("redis://120.79.222.247:6379")  // 应该使用配置的地址
                .setPassword("English629");  // 添加密码
        return Redisson.create(config);
    }
}
