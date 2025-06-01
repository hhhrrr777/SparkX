<template>
	<div style="width: 100%;height: calc(100% - 20px);margin-top: 10px">
		<el-row style="height: 100%;">
			<el-col :span="4">
				<div class="user-list">
					<el-button type="primary" icon="el-icon-plus" circle style="float: right" size="small" @click="addUser"></el-button>
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
					<el-button type="primary" icon="el-icon-Document" @click="savePermission" :disabled="isAdmin">保存权限</el-button>
					<el-tabs v-model="activeName" style="margin-top: 10px">
						<el-tab-pane label="知识库" name="1">
							<permission title="知识库名称" activeName="1" :is-admin="isAdmin" @update="dataChange" :key="datasetkey"></permission>
						</el-tab-pane>
						<el-tab-pane label="应用" name="2">
							<permission title="应用名称" activeName="2" :is-admin="isAdmin" @update="dataChange" :key="appKey"></permission>
						</el-tab-pane>
					</el-tabs>
				</div>
			</el-col>
		</el-row>
	</div>

	<add-user ref="addUser" v-if="dialogVisible" @success="handleSuccess" @closed="dialogVisible=false" :close-on-click-modal="false"></add-user>
</template>

<script>
import {Delete} from "@element-plus/icons-vue";
import permission from "@/views/setting/sub/permission.vue"
import addUser from "@/views/setting/sub/addUser.vue"
import saveDialog from "@/views/dataset/save.vue";

export default {
	components: {
		saveDialog,
		Delete,
		permission,
		addUser
	},
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
			dialogVisible: false,
			userList: [],
			nowUserId: "",
			isAdmin: false,
			datasetkey: Math.random(),
			appKey: Math.random(),
			permissionData: []
		}
	},
	mounted() {
		this.getTeamUserList()
	},
	methods: {
		// 添加用成员
		addUser() {
			this.dialogVisible = true

			this.$nextTick(() => {
				this.$refs.addUser.open()
			})
		},
		// 获取团队成员
		async getTeamUserList() {
			let res = await this.$API.team.userList.get()
			this.userList = res.data
			if (this.userList.length > 0 && this.nowUserId === '') {
				this.nowUserId = this.userList[0].userId
				this.isAdmin = this.userList[0].isAdmin === 1
			}
			this.datasetkey = Math.random()
		},
		// 保存权限
		savePermission() {

		},
		// 权限数据
		dataChange(data) {
			let saveData = []
			data.forEach((item) => {
				if (item.manage || item.view) {
					saveData.push({
						id: (this.activeName === '1') ? item.datasetId : item.appId,
						manage: (typeof item.manage === 'undefined') ? false : item.manage,
						view: (typeof item.view === 'undefined') ? false : item.view,
					})
				}
			})

			this.permissionData = saveData
		},
		// 添加用户成功
		handleSuccess(userId) {
			this.dialogVisible = false

			this.nowUserId = userId
			this.getTeamUserList()
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
