<template>
	<div class="opt-form">
		<div class="flex-center title" style="justify-content: space-between">
			<div class="flex-center">
				<div class="menu-icon" style="background: #17b26a;color: #fff;padding: 3px;border-radius: 5px;">
					<span class="iconfont icon-a-agent1" style="font-size: 18px !important;"></span>
				</div>
				<span class="node-name">Agent</span>
			</div>
			<el-dropdown>
				<el-icon size="18"><MoreFilled /></el-icon>
				<template #dropdown>
					<el-dropdown-menu>
						<el-dropdown-item style="font-size: 12px">删除节点</el-dropdown-item>
					</el-dropdown-menu>
				</template>
			</el-dropdown>
		</div>

		<div class="set-content-box">
			<div>输出参数</div>
			<div class="param-data">
				<div class="flex-center data-item" v-for="(item, index) in form.outData" :key="index">
					<div class="flex-center">
						<div class="menu-icon" style="background: #6172f3;color: #fff;padding: 3px;border-radius: 5px;">
							<span class="iconfont icon-bianliang" style="font-size: 16px !important;"></span>
						</div>
						<div class="title" style="margin-left: 10px">{{ item.field }}</div>
					</div>
					<div class="field">{{ item.name }}</div>
				</div>
			</div>
		</div>
	</div>

</template>

<script>

import {MoreFilled} from "@element-plus/icons-vue";

export default {
	components: {MoreFilled},
	props: {
		formData: {
			type: Object,
			default: () => {}
		},
		inputOptions: {
			type: Array,
			default: []
		}
	},
	data() {
		return {
			dialogVisible: false,
			form: {},
			inputData: [], // 入参
		}
	},
	created() {
		this.form = this.formData
		this.inputData = this.formData.inputData
	},
	methods: {
		// 输入选择
		inputChange(val) {
			this.form.inputData = val
			this.$emit("dataChange", this.form)
		}
	}
}
</script>

<style scoped>
.opt-form {
	width: 100%;
	height: calc(100vh - 200px);
	background: #f4f4f4;
	border-radius: 5px;
	padding: 20px;
}
.node-name {
	margin-left: 10px;
	font-weight: bold;
}
.param-data {
	width: 100%;
	border-radius: 5px;
	margin-top: 10px;
	display: flex;
	flex-direction: column;
}
.data-item {
	width: 100%;
	padding: 10px;
	height: 40px;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 10px;
	background: #f4f4f4;
	border-radius: 5px;
}
.no-param {
	width: 100%;
	height: 30px;
	background: #f4f4f4;
	border-radius: 5px;
	color: #98A2B2;
	cursor: pointer;
}
</style>
