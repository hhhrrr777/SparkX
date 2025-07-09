# 使用国内推荐的镜像源
FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17-8.6

WORKDIR /app
COPY sparkx-web-1.0.0.jar /app/

ENTRYPOINT ["/bin/sh", "-c", "java -jar sparkx-web-1.0.0.jar"]