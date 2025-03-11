import config from "@/config"
import http from "@/utils/request"

export default {
	getList: {
		url: `${config.API_URL}/paragraph/list`,
		name: "获取段落列表",
		get: async function(data={}){
			return await http.get(this.url, data);
		}
	},
	active: {
		url: `${config.API_URL}/paragraph/active`,
		name: "激活、关闭段落",
		post: async function(data={}){
			return await http.post(this.url, data);
		}
	}
}
