<!-- branch.vue -->
<template>
	<div class="node-base" :class="{'node-active': active}">
		<div class="flex-center">
			<div class="menu-icon" style="background: #f79009;color: #fff;padding: 3px;border-radius: 5px;">
				<span class="iconfont icon-qizhi" style="font-size: 16px !important;"></span>
			</div>
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

</style>
