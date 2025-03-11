<template>
	<el-button type="primary" icon="el-icon-plus" @click="add" style="margin-bottom: 10px;">添加分段</el-button>
	<div class="paragraph-list">
		<div class="paragraph-item" v-for="item in paragraphList" :key="item.paragraphId">
			<div class="paragraph-title">
				<div class="title-left line1" v-if="item.title.length > 0">{{ item.title }}</div>
				<div class="title-left line1" v-else>--</div>
				<el-switch v-model="item.active" :active-value="1" :inactive-value="2" @change="activeParagraph(item)"/>
			</div>
			<div class="paragraph-doc" @click="showEditor(item)">
				{{ item.content }}
			</div>
			<div class="paragraph-bottom">
				<span>{{ (item.content).length }} 字符</span>
				<el-dropdown trigger="click" @command="handleCommand($event, item)">
					<el-icon color="#5E17EB">
						<component :is="menusIcon"></component>
					</el-icon>
					<template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item command="question">
								<el-icon>
									<component :is="questionIcon"></component>
								</el-icon>
								生成问题
							</el-dropdown-item>
							<el-dropdown-item command="transfer">
								<el-icon>
									<component :is="switchIcon"></component>
								</el-icon> 迁移
							</el-dropdown-item>
							<el-dropdown-item command="del">
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

	<!-- 段落编辑 -->
	<el-dialog v-model="editorVisible" width="1000px" ref="saveDialog" :close-on-click-modal="false">
		<el-form :model="contentForm" label-width="10px" v-if="modeType === 'edit'">
			<el-form-item>
				<el-input v-model="contentForm.title" placeholder="标题" maxlength="255" show-word-limit></el-input>
			</el-form-item>
			<el-form-item>
				<el-input type="textarea" :rows="8" v-model="contentForm.content"  placeholder="内容" style="width: 100%" maxlength="8000" show-word-limit></el-input>
			</el-form-item>
		</el-form>
		<el-form :model="addForm" label-width="10px" v-if="modeType === 'add'">
			<el-form-item>
				<el-input v-model="addForm.title" placeholder="标题" maxlength="255" show-word-limit></el-input>
			</el-form-item>
			<el-form-item>
				<el-input type="textarea" :rows="8" placeholder="内容" v-model="addForm.content" style="width: 100%" maxlength="8000" show-word-limit></el-input>
			</el-form-item>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="editorVisible = false">取 消</el-button>
				<el-button type="primary" @click="optSubmit()" :loading="loading">确 定</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<script>
import Pages from "@/components/pages/index.vue"

export default {
	components: {Pages},
	props: {
		documentId: {
			type: String,
			default: ""
		},
		datasetId: {
			type: String,
			default: ""
		},
	},
	data() {
		return {
			paragraphList: [],
			paragraphForm: {
				documentId: '',
				page: 1,
				limit: 10
			},
			paragraphPage: {
				total: 0
			},
			editorVisible: false,
			contentForm: {
				paragraphId: "",
				title: "",
				content: ""
			},
			addForm: {
				datasetId: "",
				documentId: "",
				title: "",
				content: ""
			},
			loading: false,
			selectedDocumentIds: [],
			modeType: 'add',
			menusIcon: 'el-icon-MoreFilled',
			settingIcon: 'el-icon-Setting',
			questionIcon: 'el-icon-QuestionFilled',
			delIcon: 'el-icon-Delete',
			switchIcon: 'el-icon-Switch',
		}
	},
	mounted() {
		this.paragraphForm.documentId = this.documentId
		this.addForm.documentId = this.documentId
		this.addForm.datasetId = this.datasetId

		this.getParagraphList()
	},
	methods: {
		// 段落翻页
		handleParagraphPageChange(page) {
			this.paragraphForm.page = page
			this.getParagraphList()
		},
		// 获取段落列表
		async getParagraphList() {
			let res = await this.$API.paragraph.getList.get(this.paragraphForm)
			this.paragraphList = res.data.data
			this.paragraphPage.total = res.data.total
		},
		// 显示内容编辑
		showEditor(row) {

			this.modeType = 'edit'
			this.contentForm.paragraphId = row.paragraphId
			this.contentForm.title = row.title
			this.contentForm.content = row.content
			this.editorVisible = true
		},
		// 编辑单个段落
		async optSubmit() {
			let res;
			if (this.modeType === 'edit') {
				res = await this.$API.paragraph.edit.post(this.contentForm)
			} else {
				res = await this.$API.paragraph.add.post(this.addForm)
			}

			if (res.code === 0) {
				this.$message.success('操作成功')
				this.getParagraphList()
				this.editorVisible = false
			} else {
				this.$message.error(res.msg)
			}
		},
		// 激活、关闭段落
		async activeParagraph(row) {
			let res = await this.$API.paragraph.active.post({
				paragraphId: row.paragraphId,
				active: row.active
			})

			if (res.code === 0) {
				this.$message.success('操作成功')
			} else {
				this.$message.error(res.msg)
			}
		},
		// 操作栏
		handleCommand(event, row) {
			switch (event) {
				case 'del':
					this.handleDel(row)
					break;
			}
		},
		// 删除段落
		handleDel(row) {
			this.$confirm('此操作将永久删除该段落 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(async () => {
				let res = await this.$API.paragraph.del.post({paragraphId: row.paragraphId})
				if (res.code === 0) {
					this.$message.success(res.msg)
					this.getParagraphList()
				} else {
					this.$message.error(res.msg)
				}
			}).catch(() => {
			});
		},
		// 添加段落
		add() {

			this.modeType = 'add'
			this.addForm.title = ''
			this.addForm.content = ''
			this.addForm.datasetId = this.datasetId
			this.editorVisible = true
		},
	}
}
</script>

<style scoped>

</style>
