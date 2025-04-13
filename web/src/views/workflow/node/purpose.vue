<template>
	<div class="node-base">
		<div class="flex-center">
			<div class="menu-icon" style="background: #f79009;color: #fff;padding: 3px;border-radius: 5px;">
				<span class="iconfont icon-fenlei" style="font-size: 18px !important;"></span>
			</div>
			<span class="node-name">{{ name }}</span>
		</div>

		<div class="flex-center tips-text">
			<div class="menu-icon" style="background: #6172f3;color: #fff;padding: 2px;border-radius: 5px;">
				<span class="iconfont icon-a-zhuliudeLLM"></span>
			</div>
			<span style="font-size: 13px;margin-left: 5px" class="line1" v-if="nodeInnerData.modeInfo.modeName !== ''">{{ nodeInnerData.modeInfo.modeName }}</span>
			<span style="font-size: 13px;margin-left: 5px" class="line1" v-else>请设置语言模型</span>
		</div>

		<div class="flex-center tips-text" v-for="(item, index) in nodeInnerData.cateList" :key="index">{{ item.name }}</div>
	</div>
</template>

<script>
import initConfig from '@/views/workflow/initConfig.js';

export default {
	props: {
		nodeData: {
			type: Object,
			default: () => {}
		}
	},
	data() {
		return {
			name: "意图分类",
			nodeInnerData: {}
		}
	},
	created() {
		if (this.nodeData) {
			this.nodeInnerData = this.nodeData
		} else {
			this.nodeInnerData = initConfig.purposeData
		}
	},
	methods: {

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
