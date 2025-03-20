<template>
	<div class="container">
		<div class="chat-box">
			<el-row style="width: 100%; height: 100%">
				<el-col :span="3" class="left-side">
					<div style="padding: 20px">
						<div class="logo">
							<img src="/src/assets/robot.gif" style="width: 30px; height: 30px" alt="" />
							<span class="font-weight-700">智能助手</span>
						</div>
						<div class="chat-tool">
							<div class="new-chat btn-color">
								<el-icon><Plus /></el-icon>
								<span style="margin-left: 5px">新对话</span>
							</div>
						</div>

						<div style="margin-top: 20px">
							<div class="log-item item-active">你叫什么名字</div>
							<div class="log-item">请问php怎么搜索</div>
						</div>
					</div>
				</el-col>
				<el-col :span="21" class="right-side">
					<div class="chat-content-box">
						<div class="chat-msg">
							<!-- 循环对话开始 -->
							<div class="panel" style="background: #f4f4f4;">
								<div class="flex-x-between">
									<div class="chat-msg-content">
										<div class="chat-user">
											<div class="user-icon">
												<img src="/src/assets/user.png" style="width: 30px;height: 30px;"/>
											</div>
											<div class="chat-user-name"></div>
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
												<img src="/src/assets/robot.gif" style="width: 30px;height: 30px;"/>
											</div>
											<div class="chat-user-name"></div>
										</div>
										<div class="answer-content">
											<div class="code-box">
												<div class="answer-content-wrap markdown-body" style="width: 100%">
													<MdPreview noIconfont noPrettier :codeFoldable="false" v-model="compiledMarkdown"/>
												</div>
											</div>
										</div>
										<div class="menu-list">
											<div class="menu-left-side">
												<el-tag bordered style="margin-left: 10px;cursor: pointer;">2条引用</el-tag>
												<el-tag bordered style="margin-left: 10px">1.6s</el-tag>
												<el-tag bordered style="margin-left: 10px">150tokens</el-tag>
											</div>
											<div class="menu-right-side">
												<el-tooltip
													effect="dark"
													content="复制"
													placement="bottom"
												>
													<el-icon size="22" style="margin-left: 10px;cursor: pointer"><CopyDocument /></el-icon>
												</el-tooltip>
												<el-tooltip
													effect="dark"
													content="赞"
													placement="bottom"
												>
													<span class="iconfont icon-zan icon-style"></span>
												</el-tooltip>
												<el-tooltip
													effect="dark"
													content="踩"
													placement="bottom"
												>
													<span class="iconfont icon-cai icon-style"></span>
												</el-tooltip>
												<el-tooltip
													effect="dark"
													content="播报"
													placement="bottom"
												>
													<span class="iconfont icon-bobao icon-style"></span>
												</el-tooltip>
											</div>

										</div>
									</div>
								</div>
							</div>

						</div>

						<div class="chat-area">
							<div class="input-box">
								<el-input type="textarea" placeholder="输入你的问题或需求"
										  v-model="chatMsg"
										  max-length="3000" allow-clear show-word-limit :rows="3" class="no-border"/>
							</div>
							<div class="send-btn" @click="send">
								<div class="send-icon">
									<el-icon style="color: #5E17EB" size="28"><Promotion /></el-icon>
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
import {CopyDocument, Plus, Promotion} from "@element-plus/icons-vue";
import { config, MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { fetchEventSource } from '@microsoft/fetch-event-source';
import configInfo from "@/config"

export default {
	components: {CopyDocument, Plus, Promotion, MdPreview},
	data() {
		return {
			compiledMarkdown: "",
			chatMsg: ''
		}
	},
	mounted() {
		config({
			markdownItConfig(md) {
				md.renderer.rules.image = (tokens, idx, options, env, self) => {
					tokens[idx].attrSet('style', 'display:inline-block;min-height:33px;padding:0;margin:0')
					if (tokens[idx].content) {
						tokens[idx].attrSet('title', tokens[idx].content)
					}
					tokens[idx].attrSet(
						'onerror',
						'this.src="/src/assets/load_error.png";this.onerror=null;this.height="33px"'
					)
					return md.renderer.renderToken(tokens, idx, options)
				}
				md.renderer.rules.link_open = (tokens, idx, options, env, self) => {
					tokens[idx].attrSet('target', '_blank')
					return md.renderer.renderToken(tokens, idx, options)
				}
				document.appendChild
			}
		})
		this.compiledMarkdown = ``;
	},
	methods: {
		// 发送消息
		async send() {
			let that = this
			fetchEventSource(`${configInfo.API_URL}/chat/sseChat`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify({ content: that.chatMsg }),
				onmessage(ev) {
					console.log('Received message:', ev);
					// 这里可以根据接收到的流式数据更新前端界面
					that.compiledMarkdown += ev.data.replace("-_-_wrap_-_-", "\r\n")
				},
				onclose() {
					console.log('Connection closed by server');
				},
				onerror(err) {
					console.error('Error received:', err);
				},
			});
		}
	}
}
</script>
<style>
.no-border .el-textarea__inner {
	box-shadow: none !important; /* 使用 !important 来确保覆盖默认样式 */
}
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
.chat-tool:hover {
	background: #5E17EB;
	color: #fff;
}
.chat-tool {
	margin-top: 30px;
	width: 100%;
	display: flex;
	align-items: center;
	border: 1px solid #5E17EB;
	border-radius: 5px;
	justify-content: center;
	cursor: pointer;
	padding: 8px 0;
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
	width: calc(100% - 400px);
	height: 100%;
	margin: 0 auto;

	.chat-msg {
		height: 88%;
		width: 100%;
		overflow-y: scroll;
		overflow-x: hidden;

		.panel {
			background: #fff;
			padding: 10px;
			margin-top: 10px;
			border-radius: 5px;

			.flex-x-between {
				display: flex;
				align-items: flex-start;
				justify-content: space-between;

				.chat-msg-content {
					display: flex;
					align-items: flex-start;
					justify-content: space-between;
					flex-direction: column;
					width: 100%;
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
						width: 100%;

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
	height: 73px;
	border-radius: 5px;
	border: 1px solid var(--color-border-3);
	background: #fff;
	margin-top: 40px;
	display: flex;
	.input-box {
		width: 95%;
	}
}
.arco-textarea-wrapper {
	background-color: #fff !important;
}
.arco-textarea-focus {
	border-color: #fff !important;
}
.send-btn {
	width: 5%;
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
.menu-list {
	display: flex;
	align-items: center;
	width: 100%;
	justify-content: space-between;
	border-top: 1px solid #dee0e3;
	padding-top: 10px;
}
.menu-item {
	margin-left: 10px;cursor: pointer;stroke-width: 3;
}
.log-item {
	height: 40px;
	width: 100%;
	display: flex;
	align-items: center;
	cursor: pointer;
	font-weight: 500;
	color: #1f2329;
	padding-left: 10px;
}
.log-item:hover {
	color: #5E17EB;
	background: #eee7fd;
}
.item-active {
	color: #5E17EB;
	background: #eee7fd;
}
.icon-style {
	font-size: 20px;margin-left: 10px;cursor: pointer;
	color: #3f4a54;
}
.menu-left-side {
	display: flex;
	align-items: center;
}
.menu-right-side {
	display: flex;
	align-items: center;
	float: right;
}
</style>
