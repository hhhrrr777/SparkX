FROM postgres:15-alpine

# 复制初始化脚本
COPY initdb.d/ /docker-entrypoint-initdb.d/

# 设置权限
RUN chmod a+r /docker-entrypoint-initdb.d/*