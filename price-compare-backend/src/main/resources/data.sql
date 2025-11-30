-- 价格比价系统示例数据初始化脚本
-- 创建时间: 2024-01-01
-- 作者: AutoValuePilot

-- 插入示例商品数据
INSERT IGNORE INTO `product` (`title`, `description`, `category`, `brand`, `price`, `original_price`, `discount`, `image_url`, `product_url`, `platform`, `platform_product_id`, `sales`, `rating`, `shop_name`, `shop_rating`, `delivery`, `crawl_time`) VALUES
('iPhone 15 Pro Max 256GB', '苹果最新旗舰手机，搭载A17 Pro芯片，钛金属机身', '手机数码', 'Apple', 8999.00, 9999.00, '9折', 'https://example.com/iphone15.jpg', 'https://item.taobao.com/iphone15', 'taobao', 'taobao_iphone15_001', 1500, 4.9, '天猫Apple旗舰店', 4.9, '快递 免运费', '2024-01-01 10:00:00'),
('iPhone 15 Pro Max 256GB', '苹果最新旗舰手机，搭载A17 Pro芯片，钛金属机身', '手机数码', 'Apple', 8899.00, 9999.00, '8.9折', 'https://example.com/iphone15_jd.jpg', 'https://item.jd.com/iphone15', 'jd', 'jd_iphone15_001', 2000, 4.8, '京东Apple自营店', 4.8, '京东物流 次日达', '2024-01-01 10:05:00'),
('iPhone 15 Pro Max 256GB', '苹果最新旗舰手机，搭载A17 Pro芯片，钛金属机身', '手机数码', 'Apple', 8599.00, 9999.00, '8.6折', 'https://example.com/iphone15_pdd.jpg', 'https://item.pdd.com/iphone15', 'pdd', 'pdd_iphone15_001', 5000, 4.7, '拼多多Apple官方店', 4.7, '快递 包邮', '2024-01-01 10:10:00'),
('MacBook Pro 14英寸 M3', '苹果专业笔记本电脑，搭载M3芯片，Liquid Retina XDR显示屏', '电脑办公', 'Apple', 12999.00, 14999.00, '8.7折', 'https://example.com/macbook14.jpg', 'https://item.taobao.com/macbook14', 'taobao', 'taobao_macbook14_001', 800, 4.9, '天猫Apple旗舰店', 4.9, '快递 免运费', '2024-01-01 11:00:00'),
('MacBook Pro 14英寸 M3', '苹果专业笔记本电脑，搭载M3芯片，Liquid Retina XDR显示屏', '电脑办公', 'Apple', 12799.00, 14999.00, '8.5折', 'https://example.com/macbook14_jd.jpg', 'https://item.jd.com/macbook14', 'jd', 'jd_macbook14_001', 1200, 4.8, '京东Apple自营店', 4.8, '京东物流 次日达', '2024-01-01 11:05:00'),
('AirPods Pro 2代', '苹果主动降噪无线耳机，空间音频，MagSafe充电', '数码配件', 'Apple', 1899.00, 2199.00, '8.6折', 'https://example.com/airpods2.jpg', 'https://item.taobao.com/airpods2', 'taobao', 'taobao_airpods2_001', 3000, 4.8, '天猫Apple旗舰店', 4.9, '快递 免运费', '2024-01-01 12:00:00'),
('AirPods Pro 2代', '苹果主动降噪无线耳机，空间音频，MagSafe充电', '数码配件', 'Apple', 1799.00, 2199.00, '8.2折', 'https://example.com/airpods2_jd.jpg', 'https://item.jd.com/airpods2', 'jd', 'jd_airpods2_001', 4000, 4.7, '京东Apple自营店', 4.8, '京东物流 次日达', '2024-01-01 12:05:00'),
('iPad Air 5代 64GB', '苹果平板电脑，M1芯片，Liquid Retina显示屏', '平板电脑', 'Apple', 4399.00, 4799.00, '9.2折', 'https://example.com/ipadair5.jpg', 'https://item.taobao.com/ipadair5', 'taobao', 'taobao_ipadair5_001', 2000, 4.7, '天猫Apple旗舰店', 4.9, '快递 免运费', '2024-01-01 13:00:00'),
('iPad Air 5代 64GB', '苹果平板电脑，M1芯片，Liquid Retina显示屏', '平板电脑', 'Apple', 4299.00, 4799.00, '9折', 'https://example.com/ipadair5_jd.jpg', 'https://item.jd.com/ipadair5', 'jd', 'jd_ipadair5_001', 2500, 4.6, '京东Apple自营店', 4.8, '京东物流 次日达', '2024-01-01 13:05:00'),
('Apple Watch Series 9', '苹果智能手表，S9芯片，全天候视网膜显示屏', '智能穿戴', 'Apple', 2999.00, 3199.00, '9.4折', 'https://example.com/watch9.jpg', 'https://item.taobao.com/watch9', 'taobao', 'taobao_watch9_001', 1500, 4.8, '天猫Apple旗舰店', 4.9, '快递 免运费', '2024-01-01 14:00:00');

