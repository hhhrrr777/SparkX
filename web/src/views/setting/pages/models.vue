<template>
	<div style="background: #fff;border-radius: 10px;padding: 10px">
		<el-tabs v-model="activeName" @tab-click="handleClick">
			<el-tab-pane label="语言模型" name="first">
				<div class="flex-center model-list">
					<div class="flex-center model-item" v-for="i in 4" :key="i">
						<img src="https://ai-demo.chatmoney.cn/uploads/images/20250217/20250217164409883653511.jpeg" style="width:44px;height:44px"/>
						<div class="info">
							<span class="line1 model-name">ChatGLM2-6B</span>
							<div class="status flex-center">
								<span class="success"></span> <span class="text">已启用</span>
							</div>
						</div>
						<el-button type="primary" plain>编辑</el-button>
					</div>
				</div>
			</el-tab-pane>
			<el-tab-pane label="向量模型" name="second">
				<div class="flex-center model-list">
					<div class="flex-center model-item" v-for="i in 4" :key="i">
						<img src="https://ai-demo.chatmoney.cn/uploads/images/20250217/20250217164409883653511.jpeg" style="width:44px;height:44px"/>
						<div class="info">
							<span class="line1 model-name">ChatGLM2-6B</span>
							<div class="status flex-center">
								<span class="success"></span> <span class="text">已启用</span>
							</div>
						</div>
						<el-button type="primary" plain>编辑</el-button>
					</div>
				</div>
			</el-tab-pane>
			<el-tab-pane label="重排模型" name="third">
				<div class="flex-center model-list">
					<div class="flex-center model-item" v-for="i in 4" :key="i">
						<img src="https://ai-demo.chatmoney.cn/uploads/images/20250217/20250217164409883653511.jpeg" style="width:44px;height:44px"/>
						<div class="info">
							<span class="line1 model-name">ChatGLM2-6B</span>
							<div class="status flex-center">
								<span class="success"></span> <span class="text">已启用</span>
							</div>
						</div>
						<el-button type="primary" plain>编辑</el-button>
					</div>
				</div>
			</el-tab-pane>
		</el-tabs>
	</div>
</template>

<script>
export default {
	data() {
		return {
			activeName: 'first'
		}
	},
	mounted() {

	},
	methods: {
		// tab切换
		handleClick(row) {
			console.log('xx', row)
		}
	}
}
</script>

<style scoped>
.model-list {
	width: 100%;
	flex-wrap: wrap;
}
.model-item {
	width: 300px;
	height: 100px;
	background: #f6f6f6;
	border-radius: 10px;
	margin: 10px;
	margin-right: 10px;
	padding: 0 10px;
}
.info {
	width: 160px;
	height: 50px;
	padding-left: 10px;
}
.model-name {
	font-size: 14px;
	font-weight: 700;
	width: 100%;
}
.status {
	margin-top: 10px;
}
.success {
	width: 8px;
	height: 8px;
	border-radius: 50%;
	background: #67C23A;
}
.status .text {
	margin-left: 10px;
	color: #999;
}
</style>
