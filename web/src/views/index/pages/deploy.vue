<template>
	<div style="background: #fff;border-radius: 10px;padding: 10px 10px">
		<div class="application-info">
			<div class="application-title">应用信息</div>

		</div>
		<div class="census-list"></div>
	</div>
</template>

<script>
export default {
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

<style lang="scss" scoped>
.application-info {
	width: 100%;
	height: 60px;
	.application-title {
		font-size: 16px;
		padding-left: 10px;
		border-left: 5px solid var(--el-color-theme);
		font-weight: bold;
	}
}
</style>
