-- 价格比价系统数据库初始化脚本
-- 创建时间: 2024-01-01
-- 作者: AutoValuePilot

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码（加密存储）',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
    `status` int(1) DEFAULT 1 COMMENT '用户状态：0-禁用，1-启用',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` int(1) DEFAULT 0 COMMENT '逻辑删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建平台配置表
CREATE TABLE IF NOT EXISTS `platform_config` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `platform_name` varchar(50) NOT NULL COMMENT '平台名称',
    `platform_code` varchar(20) NOT NULL COMMENT '平台代码：taobao, jd, pdd, suning, vip',
    `api_base_url` varchar(255) DEFAULT NULL COMMENT '平台API基础URL',
    `search_api_path` varchar(100) DEFAULT NULL COMMENT '搜索API路径',
    `detail_api_path` varchar(100) DEFAULT NULL COMMENT '商品详情API路径',
    `timeout` int(11) DEFAULT 5000 COMMENT '请求超时时间（毫秒）',
    `max_retries` int(11) DEFAULT 3 COMMENT '最大重试次数',
    `enabled` int(1) DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    `rate_limit` int(11) DEFAULT 1000 COMMENT '请求频率限制（毫秒）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_platform_code` (`platform_code`),
    KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台配置表';

-- 创建平台授权表
CREATE TABLE IF NOT EXISTS `platform_auth` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '授权ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `platform_type` varchar(20) NOT NULL COMMENT '平台类型：taobao, jd, pdd, suning, vip',
    `platform_user_id` varchar(100) DEFAULT NULL COMMENT '平台用户ID',
    `access_token` varchar(500) DEFAULT NULL COMMENT '访问令牌',
    `refresh_token` varchar(500) DEFAULT NULL COMMENT '刷新令牌',
    `token_expire_time` datetime DEFAULT NULL COMMENT '令牌过期时间',
    `auth_status` int(1) DEFAULT 0 COMMENT '授权状态：0-未授权，1-已授权，2-已过期',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_platform` (`user_id`, `platform_type`),
    KEY `idx_auth_status` (`auth_status`),
    KEY `idx_expire_time` (`token_expire_time`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台授权信息表';

-- 创建商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `title` varchar(255) NOT NULL COMMENT '商品标题',
    `description` text COMMENT '商品描述',
    `category` varchar(100) DEFAULT NULL COMMENT '商品分类',
    `brand` varchar(100) DEFAULT NULL COMMENT '品牌',
    `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
    `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
    `discount` varchar(50) DEFAULT NULL COMMENT '折扣信息',
    `image_url` varchar(500) DEFAULT NULL COMMENT '商品图片URL',
    `product_url` varchar(500) DEFAULT NULL COMMENT '商品链接',
    `platform` varchar(20) DEFAULT NULL COMMENT '平台：taobao, jd, pdd, suning, vip',
    `platform_product_id` varchar(100) DEFAULT NULL COMMENT '平台商品ID',
    `sales` int(11) DEFAULT 0 COMMENT '销量',
    `rating` decimal(3,2) DEFAULT 0.00 COMMENT '评分',
    `shop_name` varchar(200) DEFAULT NULL COMMENT '店铺名称',
    `shop_rating` decimal(3,2) DEFAULT 0.00 COMMENT '店铺评分',
    `delivery` varchar(100) DEFAULT NULL COMMENT '配送信息',
    `crawl_time` datetime DEFAULT NULL COMMENT '抓取时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` int(1) DEFAULT 0 COMMENT '逻辑删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_platform_product` (`platform`, `platform_product_id`),
    KEY `idx_title` (`title`),
    KEY `idx_category` (`category`),
    KEY `idx_platform` (`platform`),
    KEY `idx_price` (`price`),
    KEY `idx_sales` (`sales`),
    KEY `idx_crawl_time` (`crawl_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 创建商品价格表
CREATE TABLE IF NOT EXISTS `product_price` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '价格ID',
    `product_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
    `platform_code` varchar(20) NOT NULL COMMENT '平台代码：taobao, jd, pdd, suning, vip',
    `platform_product_id` varchar(100) NOT NULL COMMENT '平台商品ID',
    `price` decimal(10,2) NOT NULL COMMENT '商品价格',
    `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
    `discount` varchar(50) DEFAULT NULL COMMENT '折扣信息',
    `sales` int(11) DEFAULT 0 COMMENT '销量',
    `rating` decimal(3,2) DEFAULT 0.00 COMMENT '评分',
    `product_url` varchar(500) DEFAULT NULL COMMENT '商品链接',
    `shop_name` varchar(200) DEFAULT NULL COMMENT '店铺名称',
    `shop_rating` decimal(3,2) DEFAULT 0.00 COMMENT '店铺评分',
    `delivery` varchar(100) DEFAULT NULL COMMENT '配送信息',
    `is_lowest` tinyint(1) DEFAULT 0 COMMENT '是否是最低价',
    `crawl_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '抓取时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_platform_product_price` (`platform_code`, `platform_product_id`, `crawl_time`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_platform_code` (`platform_code`),
    KEY `idx_price` (`price`),
    KEY `idx_crawl_time` (`crawl_time`),
    KEY `idx_is_lowest` (`is_lowest`),
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品价格表';

-- 创建比价任务表
CREATE TABLE IF NOT EXISTS `compare_task` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `task_id` varchar(50) DEFAULT NULL COMMENT '任务ID（UUID）',
    `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
    `source_product_id` bigint(20) DEFAULT NULL COMMENT '源商品ID',
    `source_platform` varchar(20) DEFAULT NULL COMMENT '源平台',
    `product_name` varchar(255) NOT NULL COMMENT '商品名称',
    `status` int(1) DEFAULT 0 COMMENT '任务状态：0-待处理，1-处理中，2-已完成，3-失败',
    `progress` int(3) DEFAULT 0 COMMENT '任务进度（0-100）',
    `result_count` int(11) DEFAULT 0 COMMENT '比价结果数量',
    `end_time` datetime DEFAULT NULL COMMENT '任务结束时间',
    `error_message` text COMMENT '错误信息',
    `compare_result` text COMMENT '比价结果（JSON格式）',
    `similar_products` text COMMENT '相似商品推荐（JSON格式）',
    `chart_data` text COMMENT '图表数据（JSON格式）',
    `start_time` datetime DEFAULT NULL COMMENT '任务开始时间',
    `finish_time` datetime DEFAULT NULL COMMENT '任务完成时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_product_name` (`product_name`),
    KEY `idx_create_time` (`create_time`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`source_product_id`) REFERENCES `product`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比价任务表';

-- 插入平台配置数据
INSERT IGNORE INTO `platform_config` (`platform_name`, `platform_code`, `api_base_url`, `search_api_path`, `detail_api_path`, `timeout`, `max_retries`, `enabled`, `rate_limit`) VALUES
('淘宝', 'taobao', 'https://api.taobao.com', '/router/rest', '/router/rest', 5000, 3, 1, 1000),
('京东', 'jd', 'https://api.jd.com', '/router/api', '/router/api', 5000, 3, 1, 1000),
('拼多多', 'pdd', 'https://api.pinduoduo.com', '/api/router', '/api/router', 5000, 3, 1, 1000),
('苏宁', 'suning', 'https://api.suning.com', '/router/api', '/router/api', 5000, 3, 1, 1000),
('唯品会', 'vip', 'https://api.vip.com', '/router/api', '/router/api', 5000, 3, 1, 1000);

-- 插入默认用户数据
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `phone`, `nickname`, `avatar`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV6UiC', 'admin@example.com', '13800138000', '管理员', 'https://example.com/avatar.jpg', 1);

-- 创建索引优化查询性能
CREATE INDEX idx_product_price_composite ON `product_price` (`platform_code`, `price`, `crawl_time`);
CREATE INDEX idx_compare_task_composite ON `compare_task` (`status`, `create_time`);
CREATE INDEX idx_product_search ON `product` (`title`, `platform`, `price`);