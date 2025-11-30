# API接口文档

## 📋 接口概览

### 基础信息
- **API版本**: v1
- **基础路径**: `/api/v1`
- **认证方式**: JWT Token
- **数据格式**: JSON
- **字符编码**: UTF-8

### 响应格式
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {},
    "timestamp": 1650000000000
}
```

### 错误码说明
| 状态码 | 说明 | 业务码 | 业务说明 |
|--------|------|--------|----------|
| 200 | 成功 | 200 | 操作成功 |
| 400 | 客户端错误 | 40001 | 参数校验失败 |
| 401 | 未认证 | 40101 | Token无效 |
| 403 | 权限不足 | 40301 | 无访问权限 |
| 404 | 资源不存在 | 40401 | 商品不存在 |
| 500 | 服务端错误 | 50001 | 系统异常 |

## 🔑 认证接口

### 1. 用户注册

**接口**: `POST /api/v1/auth/register`

**请求参数**:
```json
{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "phone": "13800138000"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "注册成功",
    "data": {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "phone": "13800138000",
        "avatar": null,
        "status": 1,
        "createTime": "2024-01-01T10:00:00"
    },
    "timestamp": 1650000000000
}
```

### 2. 用户登录

**接口**: `POST /api/v1/auth/login`

**请求参数**:
```json
{
    "username": "testuser",
    "password": "password123"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "user": {
            "id": 1,
            "username": "testuser",
            "email": "test@example.com",
            "phone": "13800138000",
            "avatar": null,
            "status": 1
        }
    },
    "timestamp": 1650000000000
}
```

### 3. 获取用户信息

**接口**: `GET /api/v1/auth/userinfo`

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "获取成功",
    "data": {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "phone": "13800138000",
        "avatar": null,
        "status": 1,
        "lastLoginTime": "2024-01-01T10:00:00",
        "createTime": "2024-01-01T09:00:00"
    },
    "timestamp": 1650000000000
}
```

## 🔍 商品搜索接口

### 1. 搜索商品

**接口**: `GET /api/v1/products/search`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| platforms | string | 否 | 平台代码，多个用逗号分隔 |
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页大小，默认20 |

**请求示例**:
```
GET /api/v1/products/search?keyword=iPhone15&platforms=jd,tmall&page=1&size=10
```

**响应示例**:
```json
{
    "code": 200,
    "message": "搜索成功",
    "data": {
        "items": [
            {
                "id": "iphone15_001",
                "name": "iPhone 15 Pro Max 256GB",
                "platformCode": "jd",
                "platformProductId": "100001",
                "price": 8999.00,
                "originalPrice": 9999.00,
                "discount": "9折",
                "sales": 1500,
                "rating": 4.8,
                "productUrl": "https://jd.com/product/100001",
                "shopName": "京东官方旗舰店",
                "shopRating": 4.9,
                "delivery": "京东物流 次日达",
                "isLowest": true,
                "crawlTime": "2024-01-01T10:00:00"
            }
        ],
        "total": 1,
        "page": 1,
        "size": 10,
        "pages": 1
    },
    "timestamp": 1650000000000
}
```

### 2. 获取商品详情

