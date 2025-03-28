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
	createSession: {
		url: `${config.API_URL}/chat/info`,
		name: "创建会话",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	}
}
