<template>
	<el-container style="padding: 20px">
		<el-card shadow="never">
			<el-row class="detail-box">
				<el-col :span="4" class="box-height-left">

				</el-col>
				<el-col :span="20" class="box-height-right">

				</el-col>
			</el-row>
		</el-card>
	</el-container>
</template>

<script>
export default {
	data() {
		return {

		}
	}
}
</script>

<style scoped>
.detail-box {
	width: 100%;
	height: calc(100vh - 100px);
}
.box-height-left {
	height: 100%;
}
.box-height-right {
	height: 100%;
	background: #f4f4f4;
}
</style>
