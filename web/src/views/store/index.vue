<template>
	<el-container style="padding: 20px" class='store-div-box'>
		<el-card style="height: 900px" shadow="never">
			<div class="title">知识库</div>
			<el-form :inline="true" :model="searchForm" class="demo-form-inline" style="float: right;margin-top: 10px">
				<el-form-item>
					<el-input v-model="searchForm.name" placeholder="知识库标题" clearable></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="onSubmit" icon="el-icon-search">查询</el-button>
				</el-form-item>
			</el-form>
			<el-row class="store-list">
				<el-col :span="6" class="store-item">
					<el-card class="add-box" shadow="never">
						<div class="add-item-box">
							<div class="add-icon">
								<el-icon class="icon-color">
									<component :is="plusIcon"></component>
								</el-icon>
							</div>
							<div class="add-store-name"> 创建知识库</div>
						</div>
					</el-card>
				</el-col>

				<el-col :span="6" class="store-item" v-for="item in storeList" :key="item.code">
					<el-card style="height: 170px;padding: 10px" shadow="never">
						<div class="title-box">
							<div class="title-left">
								<div class="title-label">知</div>
								<div class="title-info">
									<div class="line1 knowledge-title">这是一个测试的知识库</div>
									<div class="author">创建者： admin</div>
								</div>
							</div>
							<div class="title-right">
								<el-tag type="primary">通用</el-tag>
							</div>
						</div>
						<div class="desc-box">
							这是一段关于知识库的描述
						</div>
						<div class="tool-box">
							<div class="tool-box-left">
								<div class="box-item">
									<span class="num">1</span>
									<span class="num-label">文档数</span>
								</div>
								<el-divider direction="vertical"></el-divider>
								<div class="box-item">
									<span class="num">5.7k</span>
									<span class="num-label">字符</span>
								</div>
								<el-divider direction="vertical"></el-divider>
								<div class="box-item">
									<span class="num">1</span>
									<span class="num-label">关联应用</span>
								</div>
							</div>
							<div class="tool-box-right">
								<el-dropdown trigger="click">
									<el-icon>
										<component :is="menusIcon"></component>
									</el-icon>
									<template #dropdown>
										<el-dropdown-menu>
											<el-dropdown-item>
												<el-icon>
													<component :is="embedingIcon"></component>
												</el-icon> 向量化
											</el-dropdown-item>
											<el-dropdown-item>
												<el-icon>
													<component :is="settingIcon"></component>
												</el-icon> 设置</el-dropdown-item>
											<el-dropdown-item>
												<el-icon>
													<component :is="downloadIcon"></component>
												</el-icon> 导出Excel</el-dropdown-item>
											<el-dropdown-item>
												<el-icon>
													<component :is="downloadIcon"></component>
												</el-icon> 导出ZIP</el-dropdown-item>
											<el-dropdown-item>
												<el-icon>
													<component :is="delIcon"></component>
												</el-icon> 删除</el-dropdown-item>
										</el-dropdown-menu>
									</template>
								</el-dropdown>
							</div>
						</div>
					</el-card>
				</el-col>
			</el-row>
		</el-card>
		<Pages :form="searchForm" :page-obj="page" @pageChange="handlePageChange" @pageJump="getList"></Pages>
	</el-container>
</template>

<script>
import Pages from "@/components/pages/index.vue";

export default{
	components: {Pages},
	data() {
		return {
			searchForm: {
				name: '',
				page: 1,
				limit: 15
			},
			page: {
				total: 0
			},
			plusIcon: 'el-icon-plus',
			menusIcon: 'el-icon-MoreFilled',
			embedingIcon: 'el-icon-Switch',
			settingIcon: 'el-icon-setting',
			downloadIcon: 'el-icon-Download',
			delIcon: 'el-icon-Delete',
			storeList: []
		}
	},
	methods: {
		getList() {

		},
		handlePageChange() {

		},
		onSubmit() {

		}
	}
}
</script>

<style scoped>
.title {
	font-size: 18px;
	font-weight: bold;
	padding-bottom: 20px;
	border-bottom: 1px solid #f4f4f4;
}
.store-list {
	width: 100%;
}
.store-item {
	height: 100%;
	margin-top: 20px;
	padding: 0 20px;
	cursor: pointer;
}
.add-box {
	height: 170px;
	background: #eff0f1;
	display: flex;
	align-items: center;
	justify-content: center;
}
.add-icon {
	height: 25px;
	width: 25px;
	background: #fff;
	display: flex;
	align-items: center;
	justify-content: center;
}
.title-box {
	width: 100%;
	height: 40px;
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.title-left {
	display: flex;
	height: 40px;
	align-items: center;
	color: #1f2329;
}
.knowledge-title {
	margin-left: 10px;
	width: 250px;
}
.line1 {
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}
.author {
	margin-left: 10px;
	color: #646a73;
}
.title-label {
	background: #5E17EB;
	color: #fff;
	border-radius: 10px;
	height: 30px;
	width: 30px;
	line-height: 30px;
	text-align: center;
}
.add-store-name {
	font-size: 16px;margin-left: 10px
}
.add-box:hover {
	border: 1px dashed #5E17EB;
	background: #fff;
}
.add-box:hover .add-store-name {
	color: #5E17EB;
}
.add-box:hover .add-icon {
	border: 1px solid #5E17EB;
}
.add-box:hover .icon-color {
	color: #5E17EB;
}
.desc-box {
	color: #646a73;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	overflow: hidden;
	padding-top: 20px;
	height: 60px;
}
.tool-box {
	width: 100%;
	height: 40px;
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.tool-box-left {
	display: flex;
	align-items: center;
}
.box-item .num {
	font-weight: bold;
}
.box-item .num-label {
	color: #646a73;
	margin-left: 5px;
}
.add-item-box {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 170px;
}
</style>
