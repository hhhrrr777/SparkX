<template>
	<el-table :data="dataTable" :header-cell-style="{ backgroundColor: '#f5f6f7' }">
		<el-table-column prop="title" :label="title" v-if="activeName === '1'"/>
		<el-table-column prop="name" :label="title" v-else/>
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
			<template #default="{ row, $index }">
				<el-checkbox
					:disabled="isAdmin"
					v-model="row.manage"
					@change="(value) => adminChange($index, value)"
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
			<template #default="{ row, $index }">
				<el-checkbox
					:disabled="isAdmin"
					v-model="row.view"
					@change="(value) => viewChange($index, value)"
				/>
			</template>
		</el-table-column>
	</el-table>
</template>

<script>
export default {
	props: {
		activeName: {
			type: String,
			default: '1',
		},
		isAdmin: {
			type: Boolean,
			default: false
		},
		permissionData: {
			type: Object,
			default: {
				manage: [],
				view: []
			}
		},
		nowUserId: {
			type: String,
			default: ''
		}
	},
	data() {
		return {
			title: '',
			dataTable: [],
			checkAdminAll: false,
			checkViewAll: false,
			allIndeterminate: [], // 全选
		}
	},
	watch: {
		checkAdminAll: {
			handler(value) {
				let data = []
				this.dataTable.forEach(item => {
					item.manage = value
					item.view = value

					data.push(item)
				})
				this.dataTable = data
				this.checkAdminAll = this.checkViewAll = value
				this.allIndeterminate[1] = this.allIndeterminate[2] = !value
				this.$emit('update', this.dataTable)
			},
			deep: true,
		},
		checkViewAll: {
			handler(value) {
				let data = []
				this.dataTable.forEach(item => {
					item.view = value

					data.push(item)
				})
				this.dataTable = data
				this.$emit('update', this.dataTable)
			},
			deep: true,
		}
	},
	created() {
		if (this.activeName === '1') {
			this.getDatabaseList()
			this.title = '知识库名称'
		} else {
			this.getAppList()
			this.title = '应用名称'
		}
	},
	methods: {
		// 获取知识库列表
		async getDatabaseList() {
			let res = await this.$API.dataset.list.get({page: 1, limit: 1000, title: ''})
			this.dataTable = res.data.data
			this.defaultSelect()
		},
		// 获取应用列表
		async getAppList() {
			let res = await this.$API.application.list.get({page: 1, limit: 1000, name: '', type: 0})
			this.dataTable = res.data.data
			this.defaultSelect()
		},
		// 默认勾选
		defaultSelect() {
			if (this.isAdmin) {
				this.checkAdminAll = true
				this.checkViewAll = true
			} else {

				// 管理权限
				let count = 0
				let viewCount = 0
				this.dataTable.forEach(item => {
					if (item.userId === this.nowUserId) {

						let id = item.datasetId
						if (this.activeName === '2') {
							id = item.appId
						}

						if (this.permissionData.manage?.indexOf(id) !== -1) {
							count += 1
						}

						if (this.permissionData.view?.indexOf(id) !== -1) {
							viewCount += 1
						}
					}
				})

				let len = this.dataTable.length
				if (count < len && count > 0) {
					this.allIndeterminate[1] = true
				}

				if (count === len) {
					this.checkAdminAll = this.checkViewAll = true
					this.allIndeterminate[1] = false
				}

				if (viewCount < len && viewCount > 0) {
					this.allIndeterminate[2] = true
				}

				if (viewCount === len) {
					this.checkViewAll = true
					this.allIndeterminate[2] = false
				}
			}
		},
		// 更选选择
		adminChange(index, value) {
			this.dataTable[index].manage = value
			this.dataTable[index].view = value

			let count = 0;
			this.dataTable.forEach(item => {
				if (item.manage) {
					count += 1
				}
			})

			let len = this.dataTable.length
			if (count < len && count > 0) {
				this.allIndeterminate[1] = this.allIndeterminate[2] = true
			}

			if (count === len) {
				this.checkAdminAll = this.checkViewAll = true
				this.allIndeterminate[1] = this.allIndeterminate[2] = false
			}

			this.$emit('update', this.dataTable)
		},
		// 查看选择
		viewChange(index, value) {
			this.dataTable[index].view = value

			let count = 0;
			this.dataTable.forEach(item => {
				if (item.view) {
					count += 1
				}
			})

			let len = this.dataTable.length
			if (count < len && count > 0) {
				this.allIndeterminate[2] = true
			}

			if (count === len) {
				this.checkViewAll = true
				this.allIndeterminate[2] = false
			}

			this.$emit('update', this.dataTable)
		},
	}
}
</script>

<style scoped>

</style>
