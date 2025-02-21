import { createApp } from 'vue';
import VueDOMPurifyHTML from 'vue-dompurify-html'
import hljsVuePlugin from '@highlightjs/vue-plugin' // 支持vue3的组件
import ArcoVue from '@arco-design/web-vue';
import ArcoVueIcon from '@arco-design/web-vue/es/icon';
import globalComponents from '@/components';
import router from './router';
import store from './store';
import i18n from './locale';
import directive from './directive';
import './mock';
import App from './App.vue';
// Styles are imported via arco-plugin. See config/plugin/arcoStyleImport.ts in the directory for details
// 样式通过 arco-plugin 插件导入。详见目录文件 config/plugin/arcoStyleImport.ts
// https://arco.design/docs/designlab/use-theme-package
import '@/assets/style/global.less';
import '@/api/interceptor';
import 'highlight.js/styles/atom-one-dark.css' // 样式
import 'highlight.js/lib/common' // 依赖包

const app = createApp(App);

app.use(ArcoVue, {});
app.use(ArcoVueIcon);

app.use(router);
app.use(store);
app.use(i18n);
app.use(globalComponents);
app.use(directive);
app.use(VueDOMPurifyHTML)
app.use(hljsVuePlugin)

app.mount('#app');
