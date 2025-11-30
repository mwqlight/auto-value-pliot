# 价格比价系统 (Price Compare System)

基于 SpringBoot + Vue3 的前后端分离价格比价系统，支持多电商平台商品价格实时监控和比较。

## 🚀 功能特性

### 核心功能
- 🔍 **智能搜索** - 支持多关键词搜索，智能匹配商品
- 📊 **价格比较** - 实时比较多个电商平台价格
- 📈 **价格趋势** - 历史价格走势分析
- 🔔 **降价提醒** - 价格变动实时通知
- 👤 **用户中心** - 个性化搜索历史和收藏

### 支持平台
- 🛒 淘宝/天猫
- 🛒 京东
- 🛒 拼多多
- 🛒 苏宁易购
- 🛒 唯品会

## 🛠️ 技术栈

### 后端技术
- **框架**: Spring Boot 3.0 + MyBatis Plus
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.0
- **安全**: JWT + Spring Security
- **文档**: SpringDoc OpenAPI 3.0

### 前端技术
- **框架**: Vue 3 + TypeScript
- **构建**: Vite 4.0
- **UI组件**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **图表**: ECharts 5.0

### 部署运维
- **容器化**: Docker + Docker Compose
- **监控**: Spring Boot Actuator
- **日志**: Logback + ELK Stack

## 📦 项目结构

```
price-compare-system/
├── price-compare-backend/     # 后端SpringBoot项目
│   ├── src/main/java/        # 后端源码
│   ├── src/main/resources/   # 配置文件
│   ├── Dockerfile            # 后端Docker配置
│   └── pom.xml              # Maven配置
├── price-compare-frontend/   # 前端Vue3项目
│   ├── src/                  # 前端源码
│   ├── Dockerfile            # 前端Docker配置
│   └── package.json          # 依赖配置
├── sql/                      # 数据库脚本
├── docker-compose.yml        # 容器编排
├── deploy.sh                 # 部署脚本
└── README.md                 # 项目文档
```

## 🚀 快速开始

### 环境要求
- Docker 20.10+
- Docker Compose 2.0+
- Java 17+
- Node.js 18+

### 一键部署

```bash
# 克隆项目
git clone <repository-url>
cd price-compare-system

# 一键部署
./deploy.sh
```

### 手动部署

#### 1. 启动基础设施
```bash
# 启动数据库和缓存
docker-compose up -d mysql redis
```

#### 2. 启动后端服务
```bash
cd price-compare-backend

# 构建并启动
mvn clean package
docker-compose up -d backend
```

#### 3. 启动前端服务
```bash
cd price-compare-frontend

# 安装依赖并构建
npm install
npm run build

# 启动服务
docker-compose up -d frontend
```

## 📖 使用说明

### 访问地址
- **前端界面**: http://localhost:80
- **后端API**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html

### 默认账号
- 用户名: `admin`
- 密码: `admin123`

### 主要功能

#### 1. 商品搜索
- 输入商品关键词进行搜索
- 支持模糊匹配和智能推荐
- 实时显示各平台价格对比

#### 2. 价格比较
- 多维度价格对比（当前价、原价、折扣）
- 销量和评分信息展示
- 店铺信誉和配送信息

#### 3. 价格趋势
- 历史价格走势图表
- 价格波动分析
- 最低价提醒

#### 4. 用户功能
- 搜索历史记录
- 商品收藏管理
- 个性化推荐

## 🔧 开发指南

### 后端开发

```bash
cd price-compare-backend

# 启动开发环境
mvn spring-boot:run

# 运行测试
mvn test

# 构建镜像
docker build -t price-compare-backend .
```

### 前端开发

```bash
cd price-compare-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 构建镜像
docker build -t price-compare-frontend .
```

## 📊 API接口

### 商品搜索
```http
GET /api/products/search?keyword=iPhone 15&platforms=jd,taobao
```

### 商品详情
```http
GET /api/products/{productId}/detail
```

### 价格趋势
```http
GET /api/products/{productId}/trend?days=30
```

### 用户认证
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

## 🔒 安全配置

### JWT配置
```yaml
jwt:
  secret: your-secret-key
  expiration: 86400
```

### 数据库安全
- 使用参数化查询防止SQL注入
- 密码BCrypt加密存储
- 敏感数据加密传输

### API安全
- 接口限流和防刷
- 敏感操作二次验证
- 请求参数校验

## 📈 监控告警

### 健康检查
```http
GET /actuator/health
```

### 指标监控
```http
GET /actuator/metrics
```

### 日志查询
```bash
# 查看后端日志
docker-compose logs -f backend

# 查看前端日志
docker-compose logs -f frontend
```

## 🐛 故障排除

### 常见问题

#### 1. 服务启动失败
```bash
# 检查服务状态
docker-compose ps

# 查看详细日志
docker-compose logs [service-name]
```

#### 2. 数据库连接失败
```bash
# 检查数据库状态
docker-compose logs mysql

# 重启数据库服务
docker-compose restart mysql
```

#### 3. 内存不足
```bash
# 调整JVM参数
export JAVA_OPTS="-Xmx1g -Xms512m"
```

### 性能优化

#### 1. 数据库优化
```sql
-- 添加索引
ALTER TABLE product_price ADD INDEX idx_price_platform (price, platform_code);
```

#### 2. 缓存优化
```yaml
# Redis配置
spring:
  redis:
    timeout: 5000
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
```

#### 3. 前端优化
```javascript
// 启用Gzip压缩
server:
  compression:
    enabled: true
```

## 🤝 贡献指南

### 开发流程
1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 代码规范
- 遵循阿里巴巴Java开发规范
- 使用Prettier格式化前端代码
- 提交信息使用Conventional Commits格式

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目主页: [GitHub Repository]
- 问题反馈: [Issues]
- 邮箱: dev@pricecompare.com

## 🙏 致谢

感谢以下开源项目的支持：
- Spring Boot
- Vue.js
- Element Plus
- MyBatis Plus
- ECharts

---

⭐ 如果这个项目对你有帮助，请给我们一个Star！
