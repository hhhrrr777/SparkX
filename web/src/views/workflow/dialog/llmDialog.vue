<template>
	<div class="opt-form">
		<div class="flex-center title" style="justify-content: space-between">
			<div class="flex-center">
				<component :is="iconComponent(`llm-node-icon`)"/>
			</div>
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

		<div class="set-content-box">
			<div>模型</div>
			<div class="param-data">
				<el-cascader
					v-model="modelId"
					:options="options"
					:show-all-levels="false"
					style="width: 100%"
					@change="handleChange"
					clearable>
				</el-cascader>
			</div>
			<div class="flex-center no-param" style="margin-top: 10px;background: #fff">
				<span style="width: 80px;color: #1a1a1a">温度</span>
				<el-slider
					:step="0.01"
					v-model="form.modelInfo.temperature"
					:min="temperatureConfig.range[0]"
					:max="temperatureConfig.range[1]"
					@change="$emit('dataChange', form)"
					show-input>
				</el-slider>
			</div>
			<div class="flex-center no-param" style="margin-top: 10px;background: #fff">
				<span style="width: 80px;color: #1a1a1a">上下文轮数</span>
				<el-slider
					v-model="form.memory"
					:min="0"
					:max="10"
					@change="$emit('dataChange', form)"
					show-input>
				</el-slider>
			</div>
		</div>

		<div class="set-content-box">
			<div style="justify-content: space-between" class="flex-center">
				<span>角色设置</span>
			</div>
			<el-input v-model="form.systemMsg" type="textarea" placeholder="角色设置" :rows="4" style="margin-top: 10px"/>
		</div>

		<div class="set-content-box">
			<div style="justify-content: space-between;position: relative;" class="flex-center">
				<span>用户提示词</span>
				<el-tooltip content="按 '/' 键快速插入" placement="top" effect="light">
					<span class="iconfont icon-bianliang param-style" @click="paramVisible=true"></span>
				</el-tooltip>

				<div class="input-param-box" v-if="paramVisible" v-click-outside="closeDiv">
					<el-cascader-panel
						v-model="inputData"
						:options="inputOptions"
						@change="inputChange"
						clearable>
						<template #default="{ node, data }">
							<div class="flex-center">
								<span :class="data.icon" style="font-size: 18px !important;" :style="{color: data.color}"></span>
								<span style="margin-left: 5px">{{ data.label }}</span>
							</div>
						</template>
					</el-cascader-panel>
				</div>
			</div>
			<div class="edit-box" contenteditable="true" @keydown="handleKeyDown" @input="handleInput"></div>
		</div>
	</div>
</template>

<script>

import {MoreFilled} from "@element-plus/icons-vue";
import {iconComponent} from "@/views/workflow/icons/index.js";

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
			paramVisible: false,
			form: {},
			temperatureConfig: {
				range: [0, 1]
			},
			modelId: [],
			options: [],
			inputData: [],
			userPrompt: "", // 入参
		}
	},
	watch: {
		'form.systemMsg': {
			handler(val) {
				this.$emit("dataChange", this.form)
			}
		},
		'form.userMsg': {
			handler(val) {
				this.$emit("dataChange", this.form)
			}
		},
	},
	created() {
		this.form = this.formData
		this.modelId = [this.formData.modelInfo.modelId, this.formData.modelInfo.modelName]
		this.userPrompt = this.formData.userPrompt
		this.getModelsList()
	},
	methods: {
		iconComponent,
		// 获取模型信息
		async getModelInfo(modelId) {
			let res = await this.$API.models.info.get({modelId: modelId})
			let setOptions = JSON.parse(res.data.options)
			setOptions.forEach(item => {
				if (item.field === 'temperature') {
					this.temperatureConfig = item
				}
			})
			this.form.modelInfo.temperature = this.temperatureConfig.value

			this.$emit("dataChange", this.form)
		},
		// 获取模型列表
		async getModelsList() {
			let res = await this.$API.models.list.get({type: 1, status: 1})
			this.options = []
			res.data.forEach(item => {

				let info = {
					label: item.name,
					value: item.modelId,
					children: []
				}
				let option = []
				item.models.split(",").forEach(item => {
					option.push({
						label: item,
						value: item
					})
				})
				info.children = option

				this.options.push(info)
			})
		},
		// 选择了模型
		handleChange(val) {
			this.form.modelInfo.modelId = val[0]
			this.form.modelInfo.modelName = val[1]

			this.getModelInfo(val[0])
		},
		// 输入选择
		inputChange(val) {
			console.log(222, val)
		},
		closeDiv() {
			this.paramVisible = false
		},
		// 输入信息
		handleInput(val) {

		},
		// 监听键盘"/" 输入
		handleKeyDown(e) {
			if (e.key === '/') {
				e.preventDefault();
				this.userPrompt += '/'
				const selection = window.getSelection()
				console.log(666, selection)
			}
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
.edit-box {
	border: 2px solid #dcdfe6;
	border-radius: 5px;
	margin-top: 10px;
	min-height: 100px;
	padding: 10px;
}
.param-style {
	font-size: 20px !important;
	cursor: pointer;
	color: var(--el-color-primary);
}
.input-param-box {
	background: #fff;
	position: absolute;
	z-index: 999;
	top: 0;
}
</style>
