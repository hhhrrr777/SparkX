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

						<div style="margin-top: 20px" v-for="item in sessionLog" :key="item.sessionId">
							<div class="log-item item-active">{{ item.title }}</div>
						</div>
					</div>
				</el-col>
				<el-col :span="21" class="right-side">
					<chat-box :setting="setting" :chat-log-msg="chatLogMsg"></chat-box>
				</el-col>
			</el-row>
		</div>
	</div>
</template>

<script>
import chatBox from '@/components/chatContent/index.vue'
import {Plus} from "@element-plus/icons-vue";

export default {
	components: {Plus, chatBox},
	data() {
		return {
			setting: {
				showRelation: 1,
				showTime: 1,
				showTokens: 1,
				showAppraise: 1,
				voiceOut: 1
			},
			chatLogMsg: [],
			sessionLog: []
		}
	},
	mounted() {

	},
	methods: {

	}
}
</script>

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
