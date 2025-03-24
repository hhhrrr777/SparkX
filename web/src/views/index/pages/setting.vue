<template>
	<div style="background: #fff;border-radius: 10px;padding: 10px 5px">
		<div style="width: 100%;height: 40px;"><el-button type="primary" style="float: right">保存并发布</el-button></div>
		<el-row class="setting-div">
			<el-col :span="10" class="setting-div-setting">
				<el-form ref="form" :model="form" :rules="rules" label-position="top" label-width="80px" style="padding: 10px 20px">
					<el-form-item label="应用名称" prop="name">
						<el-input v-model="form.name" maxlength="25" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="应用描述" prop="description">
						<el-input type="textarea" v-model="form.description" rows="3" maxlength="255" show-word-limit></el-input>
					</el-form-item>
					<el-form-item :for="'test'">
						<template #label>
							<div class="flex-center">
								<div><span style="color: var(--el-color-danger);">*</span> AI模型</div>
								<div class="flex-center setting-btn" @click="setAI">
									<el-button
										icon="el-icon-Setting"
										type="primary"
										link
										@click="setAI"
										:disabled="!form.model_id"
									>
										参数
									</el-button>
								</div>
							</div>
						</template>
						<el-select v-model="form.model_id" placeholder="请选择" style="width: 100%" clearable>
							<el-option-group
								v-for="group in options"
								:key="group.label"
								:label="group.label">
								<el-option
									v-for="item in group.options"
									:key="item.value"
									:label="item.label"
									:value="item.value">
								</el-option>
							</el-option-group>
						</el-select>
					</el-form-item>
					<el-form-item label="提示词" prop="prompt">
						<el-input type="textarea" v-model="form.prompt" rows="4" maxlength="1000" show-word-limit></el-input>
					</el-form-item>
					<el-form-item>
						<template #label>
							<div class="flex-center">
								<div>关联知识库</div>
								<div class="flex-center setting-btn">
									<div class="flex-center setting-btn">
										<el-button
											icon="el-icon-Setting"
											type="primary"
											link
										>
											参数
										</el-button>
									</div>

									<div class="flex-center setting-btn" style="margin-left: 10px">
										<el-button
											icon="el-icon-Plus"
											type="primary"
											link
										>
											添加
										</el-button>
									</div>
								</div>
							</div>
						</template>
						<div class="dataset-list">
							<div class="dataset-item">
								<div class="dataset-item-div">
									<el-icon size="20" color="#5E17EB" style="margin-right: 5px">
										<Document />
									</el-icon>
									<div class="line1">关联知识库关联知识库关联</div>
								</div>
								<el-icon style="margin-left: 5px">
									<Delete />
								</el-icon>
							</div>
						</div>
					</el-form-item>
					<el-form-item label="开场白" prop="prologue">
						<el-input type="textarea" v-model="form.prologue" rows="5" maxlength="500" show-word-limit></el-input>
					</el-form-item>
				</el-form>
				<div class="setting-box" style="width: calc(100% - 40px);">
					<div class="setting-title">空搜索回复</div>
					<el-switch
						active-text="AI"
						inactive-text="人工"
						v-model="form.empty_reply">
					</el-switch>
				</div>
				<el-form ref="form" :model="form" :rules="rules" label-position="top" label-width="80px" style="padding: 10px 20px">
					<el-form-item label="回复内容" prop="reply_content">
						<el-input type="textarea" v-model="form.reply_content" rows="3" maxlength="255" show-word-limit></el-input>
					</el-form-item>
				</el-form>
				<div class="setting-box-list">
					<div class="setting-box">
						<div class="setting-title">显示引用片段</div>
						<el-switch
							v-model="form.show_relation">
						</el-switch>
					</div>
					<div class="setting-box">
						<div class="setting-title">显示耗时</div>
						<el-switch
							v-model="form.show_time">
						</el-switch>
					</div>
					<div class="setting-box">
						<div class="setting-title">显示消耗token</div>
						<el-switch
							v-model="form.show_tokens">
						</el-switch>
					</div>
					<div class="setting-box">
						<div class="setting-title">显示评价</div>
						<el-switch
							v-model="form.show_appraise">
						</el-switch>
					</div>
					<div class="setting-box">
						<div class="setting-title">语音输入</div>
						<el-switch
							v-model="form.voice_input">
						</el-switch>
					</div>
					<div class="setting-box">
						<div class="setting-title">语音输出</div>
						<el-switch
							v-model="form.voice_out">
						</el-switch>
					</div>
				</div>
			</el-col>
			<el-col :span="14" style="background: #f4f4f4;padding: 10px;">
				<chat-box></chat-box>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import chatBox from '@/components/chatContent/index.vue'
import {Plus, Setting, Document, Delete} from "@element-plus/icons-vue";

export default {
	components: {Document, Plus, chatBox, Setting, Delete},
	data() {
		return {
			form: {},
			rules: {
				name: [
					{required: true, message: '应用标题不能为空', trigger: 'blur'}
				],
				description: [
					{required: true, message: '应用不能为空', trigger: 'blur'}
				]
			},
			options: [{
				label: '热门城市',
				options: [{
					value: 'Shanghai',
					label: '上海'
				}, {
					value: 'Beijing',
					label: '北京'
				}]
			}, {
				label: '城市名',
				options: [{
					value: 'Chengdu',
					label: '成都'
				}, {
					value: 'Shenzhen',
					label: '深圳'
				}, {
					value: 'Guangzhou',
					label: '广州'
				}, {
					value: 'Dalian',
					label: '大连'
				}]
			}],
		}
	},
	mounted() {
		this.appId = this.$route.query.appId;
		this.getInfo()
	},
	methods: {
		// 获取应用详情
		async getInfo() {
			let res = await this.$API.application.info.get({appId: this.appId})
			this.form = res.data
		},
		// 设置ai信息
		setAI(e) {
			console.log(23)
			e.stopPropagation()
		}
	}
}
</script>

<style scoped>
.flex-center {
	display: flex;align-items: center;justify-content: space-between
}
.setting-btn {
	cursor: pointer;
	color: #5E17EB;
}
.dataset-list {
	width: 100%;
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
}
.dataset-item {
	width: 30%;
	height: 50px;
	display: flex;
	align-items: center;
	padding: 20px;
	border: 1px solid #ddd;
	border-radius: 5px;
	font-size: 13px;
	cursor: pointer;
	margin-bottom: 10px;
}
.dataset-item-div {
	width: calc(100% - 20px);display: flex;align-items: center;
}
.setting-box-list {
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
}
.setting-box {
	width: 40%;
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin: 0 auto;
	margin-bottom: 10px;

}
.setting-div {
	height: calc(100vh - 160px);
}
.setting-div-setting {
	height: calc(100vh - 160px);
	overflow-y: scroll;
}
</style>
