<template>
	<el-container style="padding: 20px">
		<div class="go-back"  @click="goBack">
			<el-icon style="font-size: 18px">
				<component :is="backIcon"/>
			</el-icon>
			<span style="margin-left: 10px;font-size: 16px;cursor: pointer">返回</span>
		</div>

		<el-card shadow="never" class="custom-card">
			<el-steps :active="active" finish-status="success" simple>
				<el-step title="选择文档"></el-step>
				<el-step title="处理数据"></el-step>
				<el-step title="上传数据"></el-step>
			</el-steps>
			<div class="upload-box" v-if="active === 0">
				<h4 class="title">上传文档</h4>
				<el-button-group :round="true" class="btn-group">
					<el-button type="primary">文本文件</el-button>
					<el-button>Excel表格</el-button>
					<el-button>QA 问答对</el-button>
				</el-button-group>
				<div class="notice-box">
					<p>1、文件上传前，建议规范文件的分段标识</p>
					<p>2、每次最多上传 50 个文件，每个文件不超过 100MB</p>
				</div>

				<el-upload
					v-model:file-list="fileList"
					ref="upload"
					style="margin-top: 20px"
					class="upload-demo"
					:limit="50"
					accept=".txt,.md,.pdf,.docx,.html,.xls,.xlsx,.csv,.zip"
					:action="uploadUrl"
					:auto-upload="false"
					:show-file-list="false"
					:on-change="onChange"
					drag
					multiple
				>
					<el-icon size="48">
						<component :is="uploadIcon"/>
					</el-icon>
					<div class="el-upload__text">
						拖拽文件至此上传或 <em>选择文件</em>
						<p style="margin-top: 5px;font-size: 12px">支持格式：TXT、Markdown、PDF、DOCX、HTML、XLS、XLSX、CSV、ZIP </p>
					</div>
				</el-upload>

				<div class="file-list">
					<div class="file-item" v-for="(item, index) in fileList" :key="index">
						<div class="file-info">
							<img :src="`/src/assets/files_icon/` + getExtByName(item.name) + `.png`" style="width: 30px;">
							<div class="file-data">
								<div class="file-data-title line1">{{ item.name }}</div>
								<div class="file-data-size">{{ formatBytes(item.size) }}</div>
							</div>
						</div>
						<el-icon size="16" style="cursor: pointer" @click="delFile(index)">
							<component :is="delIcon"/>
						</el-icon>
					</div>
				</div>
			</div>

			<div class="document-box" v-if="active === 1">
				<div class="document-tool">
					<div class="title">分段设置</div>

					<div class="tool-list">
						<el-radio-group v-model="radio" class="too-radio-list">
							<el-radio :label="1" border class="radio-item">
								<div class="radio-title">默认分段</div>
								<div class="radio-desc">系统会根据换行符以512个字符为一块，自动拆分文本</div>
							</el-radio>
							<el-radio :label="2" border class="radio-item" :class="{'active-radio': radio === 2}">
								<div class="radio-title">自定义分段</div>
								<div class="radio-desc">根据用户自定义的规则拆分文本</div>
								<div class="tool-radio-box" v-if="radio === 2">
									<el-form label-position="top" label-width="80px" :model="diyForm" style="width: 450px;">
										<el-form-item label="分段标识">
											<el-select v-model="diyForm.patternList" multiple placeholder="请选择" style="width: 450px;">
												<el-option
													v-for="item in options"
													:key="item.value"
													:label="item.label"
													:value="item.value">
												</el-option>
											</el-select>
										</el-form-item>
										<el-form-item label="分段长度">
											<el-slider
												v-model="diyForm.splitLen"
												:min="100"
												:max="8000"
												show-input>
											</el-slider>
										</el-form-item>
										<el-form-item label="自动清洗">
											<el-switch
												v-model="diyForm.autoClean">
											</el-switch>
										</el-form-item>
										<p style="color:#8f9593;margin-top: -20px;font-size: 12px">去掉重复多余符号空格、空行、制表符</p>
									</el-form>
								</div>
							</el-radio>
						</el-radio-group>

						<el-checkbox v-model="checked" style="margin-left: 13px;margin-top: 20px"> 导入时添加分段标题为关联问题（适用于标题为问题的问答对） </el-checkbox>
						<el-button class="preview-btn">生成预览</el-button>
					</div>
				</div>
				<div class="document-preview">
					<div class="title">分段预览(2组)</div>
				</div>
			</div>
		</el-card>

		<div class="tool-bar">
			<el-button>取消</el-button>
			<el-button type="primary" @click="nextStep">上一步</el-button>
			<el-button type="primary" @click="nextStep">下一步</el-button>
		</div>
	</el-container>
</template>

