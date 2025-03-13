<template>
    <div style="background: #fff;border-radius: 10px;padding: 10px 5px">
        <div class="search-box">
            <div>
                <el-button icon="el-icon-plus" style="margin-top: -10px;" type="primary" @click="add">创建问题
                </el-button>
                <el-button :disabled="selectedQuestionIds.length === 0" icon="el-icon-Setting"
                           style="margin-top: -10px;"
                           type="primary"
                           @click="setting">关联分段
                </el-button>
                <el-button :disabled="selectedQuestionIds.length === 0" icon="el-icon-Delete" style="margin-top: -10px;"
                           type="primary"
                           @click="del">删除
                </el-button>
            </div>

            <el-form :inline="true" :model="searchForm" class="demo-form-inline">
                <el-form-item>
                    <el-input v-model="searchForm.content" clearable placeholder="问题信息"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button icon="el-icon-search" type="primary" @click="onSubmit">查询</el-button>
                </el-form-item>
            </el-form>
        </div>

        <div style="border-radius: 10px;background: #fff;padding: 0 5px 5px 5px">
            <el-table
                :data="tableData"
                :header-cell-style="{background:'#f4f4f4', color:'#646a73'}"
                style="width: 100%"
                @selection-change="handleSelectionChange">
                <el-table-column
                    type="selection"
                    width="55">
                </el-table-column>
                <el-table-column
                    label="问题">
                    <template #default="scope">
                        <span style="cursor: pointer" @click="showQuestion(scope.row)">{{ scope.row.content }}</span>
                        <el-icon style="margin-left: 5px;cursor: pointer">
                            <component :is="editIcon" />
                        </el-icon>
                    </template>
                </el-table-column>
                <el-table-column
                    label="分段数"
                    prop="linkNum"
                    width="80">
                </el-table-column>
                <el-table-column
                    width="160"
                    label="创建时间">
                    <template #default="scope">
                        {{ scope.row.createTime.replace('T', " ") }}
                    </template>
                </el-table-column>
                <el-table-column
                    width="160"
                    label="更新时间">
                    <template #default="scope">
                        {{ scope.row.updateTime && scope.row.updateTime.replace('T', " ") }}
                    </template>
                </el-table-column>
                <el-table-column
                    label="操作"
                    prop="operation"
                    width="120">
                    <template #default="scope">
                        <div style="display: flex;align-items: center;color: #5E17EB;cursor: pointer">
                            <div style="margin-right: 8px;display: flex;align-items: center" @click="linkParagraph(scope.row)">
                                <el-tooltip class="item" content="关联">
                                    <el-icon size="14">
                                        <component :is="linkIcon" />
                                    </el-icon>
                                </el-tooltip>
                            </div>
                            <div style="margin-right: 8px;display: flex;align-items: center">
                                <el-tooltip class="item" content="编辑">
                                    <el-icon size="14">
                                        <component :is="editIcon" />
                                    </el-icon>
                                </el-tooltip>
                            </div>
                            <div style="margin-right: 8px;display: flex;align-items: center">
                                <el-tooltip class="item" content="删除">
                                    <el-icon size="14">
                                        <component :is="delIcon" />
                                    </el-icon>
                                </el-tooltip>
                            </div>
                        </div>

                    </template>
                </el-table-column>
            </el-table>
        </div>

        <Pages :form="searchForm" :page-obj="page" @pageChange="handlePageChange" @pageJump="getList"></Pages>
    </div>

    <!-- 添加问题 -->
    <el-dialog title="创建问题" v-model="dialogVisible" width="1000px" ref="saveDialog" :close-on-click-modal="false">
        <el-form :model="form" label-width="10px">
            <el-form-item>
                <el-input type="textarea" :rows="8" v-model="form.content"  placeholder="请输入问题，支持输入多个，一行一个。" style="width: 100%"></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="optSubmit()" :loading="loading">确 定</el-button>
            </div>
        </template>
    </el-dialog>

	<!-- 关联问题 -->
	<el-dialog title="关联分段" v-model="linkVisible" width="1000px" ref="save2Dialog" :close-on-click-modal="false">
		<link-paragraph :dataset-id="linkForm.datasetId" :question-id="linkForm.questionId" :key="randomKey"></link-paragraph>
	</el-dialog>
</template>

<script>
import Pages from "@/components/pages/index.vue";
import linkParagraph from "@/components/linkParagraph/index.vue";

export default {
    components: {Pages, linkParagraph},
    data() {
        return {
            searchForm: {
                datasetId: "",
                content: "",
                page: 1,
                limit: 10
            },
            selectedQuestionIds: [],
            tableData: [],
            page: {
                total: 0
            },
            dialogVisible: false,
            form: {
                datasetId: "",
                content: ""
            },
            loading: false,
            editIcon: "el-icon-edit",
            linkIcon: "el-icon-link",
            delIcon: "el-icon-delete",
			linkVisible: false,
			linkForm: {
				questionId: "",
				datasetId: ""
			},
			randomKey: 0
        }
    },
    mounted() {
        this.searchForm.datasetId = this.$route.query.datasetId
        this.form.datasetId = this.$route.query.datasetId
        this.getList();
    },
    methods: {
        // 获取列表
        async getList() {
            let res = await this.$API.question.list.get(this.searchForm)
            this.tableData = res.data.data
            this.page.total = res.data.total
        },
        onSubmit() {
            this.getList()
        },
        // 创建问题
        add() {
            this.dialogVisible = true
        },
        // 关联分段
        setting() {

        },
        // 批量删除
        del() {

        },
        // 显示问题
        showQuestion(row) {

        },
        // 添加问题
        async optSubmit() {
            let res = await this.$API.question.add.post(this.form)
            if (res.code === 0) {
                this.$message.success(res.msg)
                this.dialogVisible = false
                this.getList()
            } else {
                this.$message.error(res.msg)
            }
        },
        // 勾选
        handleSelectionChange(row) {
            this.selectedQuestionId = []
            row.forEach(item => {
                this.selectedQuestionId.push(item.question_id)
            })
        },
        // 翻页
        handlePageChange(page) {
            this.searchForm.page = page;
            this.getList();
        },
		// 链接
		linkParagraph(row) {
			this.randomKey = Math.random()
			this.linkForm.datasetId = this.$route.query.datasetId
			this.linkForm.questionId = row.questionId
			this.linkVisible = true
		}
    }
}
</script>

<style scoped>
.el-table .cell {
    font-size: 13px; /* 或者你想要的任何大小 */
    color: #172329;
}
</style>
