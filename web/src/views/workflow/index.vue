<template>
	<div style="width:100%;height:100%;position: relative">
		<top-menu class="top-menu"></top-menu>
		<div id="container" class="container"/>
		<bottom-menu class="bottom-menu"></bottom-menu>
	</div>
</template>
<script>
// 引入 AntV X6 库
import { Graph, Shape } from '@antv/x6'
import defaultNodeConfig from './node.js'
import bottomMenu from './menu/bottomMenu.vue'
import topMenu from './menu/topMenu.vue'

export default {
	components: {
		bottomMenu,
		topMenu
	},
	mounted() {
		this.initGraph()
	},
	data() {
		return {
			nowNode: null
		}
	},
	methods: {
		initGraph() {
			// 初始化 Graph 对象
			const graph = new Graph({
				container: document.getElementById('container'), // 容器元素
				interacting: {
					nodeMovable: true, // 可拖拽节点
					edgeMovable: false // 可拖拽边
				},
				background: {
					color: '#f4f4f4',
				},
				grid: {
					visible: true
				},
				connecting: {
					connector: 'smooth',
					allowBlank: false, // 不允许链接空白处
					connectionPoint: 'anchor', // 连接中心锚点
					snap: true, // 自动吸附
					createEdge() {
						return new Shape.Edge({
							attrs: {
								line: {
									stroke: '#d0d5dc',
									strokeWidth: 2,    // 设置连接线宽度
									targetMarker: null, // 去掉终点箭头
									sourceMarker: null, // 去掉起点箭头
								},
							},
							// 添加工具（删除按钮）
							tools: [],
						});
					}
				}
			})

			// 创建组件节点
			let startNode = defaultNodeConfig.startNode(100, 240)
			const branchNode = graph.addNode(startNode)

			let endNode = defaultNodeConfig.endNode(500, 240)
			const branchNode2 = graph.addNode(endNode)

			// 节点移入
			graph.on('node:mouseenter', ({ node }) => {
				setVisible('visible')
			})

			// 节点点击
			graph.on('node:click', ({ node }) => {
				this.nowNode = node
				node.updateData({checked: true})
			})

			// 点击空白处
			graph.on('blank:click', () => {
				if (this.nowNode)  {
					this.nowNode.updateData({checked: false})
					this.nowNode = null
					setVisible('hidden')
				}
			})

			// 节点移出
			graph.on('node:mouseleave', () => {
				if (!this.nowNode) {
					setVisible('hidden')
				}
			})

			// 连接线移入
			graph.on('edge:mouseenter', ({ edge }) => {
				edge.addTools([
					{ name: 'button-remove' }
				])
				edge.attr('line', { stroke: 'var(--el-color-theme)', strokeWidth: 1 })
			})

			// 连接线移出
			graph.on('edge:mouseleave', ({ edge }) => {
				edge.removeTools()
				edge.attr('line', { stroke: '#d0d5dc', strokeWidth: 2 })
			})

			// 删除连线
			graph.on('edge:tool:click', ({ edge, tool }) => {
				if (tool.name === 'button-remove') {
					// 删除连线
					graph.removeCell(edge);
				}
			})

			function setVisible(visibility) {
				setTimeout(() => {
					const ports = document.querySelectorAll(".x6-port-body");
					for (let i = 0, len = ports.length; i < len; i = i + 1) {
						ports[i].style.visibility = visibility;
					}
				}, 100)
			}
		}
	}
}
</script>
<style scoped>
.container {
	width: 100%;
	height: 100vh;
}
.bottom-menu {
	position: absolute;
	bottom: 20px;
	left: 20px;
	cursor: pointer;
}
.top-menu {
	position: absolute;
	top: 0;
	z-index: 999;
}
</style>