**接口**: `GET /api/v1/products/{productId}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| productId | string | 商品ID |

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| platformCode | string | 是 | 平台代码 |

**请求示例**:
```
GET /api/v1/products/iphone15_001?platformCode=jd
```

**响应示例**:
```json
{
    "code": 200,
    "message": "获取成功",
    "data": {
        "id": "iphone15_001",
        "name": "iPhone 15 Pro Max 256GB",
        "platformCode": "jd",
        "platformProductId": "100001",
        "price": 8999.00,
        "originalPrice": 9999.00,
        "discount": "9折",
        "sales": 1500,
        "rating": 4.8,
        "productUrl": "https://jd.com/product/100001",
        "shopName": "京东官方旗舰店",
        "shopRating": 4.9,
        "delivery": "京东物流 次日达",
        "isLowest": true,
        "crawlTime": "2024-01-01T10:00:00",
        "description": "iPhone 15 Pro Max 采用钛金属设计...",
        "images": [
            "https://img14.360buyimg.com/n1/s450x450_jfs/t1/123456/1/12345/123456.jpg"
        ],
        "specifications": [
            {
                "name": "颜色",
                "value": "钛金属色"
            },
            {
                "name": "存储",
                "value": "256GB"
            }
        ]
    },
    "timestamp": 1650000000000
}
```

### 3. 批量搜索商品

**接口**: `POST /api/v1/products/batch-search`

**请求参数**:
```json
{
    "keyword": "iPhone15",
    "platforms": ["jd", "tmall", "pdd"],
    "page": 1,
    "size": 20
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "批量搜索成功",
    "data": {
        "jd": {
            "items": [...],
            "total": 10
        },
        "tmall": {
            "items": [...],
            "total": 8
        },
        "pdd": {
            "items": [...],
            "total": 15
        }
    },
    "timestamp": 1650000000000
}
```

## 📊 价格比较接口

### 1. 价格比较

**接口**: `GET /api/v1/compare/price`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | string | 是 | 商品ID |
| platforms | string | 否 | 平台代码，多个用逗号分隔 |

**请求示例**:
```
GET /api/v1/compare/price?productId=iphone15_001&platforms=jd,tmall,pdd
```

**响应示例**:
```json
{
    "code": 200,
    "message": "比较成功",
    "data": {
        "productId": "iphone15_001",
        "productName": "iPhone 15 Pro Max 256GB",
        "comparisons": [
            {
                "platformCode": "jd",
                "platformName": "京东",
                "price": 8999.00,
                "originalPrice": 9999.00,
                "discount": "9折",
                "isLowest": true,
                "productUrl": "https://jd.com/product/100001",
                "shopName": "京东官方旗舰店",
                "delivery": "次日达"
            },
            {
                "platformCode": "tmall",
                "platformName": "天猫",
                "price": 9099.00,
                "originalPrice": 9999.00,
                "discount": "9.1折",
                "isLowest": false,
                "productUrl": "https://tmall.com/item/200001",
                "shopName": "天猫官方旗舰店",
                "delivery": "3天内发货"
            }
        ],
        "lowestPrice": 8999.00,
        "highestPrice": 9099.00,
        "priceDifference": 100.00
    },
    "timestamp": 1650000000000
}
```

### 2. 价格趋势

**接口**: `GET /api/v1/compare/trend`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | string | 是 | 商品ID |
| platformCode | string | 是 | 平台代码 |
| days | int | 否 | 天数，默认30 |

**请求示例**:
```
GET /api/v1/compare/trend?productId=iphone15_001&platformCode=jd&days=30
```

**响应示例**:
```json
{
    "code": 200,
    "message": "获取成功",
    "data": {
        "productId": "iphone15_001",
        "platformCode": "jd",
        "trends": [
            {
                "date": "2024-01-01",
                "price": 8999.00,
                "isLowest": false
            },
            {
                "date": "2024-01-02",
                "price": 8999.00,
                "isLowest": false
            },
            {
                "date": "2024-01-03",
                "price": 8799.00,
                "isLowest": true
            }
        ],
        "currentPrice": 8999.00,
        "lowestPrice": 8799.00,
        "highestPrice": 9099.00,
        "averagePrice": 8923.50
    },
    "timestamp": 1650000000000
}
```

## 📝 搜索历史接口

### 1. 获取搜索历史

**接口**: `GET /api/v1/history/search`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页大小，默认10 |

**响应示例**:
```json
{
    "code": 200,
    "message": "获取成功",
    "data": {
        "items": [
            {
                "id": 1,
                "keyword": "iPhone15",
                "searchCount": 5,
                "lastSearchTime": "2024-01-01T10:00:00",
                "createTime": "2024-01-01T09:00:00"
            },
            {
                "id": 2,
                "keyword": "MacBook Pro",
                "searchCount": 3,
                "lastSearchTime": "2024-01-01T09:30:00",
                "createTime": "2024-01-01T09:00:00"
            }
        ],
        "total": 2,
        "page": 1,
        "size": 10,
        "pages": 1
    },
    "timestamp": 1650000000000
}
```

### 2. 删除搜索历史

**接口**: `DELETE /api/v1/history/search/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | long | 历史记录ID |

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null,
    "timestamp": 1650000000000
}
```

### 3. 清空搜索历史

**接口**: `DELETE /api/v1/history/search/clear`

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "清空成功",
    "data": null,
    "timestamp": 1650000000000
}
```

