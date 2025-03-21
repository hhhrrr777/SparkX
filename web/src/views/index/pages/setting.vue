<template>
	<div style="background: #fff;border-radius: 10px;padding: 10px 5px">
		<el-row style="min-height: calc(100vh - 160px);">
			<el-col :span="10">

			</el-col>
			<el-col :span="14" style="background: #f4f4f4;padding: 10px;">
				<chat-box></chat-box>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import chatBox from '@/components/chatContent/index.vue'

export default {
	components: {chatBox},
	data() {
		return {

		}
	},
	mounted() {
	},
	methods: {

	}
}
</script>

<style scoped>

</style>
