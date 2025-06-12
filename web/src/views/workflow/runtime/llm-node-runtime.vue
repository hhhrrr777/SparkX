<template>
	<div class="detail-box">
		<div class="detail-title">系统角色</div>
		<div class="detail-content">
			<div class="content-item">
				欢迎还用
			</div>
		</div>
	</div>

	<div class="detail-box" style="margin-top: 10px">
		<div class="detail-title">历史记录</div>
		<div class="detail-content">
			<div class="content-item">
				<div class="item-left">human：</div>
				<div class="item-right">你的名字</div>
			</div>
			<div class="content-item">
				<div class="item-left">AI：</div>
				<div class="item-right">你的名字</div>
			</div>
		</div>
	</div>

	<div class="detail-box" style="margin-top: 10px">
		<div class="detail-title">本次对话</div>
		<div class="detail-content">
			<div class="content-item">
				欢迎还用
			</div>
		</div>
	</div>

	<div class="detail-box" style="margin-top: 10px">
		<div class="detail-title">AI回答</div>
		<div class="detail-content">
			<div class="content-item">
				欢迎还用
			</div>
		</div>
	</div>
</template>

<script>
export default {
	props: {
		runtimeData: {
			type: Object,
			default: () => ({})
		}
	},
	data() {
		return {
			show: false
		}
	}
}
</script>

<style scoped>
.detail-box {
	background: #f5f6f7;
	border-radius: 4px;
	height: 100%;
	width: 100%;
}
.detail-box .detail-title {
	border-bottom: 1px dashed #dee0e3;
	padding: 8px 12px;
	font-weight: bold;
}
.detail-box .detail-content {
	padding: 8px 12px;
}
.content-item {
	display: flex;
	align-items: center;
	margin-top: 5px;
}
.content-item .item-left {
	color: #646a73;
}
</style>
