<template>
	<div class="detail-box">
		<div class="detail-title">本次对话</div>
		<div class="detail-content">
			<div class="content-item">
				{{ runtimeData.outputData['agent.input'] }}
			</div>
		</div>
	</div>
	<div class="detail-box" style="margin-top: 10px">
		<div class="detail-title">Agent回复</div>
		<div class="detail-content">
			<div class="content-item">
				{{ runtimeData.outputData['sys.agentContent'] }}
			</div>
		</div>
	</div>
	<div class="detail-box" style="margin-top: 10px">
		<div class="detail-title">历史聊天记录</div>
		<div class="detail-content">
			<div class="content-item">
				{{ runtimeData.outputData['agent.input'] }}
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
	},
	mounted() {
		if (this.runtimeData.outputData['log.context'] !== '') {
			let context = decodeURIComponent(this.runtimeData.outputData['log.context'])
			if (context !== 'undefined') {
				this.runtimeData.outputData['agent.context'] = JSON.parse(context || '{}');
			}

			console.log(222, this.runtimeData.outputData['agent.context'])
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
