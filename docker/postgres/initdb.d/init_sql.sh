#!/bin/bash
set -e

# 安装Vector扩展
psql -v ON_ERROR_STOP=1 --username "sparkai" --dbname "sparkai" <<-EOSQL
    CREATE EXTENSION IF NOT EXISTS vector;
EOSQL

# 导入数据
psql -v ON_ERROR_STOP=1 --username "sparkai" --dbname "sparkai" -f /docker-entrypoint-initdb.d/sparkx.sql