import config from "@/config"
import http from "@/utils/request"

export default {
	getInfo: {
		url: `${config.API_URL}/chat/info`,
		name: "应用聊天详情",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	getSessionList: {
		url: `${config.API_URL}/chat/sessionList`,
		name: "获取会话列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	createSession: {
		url: `${config.API_URL}/chat/createSession`,
		name: "创建会话",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	updateSession: {
		url: `${config.API_URL}/chat/updateSession`,
		name: "更新会话",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
}
