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
			<div class="upload-box">
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
					style="margin-top: 20px"
					class="upload-demo"
					:limit="50"
					accept=".txt,.md,.pdf,.docx,.html,.xls,.xlsx,.csv,.zip"
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
							<img :src="`/src/assets/files_icon/` + item.ext + `.svg`" style="width: 30px;">
							<div class="file-data">
								<div class="file-data-title line1">{{ item.name }}</div>
								<div class="file-data-size">{{ item.size }}</div>
							</div>
						</div>
						<el-icon size="16" style="cursor: pointer">
							<component :is="delIcon"/>
						</el-icon>
					</div>
				</div>
			</div>
		</el-card>

		<div class="tool-bar">
			<el-button>取消</el-button>
			<el-button type="primary">下一步</el-button>
		</div>
	</el-container>
</template>

<script>

export default {
	data() {
		return {
			active: 0,
			backIcon: 'el-icon-Back',
			uploadIcon: 'el-icon-UploadFilled',
			delIcon: 'el-icon-delete',
			fileList: []
		}
	},
	mounted() {
		this.uuid = this.$route.query.uuid;

		console.log('-----', this.uuid);
	},
	methods: {
		goBack() {
			this.$router.go(-1)
		},
		onChange(file) {
			const fileSize = file.size / 1024 / 1024
			if (fileSize > 50) {
				this.$message.error('上传的文件不得超过50M')
				return false
			}

			this.fileList.push({
				name: file.name,
				size: this.formatBytes(file.size),
				ext: file.name.split('.')[1].toLowerCase(),
			});
		},
		formatBytes(bytes, decimals = 2) {

			if (bytes === 0) return '0 Bytes';
			const k = 1024;
			const dm = decimals < 0 ? 0 : decimals;
			const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
			const i = Math.floor(Math.log(bytes) / Math.log(k));

			return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
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
</style>
