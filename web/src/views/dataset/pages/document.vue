<template>
	<div style="background: #fff;border-radius: 10px;padding: 10px 5px">
		<div class="search-box">
			<div>
				<el-button type="primary" icon="el-icon-UploadFilled" @click="uploadFile" style="margin-top: -10px;">上传文档</el-button>
				<el-button type="primary" icon="el-icon-Switch" @click="uploadFile" style="margin-top: -10px;">迁移文档</el-button>
				<el-button type="primary" icon="el-icon-Refresh" @click="uploadFile" style="margin-top: -10px;">向量文档</el-button>
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
				:header-cell-style="{background:'#f4f4f4'}"
				:data="tableData"
				style="width: 100%">
				<el-table-column
					prop="name"
					label="文档名称">
				</el-table-column>
				<el-table-column
					prop="fileSize"
					label="文件大小">
				</el-table-column>
				<el-table-column
					label="状态">
					<template #default="scope">
						<el-tag type="success" v-if="scope.row.status === 1">正常</el-tag>
						<el-tag type="danger" v-else>禁用</el-tag>
					</template>
				</el-table-column>
				<el-table-column
					prop="createTime"
					label="创建时间">
				</el-table-column>
				<el-table-column
					prop="operation"
					label="操作">
					<template #default="scope">
						<el-button @click="handleEdit(scope.row)" type="text" size="small">编辑</el-button>
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
			datasetId: ''
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
</style>
