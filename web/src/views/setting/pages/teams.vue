<template>
	<div style="width: 100%;height: calc(100% - 20px);margin-top: 10px">
		<el-row style="height: 100%;">
			<el-col :span="4">
				<div class="user-list">
					<el-button type="primary" icon="el-icon-plus" circle style="float: right" size="small"></el-button>
					<el-input
						style="margin-top: 10px;margin-bottom: 10px;"
						placeholder="请输入用户名"
						suffix-icon="el-icon-search"
						v-model="searchForm.name">
					</el-input>

					<div class="user-item"
						 v-for="item in userList"
						 :key="item.userId"
						 :class="{'span-between': item.isAdmin === 2, 'user-active': nowUserId === item.userId}"
					>{{ item.name }}
						<el-tag style="margin-left: 10px" v-if="item.isAdmin === 1">创始人</el-tag>
						<el-icon style="margin-right: 10px" v-if="item.isAdmin === 2">
							<Delete />
						</el-icon>
					</div>
				</div>
			</el-col>
			<el-col :span="19" style="margin-left: 20px">
				<div class="data-list">
					<el-button type="primary" icon="el-icon-Document">保存权限</el-button>
					<el-tabs v-model="activeName" style="margin-top: 10px">
						<el-tab-pane label="知识库" name="1">
							<el-table :data="tableData" :header-cell-style="{ backgroundColor: '#f5f6f7' }">
								<el-table-column prop="title" label="知识库名称"/>
								<el-table-column
									align="center"
									width="100"
									fixed="right"
								>
									<template #header>
										<el-checkbox
											:disabled="isAdmin"
											v-model="checkAdminAll"
											label="管理"
											:indeterminate="allIndeterminate[1]"
										/>
									</template>
									<template #default="{ row }">
										<el-checkbox
											:disabled="isAdmin"
											v-model="row.manage"
											@change="adminChange"
										/>
									</template>
								</el-table-column>
								<el-table-column
									align="center"
									width="100"
									fixed="right"
								>
									<template #header>
										<el-checkbox
											:disabled="isAdmin"
											v-model="checkViewAll"
											label="查看"
											:indeterminate="allIndeterminate[2]"
										/>
									</template>
									<template #default="{ row }">
										<el-checkbox
											:disabled="isAdmin"
											v-model="row.view"
											@change="viewChange"
										/>
									</template>
								</el-table-column>
							</el-table>
						</el-tab-pane>
						<el-tab-pane label="应用" name="2">

						</el-tab-pane>
					</el-tabs>
				</div>
			</el-col>
		</el-row>
	</div>

	<el-dialog title="添加成员" v-model="dialogVisible" width="500px" destroy-on-close :close-on-click-modal="false">
		<el-form :model="form" label-width="80px">
			<el-form-item label="用户昵称" prop="nickname">
				<el-input v-model="form.nickname" placeholder="请输入用户昵称"></el-input>
			</el-form-item>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="dialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="optSubmit('ruleForm')">确 定</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<script>
import {Delete} from "@element-plus/icons-vue";

export default {
	components: {Delete},
	data() {
		return {
			searchForm: {
				name: ""
			},
			form: {
				teamId: 0,
				nickname: "",
			},
			activeName: "1",
			tableData: [],
			tableAppData: [],
			isAdmin: false,
			checkAdminAll: false,
			checkViewAll: false,
			allIndeterminate: [], // 全选
			dialogVisible: false,
			rules: {
				nickname: [
					{required: true, message: '请输入昵称', trigger: 'blur'}
				]
			},
			userList: [],
			nowUserId: ""
		}
	},
	watch: {
		checkAdminAll: {
			handler(value) {
				console.log(555, value)
			},
			deep: true,
		},
		checkViewAll: {
			handler(value) {
				console.log(666, value)
			},
			deep: true,
		}
	},
	mounted() {
		this.getDatabaseList()
		this.getTeamUserList()
	},
	methods: {
		// 获取知识库列表
		async getDatabaseList() {
			let res = await this.$API.dataset.list.get({page: 1, limit: 1000, title: ''})
			this.tableData = res.data.data
		},
		// 获取团队成员
		async getTeamUserList() {
			let res = await this.$API.team.userList.get()
			this.userList = res.data
			if (this.userList.length > 0) {
				this.nowUserId = this.userList[0].userId
			}
		},
		// 更选选择
		adminChange(row) {
			console.log(222, row)
		},
		// 查看选择
		viewChange(row) {
			console.log(333, row)
		},
		// 保存添加用户
		optSubmit(formName) {
			this.$refs[formName].validate(async (valid) => {
				if (valid) {

				}
			})
		}
	}
}
</script>

<style scoped>
.user-list {
	width: 100%;
	height: 100%;
	background: #fff;
	border-radius: 6px;
	padding: 20px;
}
.user-item {
	width: 100%;
	height: 40px;
	overflow: hidden;
	display: flex;
	align-items: center;
	padding-left: 10px;
}
.user-active {
	background: #EEE7fd;
	color: var(--el-color-theme);
	border-radius: 5px;
}
.span-between {
	justify-content: space-between;
}
.data-list {
	width: 100%;
	height: 100%;
	background: #fff;
	border-radius: 6px;
	padding: 20px;
}
</style>
