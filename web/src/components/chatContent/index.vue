<template>
	<div class="chat-content-box">
		<div class="chat-msg">
			<!-- 循环对话开始 -->
			<div class="panel" :style="{background: (item.source === 'user') ? '#f4f4f4' : '#fff' }" v-for="(item, index) in chatMsg" :key="index">
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
								<div class="answer-content-wrap" style="width: 100%">
									<p v-if="item.source === 'user'">{{ item.content }}</p>
									<MdPreview v-else noIconfont noPrettier :codeFoldable="false" v-model="item.content"/>
								</div>
							</div>
						</div>
						<div class="menu-list" v-if="item.source === 'ai'">
							<div class="menu-left-side">
								<el-tag bordered style="margin-left: 10px;cursor: pointer;" v-if="setting.showRelation === 1">2条引用</el-tag>
								<el-tag bordered style="margin-left: 10px" v-if="setting.showTime === 1">1.6s</el-tag>
								<el-tag bordered style="margin-left: 10px" v-if="setting.showTokens === 1">150tokens</el-tag>
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
									v-if="setting.showAppraise === 1"
									effect="dark"
									content="赞"
									placement="bottom"
								>
									<span class="iconfont icon-zan icon-style"></span>
								</el-tooltip>
								<el-tooltip
									v-if="setting.showAppraise === 1"
									effect="dark"
									content="踩"
									placement="bottom"
								>
									<span class="iconfont icon-cai icon-style"></span>
								</el-tooltip>
								<el-tooltip
									v-if="setting.voiceOut === 1"
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
</template>

<script>
import {CopyDocument, Promotion} from "@element-plus/icons-vue";
import { config, MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { fetchEventSource } from '@microsoft/fetch-event-source';
import configInfo from "@/config"

export default {
	components: {CopyDocument, Promotion, MdPreview},
	props: {
		chatLogMsg: {
			type: Array,
			default: []
		},
		setting: {
			type: Object,
			default: () => {}
		}
	},
	data() {
		return {
			chatMsg: "",
			compiledMarkdown: ""
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
</style>
