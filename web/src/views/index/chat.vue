<template>
	<div class="container">
		<div class="chat-box">
			<el-row style="width: 100%; height: 100%">
				<el-col :span="4" class="left-side">
					<div style="padding: 20px">
						<div class="logo">
							<img src="" style="width: 30px; height: 30px" alt="" />
							<span class="font-weight-700">智能助手</span>
						</div>
						<div class="chat-tool">
							<div class="new-chat btn-color">
								<span>新对话</span>
							</div>
						</div>

						<div style="margin-top: 20px" :bordered="false" :split="false">
							<div>你叫什么名字</div>
							<div>请问php怎么搜索</div>
						</div>
					</div>
				</el-col>
				<el-col :span="20" class="right-side">
					<div class="chat-content-box">
						<div class="chat-msg">
							<!-- 循环对话开始 -->
							<div class="panel" style="background: #f4f4f4;">
								<div class="flex-x-between">
									<div class="chat-msg-content">
										<div class="chat-user">
											<div class="user-icon">
												<img src="/src/assets/user.png" style="width: 40px;height: 40px;"/>
											</div>
											<div class="chat-user-name">用户333</div>
										</div>
										<div class="answer-content">
											<div class="code-box">
												<div class="answer-content-wrap">
													<p>你好请问你是</p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<!-- 循环对话结束 -->
							<div class="panel">
								<div class="flex-x-between">
									<div class="chat-msg-content">
										<div class="chat-user">
											<div class="user-icon">
												<img src="/src/assets/robot.gif" style="width: 40px;height: 40px;"/>
											</div>
											<div class="chat-user-name">DingDongAI</div>
										</div>
										<div class="answer-content">
											<div class="code-box">
												<div class="answer-content-wrap markdown-body">
													<MdEditor
														v-model="compiledMarkdown"
														:toolbars="[]"
														class="magnify-md-editor"
													>
													</MdEditor>
												</div>
											</div>
										</div>
										<div class="menu-list">
											<el-tag bordered style="margin-left: 10px;cursor: pointer;">2条上下文</el-tag>
											<el-tag bordered style="margin-left: 10px">1.6s</el-tag>
										</div>
									</div>
								</div>
							</div>

						</div>

						<div class="chat-area">
							<div class="input-box">
								<el-input type="textarea" placeholder="输入你的问题或需求" :max-length="500" allow-clear show-word-limit />
							</div>
							<div class="send-btn">
								<div class="send-icon">
									<el-icon style="font-size: 24px;color: #fff"><Promotion /></el-icon>
								</div>
							</div>
						</div>
					</div>
				</el-col>
			</el-row>
		</div>
	</div>
</template>

<script>
import hljs from "highlight.js";
import {Promotion} from "@element-plus/icons-vue";
import { MdPreview } from 'md-editor-v3'

export default {
	components: {Promotion, MdPreview},
	data() {
		return {
			compiledMarkdown: ""
		}
	},
	mounted() {

		this.compiledMarkdown = `
<!DOCTYPE html>
<html>
<title>

    <head>地址</head>
</title>

<body>
    <span>Hello wolrd</span>
</body>

</html>
`;
		/*this.compiledMarkdown = markdown.render(markdownContent);
		console.log(this.compiledMarkdown);
		this.$nextTick(() => {
			const blocks = document.querySelectorAll("pre code");
			blocks.forEach((block) => {
				hljs.highlightBlock(block);
			});
		})*/

	},
	methods: {

	}
}
</script>

<style>
@import 'highlight.js/styles/atom-one-dark.css';
</style>

<style lang="scss" scoped>
.container {
	background-color: #f4f4f4;
	padding: 16px 20px 0;
	display: flex;
}
.chat-box {
	width: 100%;
	height: calc(100vh - 30px);
	background: #fff;
	border-radius: 10px;
}
.left-side {
	width: 390px;
	height: 100%;
	border-right: 1px solid #f4f4f4;
}
.right-side {
	background: #f4f4f4;
	height: 100%;
}
.logo {
	display: flex;
	justify-content: left;
	align-items: center;

	span {
		margin-left: 10px;
		font-weight: 700;
	}
}
.chat-tool {
	margin-top: 30px;
	width: 100%;
	display: flex;
	align-content: center;

	.new-chat {
		width: 80%;
		height: 30px;
		border: 1px solid var(--color-border-3);
		border-radius: 20px;
		display: flex;
		align-items: center;
		justify-content: center;
		cursor: pointer;
	}
	.new-chat:hover {
		background: rgb(var(--primary-5));
		color: #fff;
		border: 1px solid var(--primary-5);
	}

	.clear {
		width: 30px;
		height: 30px;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 50%;
		border: 1px solid var(--color-border-3);
		margin-left: 20px;
		cursor: pointer;
	}
	.clear:hover {
		background: rgb(var(--primary-5));
		color: #fff;
		border: 1px solid var(--primary-5);
	}
}
.btn-color {
	color: rgb(var(--primary-5));
}
.arco-list-header {
	font-size: 13px;
}
.arco-list-item {
	font-size: 12px;
	cursor: pointer;
}
.arco-list-item:hover {
	background: rgb(var(--primary-2));
	color: var(--color-text-2);
	border-radius: 20px;
}
.chat-content-box {
	max-width: 950px;
	height: 100%;
	margin: 0 auto;

	.chat-msg {
		height: 85%;
		width: 100%;
		overflow-y: scroll;
		overflow-x: hidden;

		.panel {
			background: #fff;
			padding: 20px;
			margin-top: 10px;

			.flex-x-between {
				display: flex;
				align-items: flex-start;
				justify-content: space-between;

				.chat-msg-content {
					display: flex;
					align-items: flex-start;
					justify-content: space-between;
					flex-direction: column;

					.chat-user {
						display: flex;
						align-items: center;
					}

					.chat-user-name {
						margin-left: 10px;
						font-weight: 700;
						font-size: 15px;
					}

					.user-icon {
						width: 40px;
						height: 40px;
						display: flex;
						justify-content: center;
						align-items: center;
					}

					.answer-content {
						gap: 0;
						margin-top: 3px;
						overflow: hidden;
						display: flex;
						flex-direction: column;
						flex: 1;

						.code-box {
							padding-left: 8px;
							margin-top: 3px;
							overflow: hidden;
							flex: 1;
						}

						.answer-content-wrap {
							font-style: normal;
							font-size: 16px;
							line-height: 1.5;
							word-wrap: break-word;
						}
					}
				}
			}
		}
	}
}

.chat-area {
	width: 100%;
	height: 80px;
	border-radius: 10px;
	border: 1px solid var(--color-border-3);
	background: #fff;
	margin-top: 40px;
	display: flex;
	.input-box {
		margin-top: 14px;
		width: 90%;
	}
}
.arco-textarea-wrapper {
	background-color: #fff !important;
}
.arco-textarea-focus {
	border-color: #fff !important;
}
.send-btn {
	width: 10%;
	height: 100%;
	display: flex;
	align-items: center;
	justify-content: center;

	.send-icon {
		width: 45px;
		display: flex;
		align-items: center;
		justify-content: center;
		cursor: pointer;
		background: rgb(var(--primary-5));
		border-radius: 50%;
		height: 45px;
	}
}
.chat-msg::-webkit-scrollbar { width: 0 !important }
.chat-msg { -ms-overflow-style: none; }
.chat-msg { overflow: -moz-scrollbars-none; }
.menu-list {
	display: flex;
	align-items: center;
}
.menu-item {
	margin-left: 10px;cursor: pointer;stroke-width: 3;
}
</style>
