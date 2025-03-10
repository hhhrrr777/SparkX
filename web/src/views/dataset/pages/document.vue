<template>
	<div style="background: #fff;border-radius: 10px;padding: 10px 5px">
		<div class="search-box">
			<div>
				<el-button type="primary" icon="el-icon-UploadFilled" @click="uploadFile" style="margin-top: -10px;">上传文档</el-button>
				<el-button type="primary" icon="el-icon-Switch" @click="uploadFile" style="margin-top: -10px;">迁移文档</el-button>
				<el-button type="primary" @click="uploadFile" style="margin-top: -10px;"><span class="iconfont icon-vuesax-linear-convert-3d-cube" style="font-size: 14px;margin-right: 5px"></span>向量文档</el-button>
				<el-button type="primary" icon="el-icon-QuestionFilled" @click="uploadFile" style="margin-top: -10px;">生成问题</el-button>
				<el-button type="primary" icon="el-icon-Setting" @click="uploadFile" style="margin-top: -10px;">设置</el-button>
				<el-button type="primary" icon="el-icon-Delete" @click="uploadFile" style="margin-top: -10px;">删除</el-button>
			</div>

			<el-form :inline="true" :model="searchForm" class="demo-form-inline">
				<el-form-item>
					<el-input v-model="searchForm.name" placeholder="文档名称" clearable></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="onSubmit" icon="el-icon-search">查询</el-button>
				</el-form-item>
			</el-form>
		</div>
		<div style="border-radius: 10px;background: #fff;padding: 0 5px 5px 5px">
			<el-table
				:header-cell-style="{background:'#f4f4f4', color:'#646a73'}"
				:data="tableData"
				@selection-change="handleSelectionChange"
				style="width: 100%">
				<el-table-column
					type="selection"
					width="55">
				</el-table-column>
				<el-table-column
					label="文档名称">
					<template #default="scope">
						<span style="cursor: pointer" @click="showParagraph(scope.row)">{{ scope.row.name }}</span>
					</template>
				</el-table-column>
				<el-table-column
					label="文件大小">
					<template #default="scope">
						<span>{{ $TOOL.formatBytes(scope.row.fileSize) }}</span>
					</template>
				</el-table-column>
				<el-table-column
					prop="paragraphNum"
					label="分段数">
				</el-table-column>
				<el-table-column
					label="向量化">
					<template #default="scope">
						<span v-if="scope.row.status === 1" style="color: #999;cursor: pointer">待生成</span>
						<span v-if="scope.row.status === 2" style="display: flex;align-items: center;color: #409EFF;cursor: pointer">
							<el-icon class="custom-loading-icon">
								<component :is="embeddingIcon" />
							</el-icon>
							向量中
						</span>
						<span v-if="scope.row.status === 3" style="color: #67C23A;cursor: pointer">已完成</span>
					</template>
				</el-table-column>
				<el-table-column
					label="生成问题">
					<template #default="scope">
						<span v-if="scope.row.questionStatus === 1" style="color: #999;cursor: pointer">待生成</span>
						<span v-if="scope.row.questionStatus === 2" style="display: flex;align-items: center;color: #409EFF;cursor: pointer">
							<el-icon class="custom-loading-icon">
								<component :is="embeddingIcon" />
							</el-icon>
							生成中
						</span>
						<span v-if="scope.row.questionStatus === 3" style="color: #67C23A;cursor: pointer">已生成</span>
					</template>
				</el-table-column>
				<el-table-column
					label="状态">
					<template #default="scope">
						<el-tag type="success" v-if="scope.row.active === 1">正常</el-tag>
						<el-tag type="danger" v-else>禁用</el-tag>
					</template>
				</el-table-column>
				<el-table-column
					label="命中处理">
					<template #default="scope">
						<el-tag type="success" v-if="scope.row.hitDealType == 'model'">模型优化</el-tag>
						<el-tag type="danger" v-if="scope.row.hitDealType == 'direct'">直接返回</el-tag>
					</template>
				</el-table-column>
				<el-table-column
					label="创建时间">
					<template #default="scope">
						{{ scope.row.createTime.replace('T', " ") }}
					</template>
				</el-table-column>
				<el-table-column
					label="更新时间">
					<template #default="scope">
						{{ scope.row.updateTime && scope.row.updateTime.replace('T', " ") }}
					</template>
				</el-table-column>
				<el-table-column
					prop="operation"
					width="120"
					label="操作">
					<template #default="scope">
						<div style="display: flex;align-items: center;color: #5E17EB;cursor: pointer">
							<div style="margin-right: 8px;display: flex;align-items: center" @click="embedding(scope.row)">
								<span class="iconfont icon-vuesax-linear-convert-3d-cube" style="font-size: 14px;margin-right: 5px"></span>
							</div>
							<div style="margin-right: 8px;display: flex;align-items: center">
								<el-icon size="14">
									<component :is="settingIcon" />
								</el-icon>
							</div>
							<div style="display: flex;align-items: center;color: #5E17EB">
								<el-dropdown trigger="click">
									<el-icon color="#5E17EB">
										<component :is="menusIcon"></component>
									</el-icon>
									<template #dropdown>
										<el-dropdown-menu>
											<el-dropdown-item>
												<el-icon>
													<component :is="questionIcon"></component>
												</el-icon>
												生成问题
											</el-dropdown-item>
											<el-dropdown-item>
												<el-icon>
													<component :is="switchIcon"></component>
												</el-icon> 迁移</el-dropdown-item>
											<el-dropdown-item>
												<span class="iconfont icon-daochu" style="font-size: 14px;margin-right: 5px"></span> 导出Excel</el-dropdown-item>
											<el-dropdown-item>
												<span class="iconfont icon-daochu" style="font-size: 14px;margin-right: 5px"></span> 导出ZIP</el-dropdown-item>
											<el-dropdown-item>
												<el-icon>
													<component :is="delIcon"></component>
												</el-icon> 删除</el-dropdown-item>
										</el-dropdown-menu>
									</template>
								</el-dropdown>
							</div>
						</div>

					</template>
				</el-table-column>
			</el-table>
		</div>

		<Pages :form="searchForm" :page-obj="page" @pageChange="handlePageChange" @pageJump="getList"></Pages>

		<el-drawer
			size="1000"
			v-model="drawer"
			:title="documentTitle"
			:direction="direction"
		>
			<el-button type="primary" icon="el-icon-plus" @click="uploadFile" style="margin-bottom: 10px;">添加分段</el-button>
			<div class="paragraph-list">
				<div class="paragraph-item" v-for="item in paragraphList" :key="item.uuid">
					<div class="paragraph-title">
						<div class="title-left line1" v-if="item.title.length > 0">{{ item.title }}</div>
						<div class="title-left line1" v-else>--</div>
						<el-switch v-model="item.active" :active-value="1" :inactive-value="2"/>
					</div>
					<div class="paragraph-doc">
						{{ item.content }}
					</div>
					<div class="paragraph-bottom">
						<span>{{ (item.content).length }} 字符</span>
						<el-dropdown trigger="click">
							<el-icon color="#5E17EB">
								<component :is="menusIcon"></component>
							</el-icon>
							<template #dropdown>
								<el-dropdown-menu>
									<el-dropdown-item>
										<el-icon>
											<component :is="questionIcon"></component>
										</el-icon>
										生成问题
									</el-dropdown-item>
									<el-dropdown-item>
										<el-icon>
											<component :is="switchIcon"></component>
										</el-icon> 迁移
									</el-dropdown-item>
									<el-dropdown-item>
										<el-icon>
											<component :is="delIcon"></component>
										</el-icon> 删除</el-dropdown-item>
								</el-dropdown-menu>
							</template>
						</el-dropdown>
					</div>
				</div>

			</div>
			<Pages :form="paragraphForm" :page-obj="paragraphPage" @pageChange="handleParagraphPageChange" @pageJump="getParagraphList"></Pages>
		</el-drawer>
	</div>

