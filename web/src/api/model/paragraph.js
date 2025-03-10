import config from "@/config"
import http from "@/utils/request"

export default {
	getList: {
		url: `${config.API_URL}/paragraph/list`,
		name: "获取段落列表",
		get: async function(data={}){
			return await http.get(this.url, data);
		}
	}
}
