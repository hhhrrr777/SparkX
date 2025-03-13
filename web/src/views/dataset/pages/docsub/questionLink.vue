<template>
	<div class="line1" style="width: 600px;height: 40px;">{{ title }}</div>
	<div class="paragraph-list">
		<div class="paragraph-item" v-for="item in paragraphList" :key="item.paragraphId" v-if="paragraphList.length > 0">
			<div class="paragraph-title">
				<div class="title-left line1" v-if="item.title.length > 0">{{ item.title }}</div>
				<div class="title-left line1" v-else>--</div>
			</div>
			<div class="paragraph-doc">
				{{ item.content }}
			</div>
		</div>
		<el-empty description="暂无关联数据" style="margin: 0 auto" v-else></el-empty>
	</div>
</template>

<script>
export default {
	props: {
		questionId: {
			type: String,
			default: ''
		},
		datasetId: {
			type: String,
			default: ''
		},
		title: {
			type: String,
			default: ''
		},
	},
	data() {
		return {
			paragraphList: []
		}
	},
	mounted() {
		this.getRelinkList()
	},
	methods: {
		// 获取关联信息
		async getRelinkList() {
			let res = await this.$API.question.getRelationData.get({questionId: this.questionId, datasetId: this.datasetId})
			this.paragraphList = res.data
		}
	}
}
</script>

<style scoped>
.paragraph-list {
	width: 100%;
	display: flex;
	overflow-y: scroll;
	background: #f4f4f4;
	padding: 10px;
	flex-wrap: wrap;
	justify-content: space-between;
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
	height: calc(100% - 71px);
	padding: 5px 0;
	overflow: hidden;
	color: #606266;
	margin-top: 10px;
}
</style>
