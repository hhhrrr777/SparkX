// 运行时配置（覆盖镜像内 dist/app.config.js）
// 此文件由 docker-compose.yml bind mount 到容器 /usr/share/nginx/html/app.config.js，
// 前端打包产物通过 <script> 引用 /app.config.js，挂到 window.__PRODUCTION__{shortName}__CONF__，
// src/utils/env.ts 运行时从该全局变量读取。
//
// ⚠️ 全局变量名必须与 build/getConfigFileName.ts 生成规则完全一致：
//   `__PRODUCTION__${VITE_GLOB_APP_SHORT_NAME}__CONF__` 再 .toUpperCase()
// VITE_GLOB_APP_SHORT_NAME = SparkX智能体平台 → 全大写后英文变大写、中文不变 →
//   __PRODUCTION__SPARKX智能体平台__CONF__
// 改了 short_name 就要同步改这里的变量名，否则 bundle 读到 undefined 会解构报错。
//
// 字段名必须是 VITE_GLOB_*，与 .env.production 一致；改完只需重建容器，无需重新构建镜像。
window.__PRODUCTION__SPARKX智能体平台__CONF__ = {
  "VITE_GLOB_APP_TITLE": "SparkX智能体平台",
  "VITE_GLOB_APP_SHORT_NAME": "SparkX智能体平台",
  "VITE_GLOB_API_URL": "http://localhost:7026",
  "VITE_GLOB_API_URL_PREFIX": "",
  "VITE_GLOB_UPLOAD_URL": "",
  "VITE_GLOB_FILE_URL": "http://127.0.0.1:9000"
};
