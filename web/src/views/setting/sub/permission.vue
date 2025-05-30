<template>
	<el-table :data="dataTable" :header-cell-style="{ backgroundColor: '#f5f6f7' }">
		<el-table-column prop="title" :label="title"/>
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
		title: {
			type: String,
			default: "知识库名称"
		}
	},
	data() {
		return {
			dataTable: [],
			isAdmin: false,
			checkAdminAll: false,
			checkViewAll: false,
			allIndeterminate: [], // 全选
			needSend: false
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
				this.checkViewAll = value
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
		}
	},
	methods: {
		// 获取知识库列表
		async getDatabaseList() {
			let res = await this.$API.dataset.list.get({page: 1, limit: 1000, title: ''})
			this.dataTable = res.data.data
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
			if (count < len) {
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
			if (count < len) {
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
