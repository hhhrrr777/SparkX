import config from "@/config"
import http from "@/utils/request"

export default {
	sendMessage: {
		url: `${config.API_URL}/chat/chat`,
		name: "发送消息",
		post: async function(data= {}) {

			return await http.post(this.url, data, {
				headers: {
					'Accept': 'text/event-stream'
				}
			});
		}
	}
}
