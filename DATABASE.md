# 数据库设计文档

## 📊 数据库架构

### 数据库信息
- **数据库名**: price_compare
- **字符集**: utf8mb4
- **排序规则**: utf8mb4_unicode_ci
- **引擎**: InnoDB

## 🗃️ 数据表结构

### 1. 用户表 (user)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 用户ID | 主键，自增 |
| username | VARCHAR(50) | 用户名 | 唯一，非空 |
| email | VARCHAR(100) | 邮箱 | 唯一 |
| password | VARCHAR(255) | 密码 | 非空，BCrypt加密 |
| phone | VARCHAR(20) | 手机号 |  |
| avatar | VARCHAR(255) | 头像 |  |
| status | TINYINT | 状态：0-禁用，1-启用 | 默认1 |
| last_login_time | DATETIME | 最后登录时间 |  |
| create_time | DATETIME | 创建时间 | 默认当前时间 |
| update_time | DATETIME | 更新时间 | 默认当前时间，自动更新 |

**索引**:
- idx_username (username)
- idx_email (email)

### 2. 平台配置表 (platform_config)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 配置ID | 主键，自增 |
| platform_code | VARCHAR(50) | 平台代码 | 唯一，非空 |
| platform_name | VARCHAR(100) | 平台名称 | 非空 |
| api_base_url | VARCHAR(255) | API基础URL |  |
| search_api_path | VARCHAR(255) | 搜索API路径 |  |
| detail_api_path | VARCHAR(255) | 详情API路径 |  |
| timeout | INT | 超时时间(ms) | 默认5000 |
| max_retries | INT | 最大重试次数 | 默认3 |
| rate_limit | INT | 限流(次/秒) | 默认1000 |
| enabled | TINYINT | 是否启用：0-禁用，1-启用 | 默认1 |
| create_time | DATETIME | 创建时间 | 默认当前时间 |
| update_time | DATETIME | 更新时间 | 默认当前时间，自动更新 |

**索引**:
- idx_platform_code (platform_code)

### 3. 商品价格表 (product_price)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 价格ID | 主键，自增 |
| product_id | VARCHAR(100) | 商品ID |  |
| platform_code | VARCHAR(50) | 平台代码 | 非空 |
| platform_product_id | VARCHAR(100) | 平台商品ID | 非空 |
| price | DECIMAL(10,2) | 当前价格 | 非空 |
| original_price | DECIMAL(10,2) | 原价 |  |
| discount | VARCHAR(20) | 折扣信息 |  |
| sales | INT | 销量 | 默认0 |
| rating | DECIMAL(3,2) | 评分 |  |
| product_url | VARCHAR(500) | 商品链接 | 非空 |
| shop_name | VARCHAR(200) | 店铺名称 |  |
| shop_rating | DECIMAL(3,2) | 店铺评分 |  |
| delivery | VARCHAR(100) | 配送信息 |  |
| is_lowest | TINYINT | 是否最低价：0-否，1-是 | 默认0 |
| crawl_time | DATETIME | 爬取时间 | 非空 |
| create_time | DATETIME | 创建时间 | 默认当前时间 |
| update_time | DATETIME | 更新时间 | 默认当前时间，自动更新 |

**索引**:
- idx_product_id (product_id)
- idx_platform_product (platform_code, platform_product_id)
- idx_crawl_time (crawl_time)
- idx_price (price)
- uk_platform_product (platform_code, platform_product_id, crawl_time) - 唯一索引

### 4. 搜索历史表 (search_history)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 历史ID | 主键，自增 |
| user_id | BIGINT | 用户ID | 外键 |
| keyword | VARCHAR(200) | 搜索关键词 | 非空 |
| search_count | INT | 搜索次数 | 默认1 |
| last_search_time | DATETIME | 最后搜索时间 | 默认当前时间 |
| create_time | DATETIME | 创建时间 | 默认当前时间 |
| update_time | DATETIME | 更新时间 | 默认当前时间，自动更新 |

**索引**:
- idx_user_keyword (user_id, keyword)
- idx_keyword (keyword)
- idx_last_search (last_search_time)

### 5. 价格趋势表 (price_trend)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 趋势ID | 主键，自增 |
| product_id | VARCHAR(100) | 商品ID | 非空 |
| platform_code | VARCHAR(50) | 平台代码 | 非空 |
| platform_product_id | VARCHAR(100) | 平台商品ID | 非空 |
| price | DECIMAL(10,2) | 价格 | 非空 |
| record_date | DATE | 记录日期 | 非空 |
| create_time | DATETIME | 创建时间 | 默认当前时间 |

