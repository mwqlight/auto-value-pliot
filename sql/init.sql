-- 价格比价系统数据库初始化脚本

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS price_compare CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE price_compare;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 平台配置表
CREATE TABLE IF NOT EXISTS platform_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    platform_code VARCHAR(50) NOT NULL UNIQUE COMMENT '平台代码',
    platform_name VARCHAR(100) NOT NULL COMMENT '平台名称',
    api_base_url VARCHAR(255) COMMENT 'API基础URL',
    search_api_path VARCHAR(255) COMMENT '搜索API路径',
    detail_api_path VARCHAR(255) COMMENT '详情API路径',
    timeout INT DEFAULT 5000 COMMENT '超时时间(ms)',
    max_retries INT DEFAULT 3 COMMENT '最大重试次数',
    rate_limit INT DEFAULT 1000 COMMENT '限流(次/秒)',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_platform_code (platform_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台配置表';

-- 商品价格表
CREATE TABLE IF NOT EXISTS product_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '价格ID',
    product_id VARCHAR(100) COMMENT '商品ID',
    platform_code VARCHAR(50) NOT NULL COMMENT '平台代码',
    platform_product_id VARCHAR(100) NOT NULL COMMENT '平台商品ID',
    price DECIMAL(10,2) NOT NULL COMMENT '当前价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    discount VARCHAR(20) COMMENT '折扣信息',
    sales INT DEFAULT 0 COMMENT '销量',
    rating DECIMAL(3,2) COMMENT '评分',
    product_url VARCHAR(500) NOT NULL COMMENT '商品链接',
    shop_name VARCHAR(200) COMMENT '店铺名称',
    shop_rating DECIMAL(3,2) COMMENT '店铺评分',
    delivery VARCHAR(100) COMMENT '配送信息',
    is_lowest TINYINT DEFAULT 0 COMMENT '是否最低价：0-否，1-是',
    crawl_time DATETIME NOT NULL COMMENT '爬取时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product_id (product_id),
    INDEX idx_platform_product (platform_code, platform_product_id),
    INDEX idx_crawl_time (crawl_time),
    INDEX idx_price (price),
    UNIQUE KEY uk_platform_product (platform_code, platform_product_id, crawl_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品价格表';

-- 搜索历史表
CREATE TABLE IF NOT EXISTS search_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '历史ID',
    user_id BIGINT COMMENT '用户ID',
    keyword VARCHAR(200) NOT NULL COMMENT '搜索关键词',
    search_count INT DEFAULT 1 COMMENT '搜索次数',
    last_search_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后搜索时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_keyword (user_id, keyword),
    INDEX idx_keyword (keyword),
    INDEX idx_last_search (last_search_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索历史表';

-- 价格趋势表
CREATE TABLE IF NOT EXISTS price_trend (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '趋势ID',
    product_id VARCHAR(100) NOT NULL COMMENT '商品ID',
    platform_code VARCHAR(50) NOT NULL COMMENT '平台代码',
    platform_product_id VARCHAR(100) NOT NULL COMMENT '平台商品ID',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    record_date DATE NOT NULL COMMENT '记录日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_date (product_id, record_date),
    INDEX idx_platform_product_date (platform_code, platform_product_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='价格趋势表';

-- 插入默认平台配置数据
INSERT IGNORE INTO platform_config (platform_code, platform_name, api_base_url, search_api_path, detail_api_path, timeout, max_retries, rate_limit, enabled) VALUES
('taobao', '淘宝', 'https://api.taobao.com', '/search', '/detail', 5000, 3, 1000, 1),
('jd', '京东', 'https://api.jd.com', '/search', '/detail', 5000, 3, 1000, 1),
('pdd', '拼多多', 'https://api.pdd.com', '/search', '/detail', 5000, 3, 1000, 1),
('suning', '苏宁易购', 'https://api.suning.com', '/search', '/detail', 5000, 3, 1000, 1),
('vip', '唯品会', 'https://api.vip.com', '/search', '/detail', 5000, 3, 1000, 1);

-- 插入测试用户数据
INSERT IGNORE INTO user (username, email, password, phone, avatar, status) VALUES
('admin', 'admin@pricecompare.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVwjym', '13800138000', '/avatar/admin.jpg', 1),
('testuser', 'test@pricecompare.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVwjym', '13900139000', '/avatar/test.jpg', 1);

-- 插入示例商品价格数据
INSERT IGNORE INTO product_price (product_id, platform_code, platform_product_id, price, original_price, discount, sales, rating, product_url, shop_name, shop_rating, delivery, is_lowest, crawl_time) VALUES
('iphone15_001', 'jd', 'jd_iphone15_001', 5999.00, 6999.00, '8.6折', 1500, 4.8, 'https://jd.com/product/iphone15_001', '京东自营', 4.9, '京东物流 次日达', 1, NOW()),
('iphone15_001', 'taobao', 'taobao_iphone15_001', 5899.00, 6999.00, '8.4折', 2000, 4.7, 'https://taobao.com/product/iphone15_001', '天猫旗舰店', 4.8, '快递 免运费', 0, NOW()),
('iphone15_001', 'pdd', 'pdd_iphone15_001', 5799.00, 6999.00, '8.3折', 5000, 4.6, 'https://pdd.com/product/iphone15_001', '拼多多官方店', 4.7, '快递 包邮', 0, NOW());

-- 创建存储过程：清理过期价格数据
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS clean_old_prices()
BEGIN
    DELETE FROM product_price WHERE crawl_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
    DELETE FROM price_trend WHERE record_date < DATE_SUB(NOW(), INTERVAL 180 DAY);
END //
DELIMITER ;

-- 创建事件：每天自动清理过期数据
CREATE EVENT IF NOT EXISTS daily_cleanup
ON SCHEDULE EVERY 1 DAY STARTS CURRENT_TIMESTAMP
DO CALL clean_old_prices();