</template>

<script>
import Pages from "@/components/pages/index.vue";

export default {
	components: {Pages},
	data() {
		return {
			tableData: [],
			paragraphList: [],
			searchForm: {
				name: '',
				datasetId: '',
				page: 1,
				limit: 10
			},
			paragraphForm: {
				documentId: '',
				page: 1,
				limit: 10
			},
			page: {
				total: 0
			},
			paragraphPage: {
				total: 0
			},
			datasetId: '',
			embeddingIcon: 'el-icon-Loading',
			menusIcon: 'el-icon-MoreFilled',
			settingIcon: 'el-icon-Setting',
			questionIcon: 'el-icon-QuestionFilled',
			delIcon: 'el-icon-Delete',
			switchIcon: 'el-icon-Switch',
			timeInterval: null,
			direction: "rtl",
			drawer: false,
			documentTitle: "",
			active: 1
		}
	},
	mounted() {

		this.datasetId = this.$route.query.datasetId;
		this.searchForm.datasetId = this.datasetId

		this.getList()
	},
	beforeUnmount() {
		clearInterval(this.timeInterval)
	},
	methods: {
		async getList() {
			let res = await this.$API.document.getList.get(this.searchForm)
			this.tableData = res.data.data
			this.page.total = res.data.total
		},
		onSubmit() {
			this.getList()
		},
		handleEdit() {

		},
		handlePageChange(page) {
			this.searchForm.page = page
			this.getList()
		},
		uploadFile() {
			this.$router.push('/dataset/upload?datasetId=' + this.datasetId)
		},
		// 多选
		handleSelectionChange(row) {
			console.log('xxx', row)
		},
		// 向量化文本
		async embedding(row) {
			let res = await this.$API.document.embedding.get({documentId: row.uuid})
			if (res.code === 0) {
				this.getList()
				this.timeInterval = setInterval(() => {
					this.getList()
				}, 2000)
			} else {
				this.$message.error(res.msg)
			}
		},
		// 显示段落
		showParagraph(row) {
			this.documentTitle = row.name
			this.paragraphForm.page = 1
			this.paragraphForm.documentId = row.uuid
			this.drawer = true

			this.getParagraphList()
		},
		// 获取段落列表
		async getParagraphList() {
			let res = await this.$API.paragraph.getList.get(this.paragraphForm)
			this.paragraphList = res.data.data
			this.paragraphPage.total = res.data.total
		},
		// 段落翻页
		handleParagraphPageChange(page) {
			this.paragraphForm.page = page
			this.getParagraphList()
		}
	}
}
</script>

