<!-- branch.vue -->
<template>
	<div class="start-node" :class="{'node-active': active}">
		<div class="flex-center">
			<el-icon color="#F56C6C" size="22"><SuccessFilled /></el-icon>
			<span class="node-name">{{ name }}</span>
		</div>
	</div>
</template>

<script>
import {SuccessFilled} from "@element-plus/icons-vue";

export default {
	components: {SuccessFilled},
	inject: ["getNode"],
	data() {
		return {
			name: '结束',
			active: false,
		}
	},
	mounted() {
		const node = this.getNode();
		// 监听数据
		node.on('change:data', ({ current }) => {
			this.active = current.checked
		})
	}
}
</script>

<style scoped>
.start-node {
	display: flex;
	flex-direction: column;
	width: 190px;
	min-height: 40px;
	padding: 10px;
	border: 1px solid #fff;
	border-radius: 16px;
	box-shadow: 0 0 #0000, 0 0 #0000, 0 1px 2px 0 rgba(16, 24, 40, 0.05);
	background: #fff;
}
.node-name {
	margin-left: 10px;
	font-weight: bold;
}
.node-active {
	border: 1px solid var(--el-color-theme);
}
</style>
