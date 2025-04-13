<template>
	<div style="width:100%;height:100%;position: relative">
		<top-menu class="top-menu"></top-menu>
		<div ref="containerRef" class="container"/>

		<menu-box v-if="visible" class="add-menu-box"></menu-box>
		<bottom-menu
			:key="randomKey"
			:out-open="outOpen"
			class="bottom-menu"
			@open-menu="openMenuHandle"
			@reset="resetHandle"
			@zoom-in="zoomInHandle"
			@zoom-out="zoomOutHandle">
		</bottom-menu>

		<!-- 菜单设置 -->
		<el-drawer
			:size="600"
			v-model="drawer"
			append-to-body
			destroy-on-close>
			<div class="pages">
				<Suspense>
					<template #default>
						<component
							:form-data="formData"
							@data-change="dataChangeHandle"
							:is="page"
						/>
					</template>
					<template #fallback>
						<el-skeleton :rows="3" />
					</template>
				</Suspense>
			</div>
		</el-drawer>
	</div>
</template>
<script>
// 引入 AntV X6 库
import { Graph, Shape } from '@antv/x6'
import defaultNodeConfig from './node.js'
import bottomMenu from './menu/bottomMenu.vue'
import topMenu from './menu/topMenu.vue'
import menuBox from './menu/menuBox.vue'
import {defineAsyncComponent} from "vue";

export default {
	components: {
		bottomMenu,
		topMenu,
		menuBox
	},
	mounted() {
		this.initGraph()
	},
	data() {
		return {
			nowNode: null,
			visible: false,
			graph: null,
			outOpen: false,
			randomKey: Math.random(),
			drawer: false,
			// 当前页面
			page: '',
			// 设置页面
			pages: {
				startDialog: defineAsyncComponent(() => import('./dialog/startDialog.vue')),
				purposeDialog: defineAsyncComponent(() => import('./dialog/purposeDialog.vue')),
			},
			formData: {} // 配置数据
		}
	},
	methods: {
		// 初始化
		initGraph() {
			const containerRef = this.$refs.containerRef;
			// 初始化 Graph 对象
			const graph = new Graph({
				container: containerRef, // 容器元素
				selecting: true,
				history: true, // 启动历史记录
				interacting: {
					nodeMovable: true, // 可拖拽节点
					edgeMovable: false // 可拖拽边
				},
				background: {
					color: '#f4f4f4',
				},
				// 网格
				grid: {
					visible: true
				},
				// Scroller 使画布具备滚动、平移、居中、缩放等能力
				scroller: {
					enabled: true,
					pageVisible: true,
					pageBreak: true,
					pannable: true,
				},
				connecting: {
					connector: 'smooth',
					snap: true, // 自动吸附
					allowBlank: false, // 是否允许连接到画布空白位置的点
					allowLoop: false, // 是否允许创建循环连线，即边的起始节点和终止节点为同一节点
					allowNode: false, // 是否允许边链接到节点（非节点上的链接桩）
					createEdge() {
						return new Shape.Edge({
							attrs: {
								line: {
									stroke: '#d0d5dc',
									strokeWidth: 2,    // 设置连接线宽度
									targetMarker: null, // 去掉终点箭头
									sourceMarker: null, // 去掉起点箭头
								}
							},
							// 添加工具（删除按钮）
							tools: [],
						});
					}
				}
			})

			this.graph = graph

			// 创建组件节点
			let startNode = defaultNodeConfig.startNode(100, 240)
			graph.addNode(startNode)

			let endNode = defaultNodeConfig.endNode(900, 240)
			graph.addNode(endNode)

			// 意图分类
			let purposeNode = defaultNodeConfig.purposeNode(500, 240)
			graph.addNode(purposeNode)

			// 节点移入
			graph.on('node:mouseenter', () => {
				setVisible('visible')
			})

			// 节点点击
			graph.on('node:click', ({ node }) => {
				resetSel()

				this.nowNode = node
				node.updateData({checked: true})

				this.formData = node.getData()
				if (this.formData.pages === 'start') {
					this.page = this.pages.startDialog
				} else if (this.formData.pages === 'purpose') {
					this.page = this.pages.purposeDialog
				}
				this.drawer = true
			})

			// 点击空白处
			graph.on('blank:click', () => {
				resetSel()

				this.outOpen = false
				this.randomKey = Math.random()
				this.visible = false
				this.nowNode = null
				setVisible('hidden')
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
					const ports = document.querySelectorAll(".x6-port-body")
					for (let i = 0, len = ports.length; i < len; i = i + 1) {
						ports[i].style.visibility = visibility;
					}
				}, 100)
			}

			function resetSel() {
				graph.getNodes().forEach(node => {
					node.updateData({checked: false})
				})
			}
		},
		// 重新布局
		resetHandle() {
			this.graph.centerContent();
			this.graph.zoom(0);
		},
		// 放大
		zoomInHandle() {
			this.graph.zoom(0.1);
			this.canZoomOut = true;
		},
		// 缩小
		zoomOutHandle() {
			if (!this.canZoomOut) return;
			const Num = Number(this.graph.zoom().toFixed(1));

			if (Num > 0.1) {
				this.graph.zoom(-0.1);
			} else {
				this.canZoomOut = false;
			}
		},
		// 操作组件菜单
		openMenuHandle(visible) {
			this.visible = visible
		},
		// 节点内部设置
		dataChangeHandle(val) {
			console.log('xxx', val)
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
.add-menu-box {
	position: absolute;
	bottom: 70px;
	left: 100px;
}
</style>
