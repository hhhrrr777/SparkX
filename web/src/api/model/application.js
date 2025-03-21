import config from "@/config"
import http from "@/utils/request"

export default {
	list: {
		url: `${config.API_URL}/application/list`,
		name: "获取应用列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	add: {
		url: `${config.API_URL}/application/add`,
		name: "添加应用",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	info: {
		url: `${config.API_URL}/application/detail`,
		name: "获取应用列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	}
}
