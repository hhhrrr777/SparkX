<template>
	<div class="dataset-list">
		<el-radio-group v-model="appId" class="too-radio-list">
			<el-radio :label="item.appId" border class="radio-item" v-for="item in appList" :key="item.appId">
				<div style="display: flex;align-items: center;">
					<img :src="domain + item.icon" style="width: 45px;height: 40px;" />
					<div class="line1 name">{{ item.name }}</div>
				</div>
			</el-radio>
		</el-radio-group>
	</div>
	<div class="dialog-footer">
		<el-button @click="$emit('doClose')">取 消</el-button>
		<el-button type="primary" @click="optSubmit()" :loading="loading">确认选择</el-button>
	</div>
</template>

<script>
import config from "@/config"

export default {
	data() {
		return {
			appId: "",
			appList: [],
			loading: false,
			domain: config.API_URL.replace("/api", ""),
		}
	},
	mounted() {
		this.getAppList();
	},
	methods: {
		// 获取应用列表
		async getAppList() {
			let res = await this.$API.application.list.get({page: 1, limit: 1000, name: ''})
			this.appList = res.data.data
		},
		// 保存
		optSubmit() {
			this.$emit('success', [{datasetId: this.datasetId}])
		}
	}
}
</script>

<style scoped>
.dataset-list {
	width: 100%;
	height: 100%;
	display: flex;
	flex-wrap: wrap;
	padding: 10px;
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
	margin-top: 10px;
}
</style>
