<!-- branch.vue -->
<template>
	<div class="node-base" :class="{'node-active': active}">
		<div class="flex-center">
			<div class="menu-icon" style="background: var(--el-color-theme);color: #fff;padding: 3px;border-radius: 5px;">
				<span class="iconfont icon-ai23" style="font-size: 18px !important;"></span>
			</div>
			<span class="node-name">{{ name }}</span>
		</div>
	</div>
</template>

<script>
import {HomeFilled} from "@element-plus/icons-vue";

export default {
	components: {HomeFilled},
	inject: ["getNode"],
	data() {
		return {
			name: '开始',
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

</style>
