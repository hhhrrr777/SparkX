<template>
	<el-dialog :title="titleMap[mode]" v-model="visible" :width="700" destroy-on-close @closed="$emit('closed')">
		<el-form :model="form" :rules="rules" ref="ruleForm" label-width="100px">
			<el-form-item label="知识库标题" prop="title">
				<el-input v-model="form.title" maxlength="25" show-word-limit></el-input>
			</el-form-item>
			<el-form-item label="知识库描述" prop="description">
				<el-input v-model="form.description" type="textarea" maxlength="255" show-word-limit :rows="5"></el-input>
			</el-form-item>
			<el-form-item label="向量模型">

			</el-form-item>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="visible = false">取 消</el-button>
				<el-button type="primary" @click="optSubmit('ruleForm')" :loading="loading">确 定</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<script>
export default {
	emits: ['success', 'closed'],
	data() {
		return {
			mode: "add",
			titleMap: {
				add: '新增知识库',
				edit: '编辑知识库',
				show: '查看'
			},
			form: {
				id: 0,
				title: '',
				description: '',
				type: 1,
				embedding_mode_id: 'test'
			},
			rules: {
				title: [
					{required: true, message: '知识库标题不能为空', trigger: 'blur'}
				],
				description: [
					{required: true, message: '知识库描述不能为空', trigger: 'blur'}
				]
			},
			loading: false,
			visible: false
		}
	},
	methods: {
		//显示
		open(mode = 'add') {
			this.mode = mode;
			this.visible = true;
			return this
		},
		// 表单提交方法
		optSubmit(formName) {
			this.$refs[formName].validate(async (valid) => {
				if (valid) {
					this.loading = true
					let res = await this.$API.dataset.add.post(this.form);
					this.loading = false

					if (res.code == 0) {
						this.$message.success(res.msg)
						this.$emit('success')
					} else {
						this.$message.error(res.msg)
					}
				} else {
					return false;
				}
			})
		}
	}
}
</script>

<style scoped>

</style>