<style>
	.search-box {
		border-radius: 10px;
		margin-bottom: 20px;
		padding-top: 20px;
		padding-left: 20px;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	.custom-loading-icon {
		animation: spin 1s linear infinite;
		margin-right: 5px;
	}

	@keyframes spin {
		from { transform: rotate(0deg); }
		to { transform: rotate(360deg); }
	}
	.el-table .cell {
		font-size: 13px; /* 或者你想要的任何大小 */
		color: #172329;
	}
	.paragraph-list {
		width: 100%;
		display: flex;
		overflow-y: scroll;
		background: #f4f4f4;
		padding: 10px;
		flex-wrap: wrap;
		justify-content: space-between;
	}
	.paragraph-list::-webkit-scrollbar { /* WebKit */
		width: 0 !important;
	}
	.paragraph-item {
		background: #fff;
		height: 200px;
		width: 49%;
		border-radius: 5px;
		margin-bottom: 10px;
		cursor: pointer;
		display: flex;
		flex-direction: column;
		padding: 10px;
	}
	.paragraph-title {
		width: 100%;
		height: 20px;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}
	.title-left {
		width: 320px;
	}
	.paragraph-doc {
		width: 100%;
		height: calc(100% - 70px);
		padding: 5px 0;
		overflow: hidden;
		color: #606266;
		margin-top: 10px;
	}
	.paragraph-bottom {
		width: 100%;
		height: 30px;
		margin-top: 10px;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
</style>
