import config from "@/config"
import http from "@/utils/request"

export default {
	list: {
		url: `${config.API_URL}/dataset/list`,
		name: "获取知识库列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	add: {
		url: `${config.API_URL}/dataset/add`,
		name: "添加知识库",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	hitTest: {
		url: `${config.API_URL}/dataset/hitTest`,
		name: "命中测试",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	}
}
