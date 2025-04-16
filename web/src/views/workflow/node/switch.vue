<template>
	<div class="node-base" :class="{'node-active': active}">
		<div class="flex-center">
			<div class="menu-icon" style="background: #6172f3;color: #fff;padding: 3px;border-radius: 5px;">
				<span class="iconfont icon-fenzhi" style="font-size: 18px !important;"></span>
			</div>
			<span class="node-name" v-if="no === 1">{{ name }}</span>
			<span class="node-name" v-else>{{ name }}{{ no - 1 }}</span>
		</div>

		<div class="tips-text" style="flex-direction: column;display: flex">
			<div>IF</div>
			<div class="flex-center tips-item">
				输入变量 大于 2
			</div>
			<div class="flex-center tips-item">
				输入变量 大于 2
			</div>
		</div>

		<div class="tips-text" style="flex-direction: column;display: flex">
			<div>ELSEIF</div>
			<div class="flex-center tips-item">
				输入变量 大于 2
			</div>
			<div class="flex-center tips-item">
				输入变量 大于 2
			</div>
		</div>
	</div>
</template>

<script>
import initConfig from '@/views/workflow/initConfig.js';

export default {
	inject: ["getGraph", "getNode"],
	data() {
		return {
			no: 0,
			name: "条件分支",
			nodeInnerData: {},
			active: false,
			optionsMap: new Map()
		}
	},
	created() {
		this.no = this.getNode().store.data.data.no
		this.nodeInnerData = JSON.parse(JSON.stringify(initConfig.switchData))

		let options = JSON.parse(JSON.stringify(initConfig.switchOptions))
		options.forEach(item => {
			this.optionsMap.set(item.type, item.label)
		})
	},
	mounted() {
		const node = this.getNode();
		// 监听数据
		node.on('change:data', ({ current }) => {
			this.active = current.checked
			this.nodeInnerData = current
		})
	},
	methods: {

	}
}
</script>

<style scoped>
.tips-text {
	width: 100%;
	background: #f4f4f4;
	padding: 5px 10px;
	border-radius: 5px;
	margin-top: 10px;
}
.tips-item {
	background: #fff;
	padding: 5px 10px;
	border-radius: 5px;
	font-size: 12px;
	margin-top: 5px;
}
</style>
