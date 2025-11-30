package com.pricecompare;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 主应用测试类
 * 
 * @author AutoValuePilot
 */
@SpringBootTest
class PriceCompareApplicationTests {

    @Test
    void contextLoads() {
        // 测试Spring上下文加载
    }

    @Test
    void applicationStarts() {
        // 测试应用启动
        PriceCompareApplication.main(new String[]{});
    }
}