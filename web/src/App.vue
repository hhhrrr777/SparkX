<template>
	<el-config-provider :locale="locale" :size="config.size" :zIndex="config.zIndex" :button="config.button">
		<router-view></router-view>
	</el-config-provider>
</template>

<script>
import '@/assets/alifont/iconfont.css'
import colorTool from '@/utils/color'

export default {
	name: 'App',
	data() {
		return {
			config: {
				size: "default",
				zIndex: 2000,
				button: {
					autoInsertSpace: false
				}
			}
		}
	},
	computed: {
		locale(){
			return this.$i18n.messages[this.$i18n.locale].el
		},
	},
	created() {
		//设置主题颜色
		const app_color = this.$CONFIG.COLOR || this.$TOOL.data.get('APP_COLOR')
		if(app_color){
			document.documentElement.style.setProperty('--el-color-primary', app_color);
			for (let i = 1; i <= 9; i++) {
				document.documentElement.style.setProperty(`--el-color-primary-light-${i}`, colorTool.lighten(app_color,i/10));
			}
			for (let i = 1; i <= 9; i++) {
				document.documentElement.style.setProperty(`--el-color-primary-dark-${i}`, colorTool.darken(app_color,i/10));
			}
		}
	}
}
</script>

<style lang="scss">
body {
	font-family: PingFang SC, AlibabaPuHuiTi !important;
}
.line1 {
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}
.base-style {
	display: flex;
	align-items: center;
	margin-top: 10px;
}
.flex-center {
	display: flex;
	align-items: center;
}
.flex-center-all {
	display: flex;
	align-items: center;
	justify-content: center;
}
.el-card {
	border-radius: 10px !important;
}
.code-bg {
	background: #e2e2e2;
	padding: 10px;
	color: #303133;
	border-radius: 5px;
}
@import '@/style/style.scss';
</style>
