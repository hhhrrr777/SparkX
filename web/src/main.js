import {createApp} from 'vue'
import ElementPlus from 'element-plus'
import scui from './scui'
import i18n from './locales'
import router from './router'
import App from './App.vue'
import 'element-plus/dist/index.css';
import { config } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css';
import {createPinia} from "pinia";

config({
	editorExtensions: {
		highlight: {
			instance: highlight
		},
		screenfull: {
			instance: screenfull
		},
		katex: {
			instance: katex
		},
		cropper: {
			instance: Cropper
		},
		mermaid: {
			instance: mermaid
		}
	}
})

const pinia = createPinia()
const app = createApp(App);

app.use(pinia);

app.use(router);
app.use(ElementPlus);
app.use(i18n);
app.use(scui);


//挂载app
app.mount('#app');
