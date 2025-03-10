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
					prop="name"
					label="文档名称">
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
						<el-tag type="success" v-if="scope.row.status === 1">正常</el-tag>
						<el-tag type="danger" v-else>禁用</el-tag>
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
	</div>

</template>

<script>
import Pages from "@/components/pages/index.vue";

export default {
	components: {Pages},
	data() {
		return {
			tableData: [],
			searchForm: {
				name: '',
				datasetId: '',
				page: 1,
				limit: 10
			},
			page: {
				total: 0
			},
			datasetId: '',
			embeddingIcon: 'el-icon-Loading',
			menusIcon: 'el-icon-MoreFilled',
			settingIcon: 'el-icon-Setting',
			questionIcon: 'el-icon-QuestionFilled',
			delIcon: 'el-icon-Delete',
			switchIcon: 'el-icon-Switch'
		}
	},
	mounted() {

		this.datasetId = this.$route.query.datasetId;
		this.searchForm.datasetId = this.datasetId

		this.getList()
	},
	methods: {
		async getList() {
			let res = await this.$API.document.getList.get(this.searchForm)
			this.tableData = res.data.data
			this.page.total = res.data.total
		},
		onSubmit() {

		},
		handleEdit() {

		},
		handlePageChange() {

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
				this.$message.success(res.msg)
				this.getList()
			} else {
				this.$message.error(res.msg)
			}
		},
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
</style>
