(function() {
	// 解析 URL 参数
	const scriptUrl = document.currentScript.src;
	const urlParams = new URLSearchParams(new URL(scriptUrl).search)

	const protocol = urlParams.get("protocol")
	const host = urlParams.get("host")
	const appId = urlParams.get("appId")

	// 整体样式
	const styleCss = `
		.sparkAI-chat-container .sparkAI-chat-button {
			width: 60px;
			height: 60px;
			position: fixed;
			bottom: 100px;
			right: 20px;
			cursor: pointer;
		}
		.sparkAI-chat-container .sparkAI-chat-button img {
			width: 100%;
			height: 100%;
		}
		.sparkAI-chat-container .sparkAI-chat-box {
			z-index: 1999;
			border-radius: 8px;
			border: 1px solid #ffffff;
			background: rgb(244, 244, 244);
			box-shadow: 0 4px 8px #1f23291a;
			position: fixed;
			bottom: 16px;
			right: 16px;
			overflow: hidden;
			width: 450px;
			height: 600px;
		}
	`

	// 创建图标
	const chatLogoHtml = [
		'<div class="sparkAI-chat-button" id="sparkAI-chat-button">',
		'<img style="height:100%;width:100%;" src="' + protocol + "://" + host + '/img/logo.png" alt="sparkAI">',
		'</div>'
	]

	// 创建聊天窗口div
	const chatContentHtml = [
		'<div class="sparkAI-chat-box">',
		'<iframe id="sparkAI-chat" allow="microphone" src="' + protocol + "://" + host + '/chat/' + appId + '" width="100%" height="100%" frameborder="0"></iframe>',
		'</div>'
	]

	// 初始化sparkAI
	function initSparkAI() {
		const sparkAIDiv=document.createElement('div')
		sparkAIDiv.className = 'sparkAI-chat-container'
		const rootDiv=document.createElement('div')
		rootDiv.className = 'sparkAI-chat-root'

		initBaseStyle(rootDiv)
		sparkAIDiv.appendChild(rootDiv)
		document.body.appendChild(sparkAIDiv)
		createChatDiv(rootDiv)
	}

	// 创建聊天div
	function createChatDiv(root) {
		// 插入图标
		root.insertAdjacentHTML("beforeend", chatLogoHtml.join(""))
		// 插入聊天box
		root.insertAdjacentHTML("beforeend", chatContentHtml.join(""))

		// 点击显示
		document.getElementById('sparkAI-chat-button')
			.addEventListener('click', function () {
			console.log(1213)
		})
	}

	// 初始化样式表
	function initBaseStyle(root) {
		let style = document.createElement('style')
		style.type='text/css'
		style.innerText = styleCss
		root.appendChild(style)
	}

	window.addEventListener('load', initSparkAI)
})();