**索引**:
- idx_product_date (product_id, record_date)
- idx_platform_product_date (platform_code, platform_product_id, record_date)

## 🔗 表关系图

```mermaid
erDiagram
    user ||--o{ search_history : "1:N"
    platform_config ||--o{ product_price : "1:N"
    platform_config ||--o{ price_trend : "1:N"
    product_price ||--o{ price_trend : "1:N"
    
    user {
        BIGINT id PK
        VARCHAR(50) username UK
        VARCHAR(100) email UK
        VARCHAR(255) password
        TINYINT status
        DATETIME create_time
        DATETIME update_time
    }
    
    platform_config {
        BIGINT id PK
        VARCHAR(50) platform_code UK
        VARCHAR(100) platform_name
        TINYINT enabled
        DATETIME create_time
        DATETIME update_time
    }
    
    product_price {
        BIGINT id PK
        VARCHAR(100) product_id
        VARCHAR(50) platform_code
        VARCHAR(100) platform_product_id
        DECIMAL(10,2) price
        DATETIME crawl_time
        DATETIME create_time
        DATETIME update_time
    }
    
    search_history {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR(200) keyword
        INT search_count
        DATETIME last_search_time
        DATETIME create_time
        DATETIME update_time
    }
    
    price_trend {
        BIGINT id PK
        VARCHAR(100) product_id
        VARCHAR(50) platform_code
        VARCHAR(100) platform_product_id
        DECIMAL(10,2) price
        DATE record_date
        DATETIME create_time
    }
```

## 📈 性能优化建议

### 1. 索引优化
```sql
-- 添加复合索引提升查询性能
ALTER TABLE product_price ADD INDEX idx_platform_price (platform_code, price);
ALTER TABLE product_price ADD INDEX idx_product_platform (product_id, platform_code);
ALTER TABLE price_trend ADD INDEX idx_date_platform (record_date, platform_code);
```

### 2. 分区策略
```sql
-- 对价格趋势表按月份分区
ALTER TABLE price_trend PARTITION BY RANGE (YEAR(record_date)*100 + MONTH(record_date)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404)
);
```

### 3. 存储过程
```sql
-- 清理过期数据的存储过程
DELIMITER //
CREATE PROCEDURE clean_old_data()
BEGIN
    -- 清理30天前的价格数据
    DELETE FROM product_price WHERE crawl_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
    
    -- 清理180天前的趋势数据
    DELETE FROM price_trend WHERE record_date < DATE_SUB(NOW(), INTERVAL 180 DAY);
    
    -- 清理一年前的搜索历史
    DELETE FROM search_history WHERE last_search_time < DATE_SUB(NOW(), INTERVAL 365 DAY);
END //
DELIMITER ;
```

### 4. 事件调度
```sql
-- 每天凌晨清理过期数据
CREATE EVENT daily_cleanup
ON SCHEDULE EVERY 1 DAY STARTS '2024-01-01 03:00:00'
DO CALL clean_old_data();
```

## 🔒 安全配置

### 1. 数据库用户权限
```sql
-- 创建应用专用用户
CREATE USER 'price_user'@'%' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON price_compare.* TO 'price_user'@'%';

-- 创建只读用户（用于报表）
CREATE USER 'price_readonly'@'%' IDENTIFIED BY 'readonly_password';
GRANT SELECT ON price_compare.* TO 'price_readonly'@'%';
```

### 2. 连接池配置
```properties
# 后端应用连接池配置
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.max-lifetime=1200000
```

## 📊 数据备份策略

### 1. 自动备份脚本
```bash
#!/bin/bash
# backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/mysql"
DB_NAME="price_compare"

# 创建全量备份
mysqldump -u root -p$DB_PASSWORD $DB_NAME > $BACKUP_DIR/full_backup_$DATE.sql

# 压缩备份文件
gzip $BACKUP_DIR/full_backup_$DATE.sql

# 保留最近7天的备份
find $BACKUP_DIR -name "full_backup_*.sql.gz" -mtime +7 -delete
```

### 2. 备份计划
```bash
# 添加到crontab，每天凌晨2点执行备份
0 2 * * * /path/to/backup.sh
```

## 🚀 数据库初始化

数据库初始化脚本位于 `sql/init.sql`，包含：
- 表结构创建
- 默认数据插入
- 索引创建
- 存储过程和事件

初始化完成后，系统即可正常使用。