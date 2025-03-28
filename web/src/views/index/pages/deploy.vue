<template>
	<div class="base-div">
		<div class="application-info">
			<div class="application-title">应用信息</div>
			<div class="app-desc">
				<div class="base-info-item">
					<div class="app-title-box">
						<div class="title-label">测</div>
						<div class="app-title">测试的知识库</div>
					</div>
					<div class="base-style">
						是否对外发布
						<el-switch
							style="margin-left: 10px"
							v-model="open"
							:active-value="1"
							:inactive-value="2"
						>
						</el-switch>
					</div>
					<div class="base-style code-bg" style="width: 400px">http://localhost:8090/ui/chat/71c8380fe2196c3a <el-icon size="16px" style="margin-left: 5px"><CopyDocument /></el-icon></div>
					<div class="base-style">
						<el-button type="danger">本地演示</el-button>
						<el-button>三方嵌入</el-button>
					</div>
				</div>
				<div class="base-info-item">
					<h3 style="margin-bottom: 10px">后端服务API</h3>
					API访问凭据
					<div class="base-style code-bg" style="width: 400px">http://localhost:8090/ui/chat/71c8380fe2196c3a <el-icon size="16px" style="margin-left: 5px"><CopyDocument /></el-icon></div>
					<div class="base-style">
						<el-button>API秘钥</el-button>
					</div>
				</div>
			</div>
		</div>
		<div class="census-list">
			<div class="application-title">数据统计</div>
			<div class="flex-center time-select">
				<el-select v-model="days" style="width: 150px">
					<el-option label="过去7天" :value="1" />
					<el-option label="过去30天" :value="2" />
					<el-option label="过去90天" :value="3" />
					<el-option label="过去半年" :value="4" />
					<el-option label="自定义" :value="5" />
				</el-select>
				<div style="width: 200px;margin-left: 20px" v-if="days === 5">
					<el-date-picker
						v-model="dayRange"
						type="daterange"
						unlink-panels
						range-separator="至"
						start-placeholder="开始日期"
						end-placeholder="结束日期"
					/>
				</div>
			</div>
			<div class="flex-center census-list">
				<div class="flex-center census-card">
					<el-avatar shape="square" style="background: rgb(235, 241, 255)">
						<el-icon size="24" color="rgb(51, 112, 255)"><Avatar /></el-icon>
					</el-avatar>
					<div class="info-item">
						<div class="info-title">用户总数</div>
						<span>2</span>
					</div>
				</div>
				<div class="flex-center census-card">
					<el-avatar shape="square" style="background: rgb(255, 243, 229)">
						<el-icon size="24" color="rgb(255, 136, 0)"><ChatLineRound /></el-icon>
					</el-avatar>
					<div class="info-item">
						<div class="info-title">提问次数</div>
						<span>2</span>
					</div>
				</div>
				<div class="flex-center census-card">
					<el-avatar shape="square" style="background: rgb(229, 251, 248)">
						<el-icon size="24" color="rgb(0, 214, 185)"><Key /></el-icon>
					</el-avatar>
					<div class="info-item">
						<div class="info-title">Tokens 总数</div>
						<span>2</span>
					</div>
				</div>
				<div class="flex-center census-card">
					<el-avatar shape="square" style="background: rgb(254, 237, 236)">
						<el-icon size="24" color="rgb(245, 74, 69)"><Star /></el-icon>
					</el-avatar>
					<div class="info-item">
						<div class="info-title">用户满意度</div>

						<div class="flex-center">
							<div class="flex-center">
								<span class="iconfont icon-zan icon-style" style="font-size: 14px"></span>
								<span style="margin-left: 5px">2</span>
							</div>
							<div class="flex-center" style="margin-left: 10px">
								<span class="iconfont icon-cai icon-style" style="font-size: 14px"></span>
								<span style="margin-left: 5px">2</span>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="flex-center" style="justify-content: space-between;flex-wrap: wrap;">
				<div class="census-data-card">
					<scEcharts height="320px" :option="orderOption"></scEcharts>
				</div>
				<div class="census-data-card">
					<scEcharts height="320px" :option="orderOption"></scEcharts>
				</div>
				<div class="census-data-card">
					<scEcharts height="320px" :option="orderOption"></scEcharts>
				</div>
				<div class="census-data-card">
					<scEcharts height="320px" :option="orderOption"></scEcharts>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import {Avatar, ChatLineRound, CopyDocument, Key, Star} from "@element-plus/icons-vue";
import scEcharts from "@/components/scEcharts/index.vue";

export default {
	components: {Star, Key, ChatLineRound, Avatar, CopyDocument, scEcharts},
	data() {
		return {
			open: 1,
			days: 1,
			dayRange: [],
			orderOption: {
				xAxis: {
					type: 'category',
					data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
				},
				yAxis: {
					type: 'value'
				},
				series: [
					{
						data: [820, 932, 901, 934, 1290, 1330, 1320],
						type: 'line',
						smooth: true
					}
				]
			}
		}
	},
	mounted() {

	},
	methods: {}
}
</script>

<style lang="scss" scoped>
.application-info {
	width: 100%;
}
.application-title {
	font-size: 16px;
	padding-left: 10px;
	border-left: 5px solid var(--el-color-theme);
	font-weight: bold;
}
.base-div {
	background: #fff;
	border-radius: 10px;
	padding: 20px;
	height: calc(100vh - 110px);
	overflow-y: scroll;
}

.app-desc {
	height: 200px;
	margin-top: 20px;
	display: flex;
	align-items: center;
	width: 100%;
	border: 1px solid #e4e7ed;
	padding: 20px;
	margin-bottom: 20px;
	border-radius: 5px;
}

.app-title-box {
	height: 32px;
	display: flex;
	align-items: center;

	.app-title {
		margin-left: 5px;
		font-size: 14px;
		font-weight: bold;
	}
}
.base-info-item {
	width: 50%;
	height: 100%;
}
.title-label {
	width: 32px;
	height: 32px;
	border-radius: 5px;
	background: var(--el-color-theme);
	line-height: 32px;
	text-align: center;
	color: #fff;
}
.time-select {
	margin-top: 10px;
}
.census-list {
	width: 100%;
	margin-top: 20px;
	justify-content: space-between;
}
.census-card {
	width: 24%;
	border: 1px solid #e4e7ed;
	height: 85px;
	border-radius: 5px;
	padding: 10px 20px;
}
.info-item {
	margin-left: 20px;
	span {
		font-size: 20px;
		font-weight: 500;
	}
}
.info-title {
	color: #646a73;
	font-weight: 400;
	margin-bottom: 5px;
}
.census-data-card {
	width: 49%;
	border: 1px solid #e4e7ed;
	height: 365px;
	margin-top: 20px;
}
</style>
