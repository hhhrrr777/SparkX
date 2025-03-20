import config from "@/config"
import http from "@/utils/request"

export default {
	list: {
		url: `${config.API_URL}/application/list`,
		name: "获取应用列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	}
}
