<template>
	<el-scrollbar class="runtime-list">
		<el-card shadow="never" class="runtime-item">
			<div class="runtime-title">
				<div class="runtime-icon">
					<el-icon>
						<CaretRight />
					</el-icon>
					<component :is="iconComponent(`agent-node-icon`)"/>
				</div>
				<div class="runtime-status">
					<div class="run-time">0.01 s</div>
					<el-icon class="success" :size="16">
						<CircleCheck />
					</el-icon>
				</div>
			</div>
			<div class="runtime-content-body">

			</div>
		</el-card>
	</el-scrollbar>
</template>

<script>
import {CaretRight, CircleCheck} from '@element-plus/icons-vue'
import {iconComponent} from "@/views/workflow/icons/index.js"

export default {
	components: {
		CircleCheck,
		CaretRight
	},
	data() {
		return {

		}
	},
	mounted() {

	},
	methods: {
		iconComponent
	}
}
</script>
<style>
.runtime-item .el-card__body {
	padding: 12px 16px;
}
</style>
<style scoped>
.runtime-list {
	display: flex;
	height: 300px;
	flex-direction: column;
	width: 100%;
}
.runtime-item {
	margin-top: 10px;
	cursor: pointer;
}
.runtime-title {
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.runtime-icon {
	display: flex;
	align-items: center;
}
.runtime-status {
	display: flex;
	align-items: center;
}
.runtime-status .run-time {
	color: #646a73;
	margin-right: 10px;
}
.success {
	color: #17b26a;
}
</style>
