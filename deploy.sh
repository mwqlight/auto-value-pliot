#!/bin/bash

# 价格比价系统部署脚本

echo "🚀 开始部署价格比价系统..."

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker未安装，请先安装Docker"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose未安装，请先安装Docker Compose"
    exit 1
fi

# 创建必要的目录
echo "📁 创建数据目录..."
mkdir -p data/mysql data/redis logs/backend logs/frontend

# 构建镜像
echo "🔨 构建Docker镜像..."
docker-compose build

# 启动服务
echo "🚀 启动服务..."
docker-compose up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 检查服务状态
echo "🔍 检查服务状态..."

# 检查后端服务
if curl -f http://localhost:8080/api/health > /dev/null 2>&1; then
    echo "✅ 后端服务运行正常"
else
    echo "❌ 后端服务启动失败"
    docker-compose logs backend
    exit 1
fi

# 检查前端服务
if curl -f http://localhost:80 > /dev/null 2>&1; then
    echo "✅ 前端服务运行正常"
else
    echo "❌ 前端服务启动失败"
    docker-compose logs frontend
    exit 1
fi

# 检查数据库连接
if docker exec price-compare-mysql mysql -uprice_user -pprice_pass -e "USE price_compare; SELECT 1;" > /dev/null 2>&1; then
    echo "✅ 数据库连接正常"
else
    echo "❌ 数据库连接失败"
    docker-compose logs mysql
    exit 1
fi

# 检查Redis连接
if docker exec price-compare-redis redis-cli ping | grep -q PONG; then
    echo "✅ Redis连接正常"
else
    echo "❌ Redis连接失败"
    docker-compose logs redis
    exit 1
fi

echo ""
echo "🎉 部署完成！"
echo ""
echo "📊 服务访问地址："
echo "   前端：http://localhost:80"
echo "   后端API：http://localhost:8080"
echo "   数据库：localhost:3306 (用户：price_user，密码：price_pass)"
echo "   Redis：localhost:6379"
echo ""
echo "🔧 常用命令："
echo "   查看日志：docker-compose logs -f [service]"
echo "   停止服务：docker-compose down"
echo "   重启服务：docker-compose restart"
echo "   更新部署：./deploy.sh"
echo ""

# 显示服务状态
echo "📈 当前服务状态："
docker-compose ps