-- 插入示例商品价格历史数据
INSERT IGNORE INTO `product_price` (`product_id`, `platform_code`, `platform_product_id`, `price`, `original_price`, `discount`, `sales`, `rating`, `product_url`, `shop_name`, `shop_rating`, `delivery`, `is_lowest`, `crawl_time`) VALUES
(1, 'taobao', 'taobao_iphone15_001', 8999.00, 9999.00, '9折', 1500, 4.9, 'https://item.taobao.com/iphone15', '天猫Apple旗舰店', 4.9, '快递 免运费', 0, '2024-01-01 10:00:00'),
(2, 'jd', 'jd_iphone15_001', 8899.00, 9999.00, '8.9折', 2000, 4.8, 'https://item.jd.com/iphone15', '京东Apple自营店', 4.8, '京东物流 次日达', 0, '2024-01-01 10:05:00'),
(3, 'pdd', 'pdd_iphone15_001', 8599.00, 9999.00, '8.6折', 5000, 4.7, 'https://item.pdd.com/iphone15', '拼多多Apple官方店', 4.7, '快递 包邮', 1, '2024-01-01 10:10:00'),
(4, 'taobao', 'taobao_macbook14_001', 12999.00, 14999.00, '8.7折', 800, 4.9, 'https://item.taobao.com/macbook14', '天猫Apple旗舰店', 4.9, '快递 免运费', 0, '2024-01-01 11:00:00'),
(5, 'jd', 'jd_macbook14_001', 12799.00, 14999.00, '8.5折', 1200, 4.8, 'https://item.jd.com/macbook14', '京东Apple自营店', 4.8, '京东物流 次日达', 1, '2024-01-01 11:05:00'),
(6, 'taobao', 'taobao_airpods2_001', 1899.00, 2199.00, '8.6折', 3000, 4.8, 'https://item.taobao.com/airpods2', '天猫Apple旗舰店', 4.9, '快递 免运费', 0, '2024-01-01 12:00:00'),
(7, 'jd', 'jd_airpods2_001', 1799.00, 2199.00, '8.2折', 4000, 4.7, 'https://item.jd.com/airpods2', '京东Apple自营店', 4.8, '京东物流 次日达', 1, '2024-01-01 12:05:00'),
(8, 'taobao', 'taobao_ipadair5_001', 4399.00, 4799.00, '9.2折', 2000, 4.7, 'https://item.taobao.com/ipadair5', '天猫Apple旗舰店', 4.9, '快递 免运费', 0, '2024-01-01 13:00:00'),
(9, 'jd', 'jd_ipadair5_001', 4299.00, 4799.00, '9折', 2500, 4.6, 'https://item.jd.com/ipadair5', '京东Apple自营店', 4.8, '京东物流 次日达', 1, '2024-01-01 13:05:00'),
(10, 'taobao', 'taobao_watch9_001', 2999.00, 3199.00, '9.4折', 1500, 4.8, 'https://item.taobao.com/watch9', '天猫Apple旗舰店', 4.9, '快递 免运费', 1, '2024-01-01 14:00:00');

