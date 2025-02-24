<template>
	<el-container style="padding: 20px">
		<el-card shadow="never">
			<el-row class="detail-box">
				<el-col :span="2" class="box-height-left">
					<div class="menu-bar">
						<el-icon style="font-size: 18px" @click="goBack">
							<component :is="backIcon"/>
						</el-icon>
						<el-popover placement="bottom" :width="250" trigger="click">
							<template #reference>
								<div style="display: flex">
									<div class="dataset-title line1">
										<el-icon style="font-size: 18px;margin-right: 5px">
											<component :is="datasetIcon"/>
										</el-icon>这是一个测文档
									</div>
									<el-icon style="font-size: 18px">
										<component :is="downIcon"/>
									</el-icon>
								</div>
							</template>
							<div class="dataset-list">
								<div class="dataset-item">
									<el-icon style="font-size: 18px;margin-right: 5px">
										<component :is="datasetIcon"/>
									</el-icon>这是一个测文档
								</div>
								<div class="dataset-item">
									<el-icon style="font-size: 18px;margin-right: 5px">
										<component :is="datasetIcon"/>
									</el-icon>这是一个测文档
								</div>
								<div class="dataset-item" style="border-top: 1px solid #e2e2e2;">
									<el-icon style="font-size: 18px;margin-right: 5px">
										<component :is="plusIcon"/>
									</el-icon> 创建知识库
								</div>
							</div>
						</el-popover>
					</div>
					<el-menu
						style="margin-top: 20px"
						default-active="1">
						<el-menu-item index="1">
							<el-icon>
								<component :is="documentIcon"/>
							</el-icon>
							<span>文档管理</span>
						</el-menu-item>
						<el-menu-item index="2">
							<el-icon>
								<component :is="questionIcon"/>
							</el-icon>
							<span>问题管理</span>
						</el-menu-item>
						<el-menu-item index="3">
							<span class="iconfont icon-mingzhong" style="font-size: 18px;margin-right: 10px"></span>
							<span>命中测试</span>
						</el-menu-item>
						<el-menu-item index="4">
							<el-icon>
								<component :is="settingIcon"/>
							</el-icon>
							<span>编辑知识库</span>
						</el-menu-item>
					</el-menu>
				</el-col>
				<el-col :span="22" class="box-height-right">
					<div class="pages">
						<Suspense>
							<template #default>
								<component :is="page"/>
							</template>
							<template #fallback>
								<el-skeleton :rows="3" />
							</template>
						</Suspense>
					</div>
				</el-col>
			</el-row>
		</el-card>
	</el-container>
</template>

<script>
import {defineAsyncComponent} from "vue";

export default {
	data() {
		return {
			documentIcon: 'el-icon-Document',
			settingIcon: 'el-icon-Setting',
			questionIcon: 'el-icon-QuestionFilled',
			backIcon: 'el-icon-Back',
			downIcon: 'el-icon-CaretBottom',
			datasetIcon: 'el-icon-Collection',
			plusIcon: 'el-icon-Plus',
			components: {
				users: defineAsyncComponent(() => import('./pages/users.vue')),
			},
			page: ''
		}
	},
	mounted() {
		this.page = this.components.users
	},
	methods: {
		goBack() {
			this.$router.go(-1)
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
	border-radius: 10px;
	padding: 20px;
}
.menu-bar {
	height: 40px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding-right: 10px;
	cursor: pointer;
}
.dataset-title {
	font-weight: bold;
	width: 130px;
	display: flex;
	align-items: center;
	font-size: 14px;
}
.dataset-item {
	display: flex;
	align-items: center;
	cursor: pointer;
	padding: 10px;
}
.dataset-item:hover {
	background: #eee7fd;
	color: #5E17EB;
}
</style>
