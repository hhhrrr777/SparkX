<template>
	<div class="link-box">
		<div class="document-list">
			<el-autocomplete
				style="width: 100%"
				class="inline-input"
				v-model="searchTitle"
				:fetch-suggestions="querySearch"
				placeholder="文档名称"
				:trigger-on-focus="false"
				@select="handleSelect"
			></el-autocomplete>
			<div class="document-item"
				 @click="selectDocument(item)"
				 v-for="item in documentList" :key="item.documentId"
				 :class="{'document-active': item.documentId === this.selectedDocumentId}">
				<span style="width: 200px;" class="line1">{{ item.name }}</span>
				<span class="label" v-if="item.linkNum > 0">{{ item.linkNum }}</span>
			</div>
		</div>
		<div class="paragraph-list">
			<div class="paragraph-item" v-for="item in paragraphList" :key="item.paragraphId">
				<div class="paragraph-title">
					<div class="title-left line1" v-if="item.title.length > 0">{{ item.title }}</div>
					<div class="title-left line1" v-else>--</div>
				</div>
				<div class="paragraph-doc">
					{{ item.content }}
				</div>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	props: {
		datasetId: {
			type: String,
			default: ''
		},
		questionId: {
			type: String,
			default: ''
		}
	},
	data() {
		return {
			documentList: [],
			paragraphList: [],
			selectedDocumentId: "",
			searchTitle: ""
		}
	},
	mounted() {
		this.getDocumentList()
	},
	methods: {
		// 获取关联
		async getRelationList() {
			let res = await this.$API.question.getRelation.get({questionId: this.questionId})
			let relationData = res.data
			// 计算信息
		},
		// 获取文档列表
		async getDocumentList() {
			let res = await this.$API.document.getList.get({
				datasetId: this.datasetId,
				name: "",
				page: 1,
				limit: 2000
			})
			this.documentList = res.data.data
			if (this.documentList.length > 0) {
				this.selectedDocumentId = this.documentList[0].documentId
				this.getParagraphList()
			}
		},
		// 获取段落列表
		async getParagraphList() {
			let res = await this.$API.paragraph.getList.get({
				documentId: this.selectedDocumentId,
				page: 1,
				limit: 2000
			})
			this.paragraphList = res.data.data

			this.getRelationList()
		},
		// 选择文档
		selectDocument(row) {
			this.selectedDocumentId = row.documentId
			this.getParagraphList()
		},
		querySearch(queryString, cb) {
			var restaurants = this.documentList.map(item => {
				return {value: item.name, documentId: item.documentId}
			});

			var results = queryString ? restaurants.filter(this.createFilter(queryString)) : restaurants;
			cb(results);
		},
		createFilter(queryString) {
			return (restaurant) => {
				return (restaurant.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
			};
		},
		handleSelect(row) {
			this.selectDocument(row)
		}
	}
}
</script>

<style scoped>
.link-box {
	width: 100%;
	height: 100%;
	border-top: 1px solid #DCDFE6;
	display: flex;
}
.document-list {
	width: 250px;
	border-right: 1px solid #DCDFE6;
	padding: 10px;
	height: 600px;
	overflow-y: auto;
}
.document-list::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.paragraph-list {
	width: calc(100% - 249px);
	height: 600px;
	overflow-y: auto;
}
.paragraph-list::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.paragraph-item {
	background: #fff;
	height: 200px;
	width: 49%;
	border-radius: 5px;
	margin-bottom: 10px;
	cursor: pointer;
	display: flex;
	flex-direction: column;
	padding: 10px;
	line-height: 22px;
	font-size: 13px;
}
.paragraph-title {
	width: 100%;
	height: 20px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.title-left {
	width: 320px;
}
.paragraph-doc {
	width: 100%;
	height: calc(100% - 20px);
	padding: 5px 0;
	overflow-y: auto;
	color: #606266;
	margin-top: 10px;
}
.paragraph-doc::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.active {
	border: 1px solid #5E17EB;
}
.document-item {
	width: 100%;
	height: 30px;
	margin-top: 5px;
	padding: 5px 5px;
	cursor: pointer;
	display: flex;
	align-items: center;
	font-size: 13px;
}
.document-item:hover {
	background: #eee7fd;
	border-radius: 5px;
	color: #5E17EB;
}
.document-active {
	background: #eee7fd;
	color: #5E17EB;
	border-radius: 5px;
}
.label {
	width: 20px;
	height: 20px;
	border-radius: 20px;
	line-height: 20px;
	text-align: center;
	color: #fff;
	background: #5E17EB;
	font-size: 11px;
	margin-left: 5px;
}
</style>