-- 插入示例比价任务数据
INSERT IGNORE INTO `compare_task` (`task_id`, `user_id`, `product_name`, `status`, `progress`, `result_count`, `start_time`, `end_time`, `create_time`) VALUES
('task_001', 1, 'iPhone 15 Pro Max', 2, 100, 3, '2024-01-01 10:00:00', '2024-01-01 10:15:00', '2024-01-01 10:00:00'),
('task_002', 1, 'MacBook Pro 14英寸', 2, 100, 2, '2024-01-01 11:00:00', '2024-01-01 11:10:00', '2024-01-01 11:00:00'),
('task_003', 1, 'AirPods Pro 2代', 2, 100, 2, '2024-01-01 12:00:00', '2024-01-01 12:08:00', '2024-01-01 12:00:00'),
('task_004', 1, 'iPad Air 5代', 2, 100, 2, '2024-01-01 13:00:00', '2024-01-01 13:07:00', '2024-01-01 13:00:00'),
('task_005', 1, 'Apple Watch Series 9', 2, 100, 1, '2024-01-01 14:00:00', '2024-01-01 14:05:00', '2024-01-01 14:00:00'),
('task_006', 1, '三星 Galaxy S24', 1, 50, 0, '2024-01-01 15:00:00', NULL, '2024-01-01 15:00:00'),
('task_007', 1, '华为 Mate 60', 0, 0, 0, NULL, NULL, '2024-01-01 16:00:00');

-- 插入平台授权示例数据
INSERT IGNORE INTO `platform_auth` (`user_id`, `platform_type`, `auth_status`, `create_time`) VALUES
(1, 'taobao', 1, '2024-01-01 09:00:00'),
(1, 'jd', 1, '2024-01-01 09:00:00'),
(1, 'pdd', 1, '2024-01-01 09:00:00'),
(1, 'suning', 0, '2024-01-01 09:00:00'),
(1, 'vip', 0, '2024-01-01 09:00:00');

-- 更新商品价格表中的is_lowest字段
UPDATE `product_price` pp1
SET `is_lowest` = 1
WHERE pp1.`price` = (
    SELECT MIN(pp2.`price`)
    FROM `product_price` pp2
    WHERE pp2.`platform_product_id` = pp1.`platform_product_id`
    AND pp2.`crawl_time` = pp1.`crawl_time`
);

-- 创建价格趋势示例数据（用于图表展示）
INSERT IGNORE INTO `product_price` (`product_id`, `platform_code`, `platform_product_id`, `price`, `crawl_time`, `create_time`) VALUES
(1, 'taobao', 'taobao_iphone15_001', 9199.00, '2023-12-25 10:00:00', '2023-12-25 10:00:00'),
(1, 'taobao', 'taobao_iphone15_001', 9099.00, '2023-12-28 10:00:00', '2023-12-28 10:00:00'),
(1, 'taobao', 'taobao_iphone15_001', 8999.00, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
(2, 'jd', 'jd_iphone15_001', 8999.00, '2023-12-25 10:00:00', '2023-12-25 10:00:00'),
(2, 'jd', 'jd_iphone15_001', 8949.00, '2023-12-28 10:00:00', '2023-12-28 10:00:00'),
(2, 'jd', 'jd_iphone15_001', 8899.00, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
(3, 'pdd', 'pdd_iphone15_001', 8799.00, '2023-12-25 10:00:00', '2023-12-25 10:00:00'),
(3, 'pdd', 'pdd_iphone15_001', 8699.00, '2023-12-28 10:00:00', '2023-12-28 10:00:00'),
(3, 'pdd', 'pdd_iphone15_001', 8599.00, '2024-01-01 10:00:00', '2024-01-01 10:00:00');