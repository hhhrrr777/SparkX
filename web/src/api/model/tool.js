import config from "@/config"
import http from "@/utils/request"

export default {
	list: {
		url: `${config.API_URL}/tool/list`,
		name: "获取插件列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	add: {
		url: `${config.API_URL}/tool/add`,
		name: "创建插件",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	edit: {
		url: `${config.API_URL}/tool/edit`,
		name: "编辑插件",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	}
}
