<template>
	<el-collapse :expand-icon-position="position">
		<el-collapse-item title="Consistency" name="1" icon="CaretRight">
			<div>
				Consistent with real life: in line with the process and logic of real
				life, and comply with languages and habits that the users are used to;
			</div>
			<div>
				Consistent within interface: all elements should be consistent, such
				as: design style, icons and texts, position of elements, etc.
			</div>
		</el-collapse-item>
		<el-collapse-item title="Feedback" name="2" icon="CaretRight">
			<div>
				Operation feedback: enable the users to clearly perceive their
				operations by style updates and interactive effects;
			</div>
			<div>
				Visual feedback: reflect current state by updating or rearranging
				elements of the page.
			</div>
		</el-collapse-item>
		<el-collapse-item title="Efficiency" name="3" icon="CaretRight">
			<div>
				Simplify the process: keep operating process simple and intuitive;
			</div>
			<div>
				Definite and clear: enunciate your intentions clearly so that the
				users can quickly understand and make decisions;
			</div>
			<div>
				Easy to identify: the interface should be straightforward, which helps
				the users to identify and frees them from memorizing and recalling.
			</div>
		</el-collapse-item>
	</el-collapse>
</template>

<script>
import { CaretRight } from '@element-plus/icons-vue'

export default {
	components: {
		CaretRight
	},
	data() {
		return {
			position: "left"
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
