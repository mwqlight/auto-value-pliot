package com.pricecompare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 价格比较应用启动类
 *
 * @author price-compare
 * @since 2024
 */
@SpringBootApplication(exclude = {
    RedisRepositoriesAutoConfiguration.class
})
@EnableAsync
@EnableScheduling
public class PriceCompareApplication {

    public static void main(String[] args) {
        SpringApplication.run(PriceCompareApplication.class, args);
    }

}