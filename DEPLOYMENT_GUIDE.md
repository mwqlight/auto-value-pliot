# 部署指南

## 🚀 快速开始

### 环境要求

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| Docker | 20.10+ | 容器化部署 |
| Docker Compose | 2.0+ | 多容器编排 |
| Java | 17+ | 后端运行环境 |
| Node.js | 18+ | 前端构建环境 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存 |

### 一键部署

```bash
# 克隆项目
git clone <repository-url>
cd auto-value-pliot

# 一键部署
./deploy.sh
```

## 📦 手动部署步骤

### 1. 环境准备

#### 安装Docker和Docker Compose

**macOS**:
```bash
# 使用Homebrew安装
brew install --cask docker

# 或者下载Docker Desktop
# https://www.docker.com/products/docker-desktop/
```

**Linux (Ubuntu)**:
```bash
# 安装Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# 安装Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

#### 验证安装
```bash
docker --version
docker-compose --version
```

### 2. 配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑环境变量
nano .env
```

**关键配置项**:
```env
# 应用配置
APP_NAME=price-compare
APP_VERSION=1.0.0
APP_PORT=8080

# 数据库配置
DB_HOST=mysql
DB_PORT=3306
DB_NAME=price_compare
DB_USER=root
DB_PASSWORD=root

# Redis配置
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=

# 日志配置
LOG_LEVEL=INFO
LOG_PATH=/app/logs

# 安全配置
JWT_SECRET=your-jwt-secret-key-here
JWT_EXPIRATION=86400

# 爬虫配置
CRAWL_TIMEOUT=5000
CRAWL_RETRIES=3
CRAWL_RATE_LIMIT=1000
```

### 3. 数据库初始化

```bash
# 创建数据目录
mkdir -p data/mysql data/redis

# 设置目录权限
chmod -R 755 data/
```

### 4. 构建和启动服务

```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 查看服务状态
docker-compose ps
```

### 5. 验证部署

```bash
# 检查后端服务
curl http://localhost:8080/actuator/health

# 检查前端服务
curl http://localhost:3000

# 查看日志
docker-compose logs -f backend
docker-compose logs -f frontend
```

## 🔧 详细配置说明

### Docker配置详解

#### 后端Dockerfile
```dockerfile
# 多阶段构建：构建阶段
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# 下载依赖（利用Docker缓存）
RUN mvn dependency:go-offline

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre
WORKDIR /app

# 复制JAR文件
COPY --from=builder /app/target/*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs

# 设置JVM参数
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

#### 前端Dockerfile
```dockerfile
# 构建阶段
FROM node:18-alpine AS builder
WORKDIR /app

# 复制package文件
COPY package*.json ./

# 安装依赖
RUN npm ci --only=production

# 复制源代码
COPY . .

# 构建应用
RUN npm run build

# 运行阶段
FROM nginx:alpine

# 复制nginx配置
COPY nginx.conf /etc/nginx/nginx.conf

# 复制构建文件
COPY --from=builder /app/dist /usr/share/nginx/html

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]
```

### Nginx配置

```nginx
# nginx.conf
worker_processes auto;

