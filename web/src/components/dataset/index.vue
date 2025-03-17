<template>
	<div class="dataset-list">
		<el-radio-group v-model="datasetId" class="too-radio-list">
			<el-radio :label="1" border class="radio-item" v-for="i in 10" :key="i">
				<div style="display: flex;align-items: center;">
					<el-icon size="26" color="#5E17EB"><Management /></el-icon>
					<div class="line1 name">一个测试的数据库</div>
				</div>
			</el-radio>
		</el-radio-group>
	</div>
</template>

<script>
import {Management} from "@element-plus/icons-vue";

export default {
	components: {Management},
	props: {
		diffDatasetId: {
			type: String,
			default: ''
		}
	},
	data() {
		return {
			datasetId: ""
		}
	},
	mounted() {

	},
	methods: {

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
	width: 28%;
	font-size: 13px;
	height: 60px;
	background: #fff;
}
.name {
	width: 140px;
	font-size: 13px;
	margin-left: 5px;
}
</style>
