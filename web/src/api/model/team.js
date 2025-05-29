import config from "@/config"
import http from "@/utils/request"

export default {
	userList: {
		url: `${config.API_URL}/team/userList`,
		name: "获取用户列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	}
}
