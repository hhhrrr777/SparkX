<template>
	<el-container style="padding: 20px">
		<el-card shadow="never">
			<el-row class="detail-box">
				<el-col :span="3" class="box-height-left">
					<div class="menu-bar">
						<el-icon size="18" @click="goBack">
							<Back />
						</el-icon>
						<el-popover placement="bottom" :width="250" trigger="click">
							<template #reference>
								<div style="display: flex;width: calc(100% - 20px)">
									<div class="dataset-title line1">
										<el-icon style="font-size: 18px;margin-right: 5px;margin-top: 5px;top:4px;">
											<Collection />
										</el-icon>这是一个测文档
									</div>
									<el-icon style="font-size: 18px">
										<CaretBottom />
									</el-icon>
								</div>
							</template>
							<div class="dataset-list">
								<div class="dataset-item">
									<el-icon style="font-size: 18px;margin-right: 5px">
										<Collection />
									</el-icon>这是一个测文档
								</div>
								<div class="dataset-item">
									<el-icon style="font-size: 18px;margin-right: 5px">
										<Collection />
									</el-icon>这是一个测文档
								</div>
								<div class="dataset-item" style="border-top: 1px solid #e2e2e2;">
									<el-icon style="font-size: 18px;margin-right: 5px">
										<Collection />
									</el-icon> 创建知识库
								</div>
							</div>
						</el-popover>
					</div>
					<el-menu
						style="margin-top: 20px"
						default-active="1">
						<el-menu-item index="1" @click="handleClick(1)">
							<el-icon>
								<Document />
							</el-icon>
							<span>文档管理</span>
						</el-menu-item>
						<el-menu-item index="2" @click="handleClick(2)">
							<el-icon>
								<QuestionFilled />
							</el-icon>
							<span>问题管理</span>
						</el-menu-item>
						<el-menu-item index="3" @click="handleClick(3)">
							<span class="iconfont icon-mingzhong" style="font-size: 18px;margin-right: 10px"></span>
							<span>命中测试</span>
						</el-menu-item>
						<el-menu-item index="4" @click="handleClick(4)">
							<el-icon>
								<Setting />
							</el-icon>
							<span>编辑知识库</span>
						</el-menu-item>
					</el-menu>
				</el-col>
				<el-col :span="21" class="box-height-right">
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
import {Back, CaretBottom, Collection, Document, QuestionFilled, Setting} from "@element-plus/icons-vue";

export default {
	components: {CaretBottom, Collection, Back, Document, QuestionFilled, Setting},
	data() {
		return {
			components: {
				document: defineAsyncComponent(() => import('./pages/document.vue')),
				question: defineAsyncComponent(() => import('./pages/question.vue')),
			},
			page: ''
		}
	},
	mounted() {
		this.page = this.components.document
	},
	methods: {
		goBack() {
			this.$router.push("/dataset/index")
		},
		// 菜单选择
		handleClick(index) {
			if (index === 1) {
				this.page = this.components.document
			} else if (index === 2) {
				this.page = this.components.question
			}
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
	padding-right: 10px;
	cursor: pointer;
}
.dataset-title {
	font-weight: bold;
	width: 100%;
	font-size: 14px;
	margin-top: -6px;
	margin-left: 10px;
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
