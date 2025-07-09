# Maven 构建
FROM maven:3.8.6-eclipse-temurin-17-alpine AS builder

# 设置阿里云镜像加速
RUN mkdir -p /usr/share/maven/ref/ && \
    echo > /usr/share/maven/ref/settings.xml <<EOF
<settings>
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
      <mirrorOf>*</mirrorOf>
    </mirror>
  </mirrors>
</settings>
EOF

# 复制整个项目（包括父pom和子模块）
WORKDIR /build
COPY . .

# 构建特定子模块（sparkx-web）
RUN mvn clean package -pl sparkx-web -am -DskipTests

# 使用国内推荐的镜像源
FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17-8.6

WORKDIR /app
# 从构建阶段复制生成的JAR包（根据实际路径调整）
COPY --from=builder /build/sparkx-web/target/sparkx-web-*.jar ./sparkx-web.jar

ENTRYPOINT ["/bin/sh", "-c", "java -jar sparkx-web.jar"]