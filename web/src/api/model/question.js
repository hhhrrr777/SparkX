import config from "@/config"
import http from "@/utils/request"

export default {
	list: {
		url: `${config.API_URL}/question/list`,
		name: "获取问题列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	add: {
		url: `${config.API_URL}/question/add`,
		name: "添加问题",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	}
}