## ⚙️ 平台管理接口

### 1. 获取平台列表

**接口**: `GET /api/v1/platforms`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| enabled | boolean | 否 | 是否启用 |

**请求示例**:
```
GET /api/v1/platforms?enabled=true
```

**响应示例**:
```json
{
    "code": 200,
    "message": "获取成功",
    "data": [
        {
            "id": 1,
            "platformCode": "jd",
            "platformName": "京东",
            "apiBaseUrl": "https://api.jd.com",
            "timeout": 5000,
            "maxRetries": 3,
            "rateLimit": 1000,
            "enabled": true,
            "createTime": "2024-01-01T00:00:00"
        },
        {
            "id": 2,
            "platformCode": "tmall",
            "platformName": "天猫",
            "apiBaseUrl": "https://api.tmall.com",
            "timeout": 5000,
            "maxRetries": 3,
            "rateLimit": 1000,
            "enabled": true,
            "createTime": "2024-01-01T00:00:00"
        }
    ],
    "timestamp": 1650000000000
}
```

### 2. 更新平台配置

**接口**: `PUT /api/v1/platforms/{id}`

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | long | 平台配置ID |

**请求参数**:
```json
{
    "timeout": 6000,
    "maxRetries": 5,
    "rateLimit": 800,
    "enabled": true
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "更新成功",
    "data": {
        "id": 1,
        "platformCode": "jd",
        "platformName": "京东",
        "timeout": 6000,
        "maxRetries": 5,
        "rateLimit": 800,
        "enabled": true,
        "updateTime": "2024-01-01T10:00:00"
    },
    "timestamp": 1650000000000
}
```

## 🔧 系统监控接口

### 1. 健康检查

**接口**: `GET /actuator/health`

**响应示例**:
```json
{
    "status": "UP",
    "components": {
        "db": {
            "status": "UP",
            "details": {
                "database": "MySQL",
                "validationQuery": "isValid()"
            }
        },
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 500000000000,
                "free": 300000000000,
                "threshold": 10485760
            }
        },
        "redis": {
            "status": "UP",
            "details": {
                "version": "6.2.6"
            }
        }
    }
}
```

### 2. 系统信息

**接口**: `GET /actuator/info`

**响应示例**:
```json
{
    "app": {
        "name": "price-compare-backend",
        "version": "1.0.0",
        "description": "商品价格比较系统后端服务"
    },
    "build": {
        "artifact": "price-compare-backend",
        "name": "price-compare-backend",
        "time": "2024-01-01T10:00:00.000Z",
        "version": "1.0.0",
        "group": "com.example"
    }
}
```

## 📚 接口测试

### 使用Postman测试

1. **导入环境变量**
```json
{
    "id": "price-compare-api",
    "name": "Price Compare API",
    "values": [
        {
            "key": "baseUrl",
            "value": "http://localhost:8080",
            "type": "default"
        },
        {
            "key": "token",
            "value": "",
            "type": "default"
        }
    ]
}
```

2. **测试流程**
   - 先调用登录接口获取token
   - 设置环境变量中的token值
   - 测试其他需要认证的接口

### 使用curl测试

```bash
# 登录获取token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# 搜索商品
curl -X GET "http://localhost:8080/api/v1/products/search?keyword=iPhone15" \
  -H "Authorization: Bearer {token}"
```

## 🔒 安全注意事项

1. **Token管理**
   - Token有效期为24小时
   - Token过期后需要重新登录
   - 敏感操作需要二次验证

2. **限流策略**
   - 搜索接口：100次/分钟
   - 详情接口：50次/分钟
   - 批量接口：10次/分钟

3. **数据安全**
   - 所有敏感数据加密传输
   - 用户密码BCrypt加密存储
   - 定期清理过期数据

此文档会随着API的更新而维护，请确保使用最新版本。