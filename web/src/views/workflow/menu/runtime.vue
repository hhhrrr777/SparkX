<template>
	<el-scrollbar class="runtime-list">
		<el-card shadow="never" class="runtime-item" v-for="index in 3" :key="index">
			<div class="runtime-title" @click="showDetail(index)">
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
			<el-collapse-transition>
				<div class="runtime-content-body" v-show="currentIndex === index">
					<component :is="runtimeComponent(`agent-node-runtime`)"/>
				</div>
			</el-collapse-transition>
		</el-card>
	</el-scrollbar>
</template>

<script>
import {CaretRight, CircleCheck} from '@element-plus/icons-vue'
import {iconComponent} from "@/views/workflow/icons/index.js"
import {runtimeComponent} from "@/views/workflow/runtime/index.js"

export default {
	components: {
		CircleCheck,
		CaretRight
	},
	data() {
		return {
			currentIndex: 0
		}
	},
	mounted() {

	},
	methods: {
		iconComponent,
		runtimeComponent,
		// 展示详情
		showDetail(index) {
			if (this.currentIndex === index) {
				this.currentIndex = -1
			} else {
				this.currentIndex = index
			}
		}
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
.runtime-content-body {
	font-size: 14px;
	margin-top: 10px;
}
</style>
