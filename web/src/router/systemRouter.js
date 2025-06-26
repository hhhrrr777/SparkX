import config from "@/config"

//系统路由
const routes = [
	{
		name: "layout",
		path: "/",
		component: () => import(/* webpackChunkName: "layout" */ '@/layout/index.vue'),
		redirect: config.DASHBOARD_URL || '/dashboard',
		children: [

		]
	},
	{
		path: "/login",
		component: () => import(/* webpackChunkName: "login" */ '@/views/login/index.vue'),
		meta: {
			title: "登录"
		}
	},
	{
		path: "/chat/:appId",
		component: () => import(/* webpackChunkName: "login" */ '@/views/chat/index.vue'),
		meta: {
			title: "SparkAI"
		}
	}
]

export default routes;
