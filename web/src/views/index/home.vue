<template>
	我是首页<button @click="open1">点击</button>
</template>

<script>

import { ElMessage } from 'element-plus'
export default {
	data() {
		return {
		}
	},
	mounted() {
		this.$message.success('res')
	},
	methods: {
		open1() {
			ElMessage.error("这是一个错误")
		}
	}
}
</script>

<style scoped>

</style>
