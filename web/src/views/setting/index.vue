<template>
	<el-container style="padding: 20px">
		<el-card>
			<div class="title">系统设置</div>
			<el-row class="tac">
				<el-col :span="2">
					<div class="menu">
						<el-menu
							style="background: #f5f5f5"
							default-active="1">
							<el-menu-item index="1">
								<el-icon>
									<component :is="userIcon"/>
								</el-icon>
								<span>用户设置</span>
							</el-menu-item>
							<el-menu-item index="2">
								<el-icon>
									<component :is="userIcon"/>
								</el-icon>
								<span>团队管理</span>
							</el-menu-item>
							<el-menu-item index="3">
								<el-icon>
									<component :is="filesIcon"/>
								</el-icon>
								<span>模型管理</span>
							</el-menu-item>
						</el-menu>
					</div>
				</el-col>
				<el-col :span="22">
					<div class="pages"></div>
				</el-col>
			</el-row>
		</el-card>
	</el-container>
</template>

<script>
export default {
	data() {
		return {
			userIcon: 'el-icon-user',
			filesIcon: 'el-icon-files'
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
	.tac {
		width: 100%;
		height: calc(100vh - 100px);
	}
	.menu {
		width: 100%;
		height: 400px;
		padding: 20px;
		background: #f5f5f5;
		border-radius: 10px;
		margin-top: 40px;
	}
	.pages {
		width: 100%;
		height: 100%;
		padding: 20px;
	}
</style>
