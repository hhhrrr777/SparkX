<!-- branch.vue -->
<template>
	<div class="node-base" :class="{'node-active': active}">
		<div class="flex-center">
			<div class="menu-icon" style="background: #06ae4d;color: #fff;padding: 3px;border-radius: 5px;">
				<span class="iconfont icon-pinglun3-copy" style="font-size: 18px !important;"></span>
			</div>
			<span class="node-name" v-if="no === 1">{{ name }}</span>
			<span class="node-name" v-else>{{ name }}{{ no - 1 }}</span>
		</div>
	</div>
</template>

<script>

export default {
	inject: ["getNode"],
	data() {
		return {
			no: 0,
			name: '回复',
			active: false,
		}
	},
	created() {
		this.no = this.getNode().store.data.data.no
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
.tips-text {
	width: 100%;
	height: 30px;
	background: #f4f4f4;
	padding: 5px 10px;
	border-radius: 5px;
	margin-top: 10px;
}
</style>
