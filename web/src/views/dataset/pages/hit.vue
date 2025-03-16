<template>
	<div style="background: #fff;border-radius: 10px;padding: 10px 5px">
		<div class="tool-bar">
			<div class="search-type">
				<el-icon>
					<Setting />
				</el-icon>
				向量检索
			</div>
			<div class="search-input" style="width: calc(100% - 200px);">
				<el-input v-model="searchForm.keyword" suffix-icon="el-icon-search" @keyup.enter.native="query" clearable/>
			</div>
		</div>
		<div class="hit-list">
			<div class="content" v-if="hitList.length > 0">
				<div class="paragraph-item" v-for="(item, index) in hitList" :key="index">
					<div style="color: #999;border-bottom: 1px solid #e2e2e2;font-size: 13px;padding-bottom: 5px;">段落ID: {{ item.paragraphId }}</div>
					<div class="paragraph-title">
						<div class="title-left line1" v-if="item.title.length > 0">{{ item.title }}</div>
						<div class="title-left line1" v-else>--</div>
					</div>
					<div class="paragraph-doc">
						{{ item.content }}
					</div>
					<div class="paragraph-bottom">
						<span>相似度：{{ item.comprehensiveScore.toFixed(3) }}</span>
						<span>来源文档：{{ item.documentName }}</span>
					</div>
				</div>
			</div>
			<el-empty style="margin-top: 10%" description="无命中段落" v-loading="loading" v-else></el-empty>
		</div>
	</div>
</template>

<script>
import {Setting} from "@element-plus/icons-vue";

export default {
	components: {Setting},
	data() {
		return {
			searchForm: {
				keyword: "",
				type: "mix", //"embedding",
				similarity: 0.0,
				topRank: 5,
				datasetIds: ""
			},
			hitList: [],
			loading: false
		}
	},
	mounted() {
		this.searchForm.datasetIds = this.$route.query.datasetId
	},
	methods: {
		// 查询测试
		async query() {
			this.loading = true
			let res = await this.$API.dataset.hitTest.post(this.searchForm)
			setTimeout(() => {
				this.loading = false
			}, 800)
			this.hitList = res.data
		}
	}
}
</script>

<style scoped>
.tool-bar {
	display: flex;
	align-items: center;
	padding: 10px;
}
.search-type {
	border: 1px solid #5E17EB;
	border-radius: 5px;
	color: #5E17EB;
	padding: 5px 10px;
	cursor: pointer;
	display: flex;
	align-items: center;
}
.search-input {
	margin-left: 20px;
}
.hit-list {
	height: calc(100vh - 210px);
	width: 100%;
	background: #fff;
	padding: 10px;
	overflow-y: scroll;
}
.hit-list::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.hit-list .content {
	background: #f4f4f4;
	width: 100%;
	padding: 20px 10px;
	display: grid;
	box-sizing: border-box;
	margin-top: 16px;
	gap: 16px;
	grid-template-columns: repeat(auto-fill, minmax(24%, 1fr));
}
.paragraph-item {
	background: #fff;
	height: 250px;
	border-radius: 5px;
	cursor: pointer;
	padding: 15px 12px 15px 12px;
	margin-right: 5px;
	flex-direction: column;
	align-items: flex-start;
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
	height: calc(100% - 81px);
	overflow: hidden;
	color: #606266;
	margin-top: 10px;
}
.paragraph-bottom {
	width: 100%;
	height: 30px;
	margin-top: 10px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding-bottom: 10px;
}
</style>
