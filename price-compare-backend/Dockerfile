# 后端Dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS build

# 设置工作目录
WORKDIR /app

# 复制pom.xml和源代码
COPY pom.xml .
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 运行时镜像
FROM eclipse-temurin:17-jre

# 设置工作目录
WORKDIR /app

# 复制构建的jar文件
COPY --from=build /app/target/price-compare-backend-*.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xmx512m -Xms256m -Dspring.profiles.active=docker"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]