<template>
	<div class="tools-list" v-if="toolsList.length > 0">
		<el-checkbox-group v-model="selectedToolsIds" class="too-radio-list">
			<el-checkbox :label="item.id" border class="radio-item" v-for="item in toolsList" :key="item.id">
				<div style="display: flex;align-items: center;">
					<el-icon size="26" color="var(--el-color-theme)"><ElementPlus /></el-icon>
					<div class="line1 name">{{ item.title }}</div>
				</div>
			</el-checkbox>
		</el-checkbox-group>
	</div>
	<div v-else>
		<div class="flex-center-all" style="padding: 20px 10px;background: #f4f4f4">
			暂无插件
			<el-button @click="goTo" type="text" style="margin-top: 3px;margin-left: 10px">创建</el-button>
		</div>
	</div>
	<div class="dialog-footer">
		<el-button @click="$emit('doClose')">取 消</el-button>
		<el-button type="primary" @click="optSubmit()">确认选择</el-button>
	</div>
</template>

<script>
import {ElementPlus} from "@element-plus/icons-vue";

export default {
	props: {
		toolsList: {
			type: Array,
			default: []
		},
		toolIds: {
			type: Array,
			default: []
		}
	},
	components: {ElementPlus},
	data() {
		return {
			selectedToolsIds: []
		}
	},
	mounted() {
		this.selectedToolsIds = this.toolIds
	},
	methods: {
		goTo() {
			this.$router.push({
				path: '/tools/index'
			})
		},
		// 选择插件
		optSubmit() {
			let selectedTools = []

			this.toolsList.forEach(item => {
				if (this.selectedToolsIds.indexOf(item.id) !== -1) {
					selectedTools.push(item)
				}
			})
			console.log(242, selectedTools)
			this.$emit('success', selectedTools)
		}
	}
}
</script>

<style scoped>
.tools-list {
	width: 100%;
	height: 100%;
	display: flex;
	flex-wrap: wrap;
	padding: 0 10px;
	justify-content: space-between;
}
.radio-item {
	margin-top: 20px;
	width: 207px;
	font-size: 13px;
	height: 60px;
	background: #fff;
}
.name {
	width: 140px;
	font-size: 13px;
	margin-left: 5px;
}
.dialog-footer {
	text-align: center;
	margin-top: 20px;
}
.notice {
	font-size: 13px;
	position: relative;
	left: 10px;
	color: #E6A23C;
}
</style>
