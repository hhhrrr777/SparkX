<template>
	<div class="opt-form">
		<div class="flex-center title" style="justify-content: space-between">
			<div class="flex-center">
				<div class="menu-icon" style="background: #6172f3;color: #fff;padding: 3px;border-radius: 5px;">
					<span class="iconfont icon-fenzhi" style="font-size: 18px !important;"></span>
				</div>
				<span class="node-name">条件分支</span>
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
			<div>条件分支</div>
			<div class="flex-center item-box" v-for="(item2, index) in form.ifBranch" :key="index">
				<div style="width: 60px" v-if="index === 0">IF</div>
				<div style="width: 60px" v-else>ELSEIF</div>
				<div class="flex-center" style="flex-direction: column">
					<div v-for="(item3, index2) in item2.data" :key="index2" style="margin-top: 10px">
						<div class="tips-data flex-center">
							<el-cascader
								v-model="item3.input"
								:options="inputOptions"
								@change="inputChange"
								style="width: 150px"
								clearable>
								<template #default="{ node, data }">
									<div class="flex-center">
										<span :class="data.icon" style="font-size: 18px !important;" :style="{color: data.color}"></span>
										<span style="margin-left: 5px">{{ data.label }}</span>
									</div>
								</template>
							</el-cascader>
							<el-select
								v-model="item3.tips"
								placeholder="请选择"
								style="width: 100px;margin-left: 5px;"
							>
								<el-option
									v-for="item in options"
									:key="item.value"
									:label="item.label"
									:value="item.value"
								/>
							</el-select>
							<el-input v-model="item3.value" style="width: 100px;margin-left: 5px" placeholder="" />
							<div style="width: 40px;" v-if="index === 0 && index2 === 0"></div>
							<el-icon style="width: 40px;cursor: pointer;color: #F56C6C" v-else @click="delBranch(index, index2)">
								<Delete />
							</el-icon>
						</div>
					</div>
					<div class="flex-center" style="margin-top: 10px;cursor: pointer" @click="addTips(index)">
						<el-icon style="margin-right: 5px;">
							<Plus />
						</el-icon> 添加条件
					</div>
				</div>

			</div>

			<div class="flex-center-all item-box" style="cursor: pointer" @click="addBranch">
				<el-icon style="margin-right: 5px;margin-top: 3px">
					<Plus />
				</el-icon> 添加 ELSEIF
			</div>

			<div class="flex-center item-box">
				<div style="width: 60px">ELSE</div>
				<el-cascader
					v-model="form.elseBranch.input"
					:options="inputOptions"
					@change="inputChange"
					style="margin-left: 20px;width: 150px"
					clearable>
					<template #default="{ node, data }">
						<div class="flex-center">
							<span :class="data.icon" style="font-size: 18px !important;" :style="{color: data.color}"></span>
							<span style="margin-left: 5px">{{ data.label }}</span>
						</div>
					</template>
				</el-cascader>
				<el-select
					v-model="form.elseBranch.tips"
					placeholder="请选择"
					style="width: 100px;margin-left: 5px;"
				>
					<el-option
						v-for="item in options"
						:key="item.value"
						:label="item.label"
						:value="item.value"
					/>
				</el-select>
				<el-input v-model="form.elseBranch.value" style="width: 100px;margin-left: 5px" placeholder="" />
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
	</div>

</template>

<script>
import {Delete, MoreFilled, Plus} from "@element-plus/icons-vue";

export default {
	components: {MoreFilled, Delete, Plus},
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
			value: "",
			input: "",
			options: [
				{label: '等于', value: '='}
			],
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
		},
		// 添加分支
		addBranch() {
			this.form.ifBranch.push({type: 'elseif', data: [{input: "", tips: "", value: ""}]})
		},
		// 删除分支
		delBranch(index, index2) {
			this.form.ifBranch[index].data.splice(index2, 1)
			if (this.form.ifBranch[index].data.length === 0) {
				this.form.ifBranch.splice(index, 1)
			}
		},
		// 添加条件
		addTips(index) {
			this.form.ifBranch[index].data.push({input: "", tips: "", value: ""})
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
.item-box {
	margin-top: 10px;
	background: #f4f4f4;
	padding: 10px;
	border-radius: 5px;
}
</style>
