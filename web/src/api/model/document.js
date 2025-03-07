import config from "@/config"
import http from "@/utils/request"

export default {
	preview: {
		url: `${config.API_URL}/document/preview`,
		name: "上传文档",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	save: {
		url: `${config.API_URL}/document/save`,
		name: "保存文档",
		post: async function(data={}){
			return await http.post(this.url, data);
		}
	}
}