<script>
import config from "@/config"
export default {
	data() {
		return {
			active: 1,
			backIcon: 'el-icon-Back',
			uploadIcon: 'el-icon-UploadFilled',
			delIcon: 'el-icon-delete',
			fileList: [],
			uploadUrl: config.API_URL + '/document/upload',
			isUpload: false,
			radio: 1,
			diyForm: {
				patternList: [],
				splitLen: 512,
				autoClean: true
			},
			options: [{
				value: '选项1',
				label: '黄金糕'
			}, {
				value: '选项2',
				label: '双皮奶'
			}, {
				value: '选项3',
				label: '蚵仔煎'
			}, {
				value: '选项4',
				label: '龙须面'
			}, {
				value: '选项5',
				label: '北京烤鸭'
			}],
			checked: false
		}
	},
	mounted() {
		this.uuid = this.$route.query.uuid;
	},
	methods: {
		goBack() {
			this.$router.go(-1)
		},
		onChange(file) {
			const fileSize = file.size / 1024 / 1024
			if (fileSize > 100) {
				this.$message.error('上传的文件不得超过100M')
				return false
			}
		},
		formatBytes(bytes, decimals = 2) {

			if (bytes === 0) return '0 Bytes';
			const k = 1024;
			const dm = decimals < 0 ? 0 : decimals;
			const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
			const i = Math.floor(Math.log(bytes) / Math.log(k));

			return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
		},
		getExtByName(name) {
			return name.split('.')[1]
		},
		delFile(index) {
			this.fileList.splice(index, 1)
		},
		// 下一步
		async nextStep() {
			// 上传文件
			if (this.active === 0) {
				if (this.fileList.length === 0) {
					this.$message.error('请上传文件')
					return
				}

				let formData = new FormData();
				// 将上传的文件放到数据对象中
				this.fileList.forEach(file => {
					formData.append('files', file.raw);
				});

				this.active = 1
				let res = await this.$API.document.upload.post(formData)
			}
		}
	}
}
</script>

<style scoped>
.go-back {
	width: 200px;
	height: 30px;
	display: flex;
	align-items: center;
	margin-bottom: 10px;
}
.upload-box {
	width: 70%;
	margin: 0 auto;
	margin-top: 20px;
}
.title {
	border-left: 5px solid #5E17EB;
	padding-left: 10px;
	font-size: 16px;
}
.btn-group {
	margin-top: 20px;
}
.notice-box {
	background: #eee7fd;
	border-radius: 4px;
	width: 100%;
	height: 66px;
	margin-top: 20px;
	display: flex;
	justify-content: center;
	flex-direction: column;
	padding-left: 20px;
}
.notice-box p {
	font-weight: 400;
	margin-top: 5px;
}
.tool-bar {
	width: calc(100% - 100px);
	height: 50px;
	background: #fff;
	position: absolute;
	bottom: 0;
	display: flex;
	align-items: center;
	text-align: right;
	border-radius: 10px;
	padding-left: 80%;
}
.file-item {
	width: 49%;
	height: 58px;
	border: 1px solid var(--el-card-border-color);
	border-radius: 5px;
	margin-bottom: 10px;
	padding: 10px 20px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.file-item .file-info {
	display: flex;
	align-items: center;
	width: calc(100% - 150px);
}
.file-data {
	margin-left: 8px;
	width: 100%;
}
.file-data-title {
	width: 100%;
}
.file-data-size {
	color: #8f959e;
	margin-top: 5px;
}
.file-list {
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
	overflow-y: scroll;
	margin-top: 10px;
}

.file-list  {
	scrollbar-width: none; /* Firefox */
	-ms-overflow-style: none;  /* Internet Explorer 10+ */
}

.file-list::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.document-box {
	width: 100%;
	height: calc(100vh - 240px);
	display: flex;
	margin-top: 20px;
}
.document-tool {
	width: 600px;
	height: 100%;
	border-right: 1px solid var(--el-card-border-color);
}
.document-preview {
	width: calc(100vh - 600px);
	height: 100%;
	overflow-y: scroll;
	padding-left: 20px;
}
.document-preview::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.tool-list {
	width: 100%;
}
.too-radio-list {
	width: 100%;
	display: flex;
	padding-left: 10px;
}
.radio-item {
	margin-top: 20px;
	width: calc(100% - 40px);
	padding: 30px 10px;
	font-size: 13px;
}
.radio-desc {
	margin-top: 5px;
	margin-left: 10px;
}
.radio-title {
	margin-left: 10px;
}
.tool-radio-box {
	background: #f5f6f7;
	height: 270px;
	margin-top: 10px;
	width: 500px;
	border-radius: 5px;
	padding: 20px;
}
.active-radio {
	height: 340px;
}
.preview-btn {
	position: relative;
	top: 40px;
	left: 42px;
}
</style>
