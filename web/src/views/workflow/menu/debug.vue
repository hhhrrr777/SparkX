<template>
	<div class="customer-chat-box">
		<div class="header">
			<div class="title-box">
				<div class="title-label">测</div>
				<div class="title">
					测试编排小任务
				</div>
			</div>
			<el-icon style="cursor: pointer" size="18">
				<Close />
			</el-icon>
		</div>
	</div>
</template>

<script>
import {Close} from "@element-plus/icons-vue";

	export default {
		components: {Close},
		data() {
			return {

			}
		},
		mounted() {
		},
		methods: {

		}
	}
</script>

<style scoped>
.customer-chat-box {
	z-index: 1999;
	border-radius: 8px;
	border: 1px solid #ffffff;
	background:  linear-gradient(188deg, rgba(235, 241, 255, .2) 39.6%, rgba(231, 249, 255, .2) 94.3%), #eff0f1;
	box-shadow: 0 4px 8px #1f23291a;
	position: fixed;
	bottom: 16px;
	right: 16px;
	overflow: hidden;
	width: 450px;
	height: 600px;
}
.header {
	width: 100%;
	height: 56px;
	background: #fff;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 16px;
}
.title-box {
	display: flex;
	align-items: center;
}
.header .title {
	font-size: 14px;
	margin-left: 10px;
}
.title-label {
	background: var(--el-color-theme);
	color: #fff;
	border-radius: 8px;
	height: 35px;
	width: 35px;
	line-height: 35px;
	text-align: center;
	font-weight: bold;
}
</style>
