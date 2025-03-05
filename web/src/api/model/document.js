import config from "@/config"
import http from "@/utils/request"

export default {
	upload: {
		url: `${config.API_URL}/document/preview`,
		name: "上传文档",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	}
}