error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;

    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;

    # Gzip压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # 静态文件缓存
    server {
        listen 80;
        server_name localhost;
        root /usr/share/nginx/html;
        index index.html;

        # 静态资源缓存
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }

        # SPA路由支持
        location / {
            try_files $uri $uri/ /index.html;
        }

        # API代理到后端
        location /api/ {
            proxy_pass http://backend:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # 健康检查
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
```

### Docker Compose配置

```yaml
# docker-compose.yml
version: '3.8'

services:
  # MySQL数据库
  mysql:
    image: mysql:8.0
    container_name: price-compare-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: price_compare
      MYSQL_USER: price_user
      MYSQL_PASSWORD: price_pass
    ports:
      - "3306:3306"
    volumes:
      - ./data/mysql:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql
    command: --default-authentication-plugin=mysql_native_password
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  # Redis缓存
  redis:
    image: redis:6.2-alpine
    container_name: price-compare-redis
    ports:
      - "6379:6379"
    volumes:
      - ./data/redis:/data
    command: redis-server --appendonly yes
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      timeout: 10s
      retries: 5

  # 后端服务
  backend:
    build: ./price-compare-backend
    container_name: price-compare-backend
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_HOST=mysql
      - DB_PORT=3306
      - DB_NAME=price_compare
      - DB_USER=root
      - DB_PASSWORD=root
      - REDIS_HOST=redis
      - REDIS_PORT=6379
      - JWT_SECRET=your-jwt-secret-key-here
    ports:
      - "8080:8080"
    volumes:
      - ./logs/backend:/app/logs
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      timeout: 10s
      retries: 5

  # 前端服务
  frontend:
    build: ./price-compare-frontend
    container_name: price-compare-frontend
    ports:
      - "3000:80"
    depends_on:
      - backend
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:80/health"]
      timeout: 10s
      retries: 5

# 网络配置
networks:
  default:
    name: price-compare-network
```

## 🛠️ 运维管理

### 常用命令

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f [service_name]

# 进入容器
docker-compose exec backend bash

# 构建镜像
docker-compose build

# 清理无用镜像
docker system prune -f
```

### 数据备份

```bash
# 备份数据库
docker-compose exec mysql mysqldump -u root -proot price_compare > backup_$(date +%Y%m%d).sql

# 备份Redis数据
docker-compose exec redis redis-cli save
cp data/redis/dump.rdb backup_redis_$(date +%Y%m%d).rdb

# 恢复数据库
docker-compose exec -T mysql mysql -u root -proot price_compare < backup.sql
```

### 监控和日志

```bash
# 查看系统资源
docker stats

# 查看容器日志
docker-compose logs --tail=100 backend

# 实时监控日志
docker-compose logs -f frontend

# 查看应用日志
tail -f logs/backend/application.log
```

## 🔒 安全配置

### 生产环境安全建议

1. **修改默认密码**
```bash
# 修改MySQL root密码
ALTER USER 'root'@'%' IDENTIFIED BY 'new_strong_password';

# 修改Redis密码
# 在redis.conf中添加：requirepass your_redis_password
```

2. **配置SSL证书**
```nginx
# nginx SSL配置
server {
    listen 443 ssl;
    server_name your-domain.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/private.key;
    
    # 其他配置...
}
```

3. **防火墙配置**
```bash
# 只开放必要端口
ufw allow 80
ufw allow 443
ufw allow 22
ufw enable
```

### 环境变量安全

```bash
# 使用.env文件管理敏感信息
# 确保.env文件不被提交到版本控制
echo ".env" >> .gitignore

# 生产环境使用密钥管理服务
# AWS Secrets Manager, HashiCorp Vault等
```

## 📈 性能优化

### 数据库优化

```sql
-- 创建索引优化查询性能
CREATE INDEX idx_product_price ON product_price(product_id, price);
CREATE INDEX idx_search_history ON search_history(user_id, last_search_time);

-- 优化表结构
OPTIMIZE TABLE product_price;
ANALYZE TABLE price_trend;
```

### 缓存策略

```yaml
# Redis缓存配置
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
```

### JVM调优

```bash
# 生产环境JVM参数
JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=4 -XX:ConcGCThreads=2"
```

## 🚨 故障排除

### 常见问题

1. **端口冲突**
```bash
# 检查端口占用
netstat -tulpn | grep :8080

# 修改端口配置
# 在docker-compose.yml中修改ports配置
```

2. **数据库连接失败**
```bash
# 检查MySQL服务状态
docker-compose logs mysql

# 检查网络连接
docker-compose exec backend ping mysql
```

3. **内存不足**
```bash
# 查看内存使用
docker stats

# 调整JVM内存参数
JAVA_OPTS="-Xmx512m -Xms256m"
```

### 日志分析

```bash
# 查看错误日志
grep "ERROR" logs/backend/application.log

# 查看慢查询日志
docker-compose exec mysql tail -f /var/log/mysql/slow.log

# 监控系统性能
docker-compose top
```

## 🔄 版本升级

### 升级步骤

1. **备份数据**
```bash
./scripts/backup.sh
```

2. **停止服务**
```bash
docker-compose down
```

3. **更新代码**
```bash
git pull origin main
```

4. **重建镜像**
```bash
docker-compose build --no-cache
```

5. **启动服务**
```bash
docker-compose up -d
```

6. **验证部署**
```bash
curl http://localhost:8080/actuator/health
```

## 📞 技术支持

如果遇到问题，请检查：
1. 查看日志文件：`docker-compose logs`
2. 检查服务状态：`docker-compose ps`
3. 验证网络连接：`docker network ls`
4. 查看文档：README.md 和本指南

如需进一步帮助，请联系技术支持团队。