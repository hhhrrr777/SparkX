<template>
	<div class="flex-center menu-box">
		<el-tooltip
			class="box-item"
			effect="dark"
			content="缩小"
			placement="top-start"
		>
			<el-icon size="16"><ZoomOut /></el-icon>
		</el-tooltip>
		<el-tooltip
			class="box-item"
			effect="dark"
			content="放大"
			placement="top-start"
		>
			<el-icon size="16"><ZoomIn /></el-icon>
		</el-tooltip>
		<el-tooltip
			class="box-item"
			effect="dark"
			content="添加节点"
			placement="top-start"
		>
			<el-icon size="16"><CirclePlus /></el-icon>
		</el-tooltip>
		<el-tooltip
			class="box-item"
			effect="dark"
			content="整理布局"
			placement="top-start"
		>
			<el-icon size="16"><SetUp /></el-icon>
		</el-tooltip>
	</div>
</template>

<script>
import {CirclePlus, SetUp, ZoomIn, ZoomOut} from "@element-plus/icons-vue";

export default {
	components: {ZoomOut, ZoomIn, SetUp, CirclePlus},
	data() {
		return {

		}
	},
	mounted() {

	},
	methods: {

	}
}
</script>

<style scoped>
.menu-box {
	width: 200px;
	height: 40px;
	border-radius: 10px;
	background: #fff;
	box-shadow: 0 0 #0000, 0 0 #0000, 0 1px 2px 0 rgba(16, 24, 40, 0.05);
	justify-content: space-between;
	padding: 0 20px;
}

</style>
