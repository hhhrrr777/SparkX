<template>
	<div id="container" class="container"></div>

	<div class="operating">
		<el-tooltip
			class="item"
			effect="light"
			content="撤销"
			placement="bottom"
		>
			<i
				class="el-icon-refresh-left"
				:class="{ opacity: !canUndo }"
			></i>
		</el-tooltip>
		<el-tooltip
			class="item"
			effect="light"
			content="重做"
			placement="bottom"
		>
			<i
				class="el-icon-refresh-right"
				:class="{ opacity: !canRedo }"
			></i>
		</el-tooltip>
		<el-tooltip
			class="item"
			effect="light"
			content="放大"
			placement="bottom"
		>
			<i class="el-icon-zoom-in" @click="zoomInFn"></i>
		</el-tooltip>
		<el-tooltip
			class="item"
			effect="light"
			content="缩小"
			placement="bottom"
		>
			<i
				class="el-icon-zoom-out"
				:class="{ opacity: !canZoomOut }"
			></i>
		</el-tooltip>
		<el-tooltip
			class="item"
			effect="light"
			content="重置"
			placement="bottom"
		>
			<i class="el-icon-full-screen"></i>
		</el-tooltip>
		<el-tooltip
			class="item"
			effect="light"
			content="保存"
			placement="bottom"
		>
			<i class="el-icon-document-add"></i>
		</el-tooltip>
	</div>
</template>

<script>
import { Graph } from '@antv/x6';
export default {
	data() {
		return {
			canUndo: false,
			canRedo: false,
			canZoomOut: false
		}
	},
	mounted() {
		this.init()
	},
	methods: {

		init() {
			const data = {
				// 节点
				nodes: [
					{
						id: 'node1', // String，可选，节点的唯一标识
						x: 40,       // Number，必选，节点位置的 x 值
						y: 40,       // Number，必选，节点位置的 y 值
						width: 80,   // Number，可选，节点大小的 width 值
						height: 40,  // Number，可选，节点大小的 height 值
						label: 'hello', // String，节点标签
					},
					{
						id: 'node2', // String，节点的唯一标识
						x: 160,      // Number，必选，节点位置的 x 值
						y: 180,      // Number，必选，节点位置的 y 值
						width: 80,   // Number，可选，节点大小的 width 值
						height: 40,  // Number，可选，节点大小的 height 值
						label: 'world', // String，节点标签
					},
				],
				// 边
				edges: [
					{
						source: 'node1', // String，必须，起始节点 id
						target: 'node2', // String，必须，目标节点 id
					},
				],
			};

			const graph = new Graph({
				container: document.getElementById('container'),
				background: {
					color: '#f4f4f4', // 设置画布背景颜色
				},
				grid: {
					size: 10,      // 网格大小 10px
					visible: true, // 渲染网格背景
				},
			});

			graph.fromJSON(data)
		}
	}
}
</script>

<style scoped>
.container {
	width: 100%;
	height: 100vh;
}
.operating {
	position: absolute;
	left: 160px;
	bottom: 20px;
	z-index: 999;
	background-color: #ffffff;
	padding: 10px;
	box-shadow: 1px 1px 4px 0 #0a0a0a2e;
	i {
		font-size: 24px;
		cursor: pointer;
		margin: 0 10px;
		color: #515a6e;
		&:hover {
			color: #2d8cf0;
		}
		&.opacity {
			opacity: 0.5;
		}
	}
}
</style>
