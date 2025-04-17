<template>
	<div style="width:100%;height:100%;position: relative">
		<top-menu
			class="top-menu"
			@debug="debugHandle"
		>
		</top-menu>
		<div ref="containerRef" class="container"/>

		<menu-box
			v-if="visible"
			@add-node="addNodeHandle"
			class="add-menu-box">
		</menu-box>

		<bottom-menu
			:key="randomKey"
			:out-open="outOpen"
			class="bottom-menu"
			@open-menu="openMenuHandle"
			@center="centerHandle"
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
							:key="randomKey"
							:form-data="formData"
							@port-del="portDelHandle"
							@port-add="portAddHandle"
							@port-update="portUpdate"
							@data-change="dataChangeHandle"
							:input-options="inputOptions"
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
import {Graph, Shape} from '@antv/x6'
import defaultNodeConfig from './node.js'
import bottomMenu from './menu/bottomMenu.vue'
import topMenu from './menu/topMenu.vue'
import menuBox from './menu/menuBox.vue'
import {defineAsyncComponent} from "vue";
import inputDataUtil from './inputData.js'

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
				llmDialog: defineAsyncComponent(() => import('./dialog/llmDialog.vue')),
				datasetDialog: defineAsyncComponent(() => import('./dialog/datasetDialog.vue')),
				answerDialog: defineAsyncComponent(() => import('./dialog/answerDialog.vue')),
				switchDialog: defineAsyncComponent(() => import('./dialog/switchDialog.vue')),
			},
			formData: {}, // 配置数据
			inputOptions: [], // 入参
			nodeNoData: { // 页面中不同组件的数量
				purpose: 0,
				agent: 0,
				answer: 0,
				llm: 0,
				dataset: 0,
				switch: 0
			},
		}
	},
	methods: {
		// 初始化
		initGraph() {
			const containerRef = this.$refs.containerRef;
			const that = this;
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
					allowMulti: false, // 不允许想同的期间和中间直接连接多条线
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
					},
					allowPort(arg) { // 验证是否可以连接
						// 通用函数，根据端口 ID 查找端口类型
						const getPortType = (ports, portId) => {
							const port = ports.find(port => port.id === portId);
							return port ? port.type : null;
						};

						const sourcePortType = getPortType(arg.sourceCell.port.ports, arg.sourcePort);
						const targetPortType = getPortType(arg.targetCell.port.ports, arg.targetPort);

						// 不允许反向链接
						if (sourcePortType === 'input') {
							return false;
						}

						// 相同类型的节点不允许连接
						if (sourcePortType === targetPortType) {
							return false;
						}

						return true
					}
				}
			})

			this.graph = graph

			// 创建组件节点
			let startNodeData = defaultNodeConfig.startNode(100, 240)
			graph.addNode(startNodeData)

			let endNodeData = defaultNodeConfig.endNode(900, 240)
			graph.addNode(endNodeData)

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
				} else if (this.formData.pages === 'llm') {
					this.page = this.pages.llmDialog
				} else if (this.formData.pages === 'dataset') {
					this.page = this.pages.datasetDialog
				} else if (this.formData.pages === 'answer') {
					this.page = this.pages.answerDialog
				} else if (this.formData.pages === 'switch') {
					this.page = this.pages.switchDialog
				}

				if (this.formData.pages !== 'start'
					&& this.formData.pages !== 'end') {
					// 计算节点前的数据
					this.getNodeInputData()
				}

				this.randomKey = Math.random()
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
		// 居中布局
		centerHandle() {
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
			const num = Number(this.graph.zoom().toFixed(1));

			if (num > 0.1) {
				this.graph.zoom(-0.1);
			} else {
				this.canZoomOut = false;
			}
		},
		// 操作组件菜单
		openMenuHandle(visible) {
			this.visible = visible
		},
		// 连接桩增加
		portAddHandle(val) {
			if (val.type === 'purpose') {
				let len = val.cateList.length
				let y = (len - 1) * 40 + 100
				this.nowNode.addPort({ group: 'rightPorts', args: { x: 230, y: y }, type: 'output'})
			} else if (val.type === 'switch') {
				let ports = this.nowNode.port.ports
				let data = this.nowNode.store.data.data.ifBranch
				let y = 0
				let totalNodes = data[data.length - 1].data.length
				if (ports.length === 3) {
					y = ports[1].args.y + totalNodes * 50 + 40
				} else {
					y = ports[ports.length - 1].args.y + totalNodes * 50 + (ports.length - 2) * 20
					console.log('ports', ports, 'y', ports[ports.length - 1].args.y, 'ifBranch', data)
				}

				this.nowNode.addPort({ group: 'rightPorts', args: { x: 230, y: y }, type: 'output'})
				this.portUpdate(val)
			}
		},
		// 连接桩更新
		portUpdate(val) {

			if (val.type === 'switch') {

				// 更新else节点的位置
				let data = this.nowNode.store.data.data.ifBranch
				let totalNodes = data[data.length - 1].data.length

				let ports = this.nowNode.port.ports
				this.nowNode.port.ports[2].args.y = ports[ports.length - 1].args.y + totalNodes * 50 + (ports.length - 2) * 20
				this.nowNode.setPropByPath('ports/items', this.nowNode.port.ports)
			}
		},
		// 连接桩删除
		portDelHandle() {
			const ports = this.nowNode.getPorts()
			if (ports.length) {
				this.nowNode.removePortAt(ports.length - 1)
			}
		},
		// 节点内部设置
		dataChangeHandle(val) {
			this.nowNode.updateData(val)
		},
		// 调试链接
		debugHandle() {

		},
		// 获取节点前数据
		getNodeInputData() {
			this.inputOptions = inputDataUtil.getNodeInputData(this.nowNode, this.graph)
		},
		// 添加节点
		addNodeHandle(type) {

			function getRandomInt(min, max) {
				return Math.floor(Math.random() * (max - min + 1)) + min;
			}

			if (type === 'purpose') {
				this.nodeNoData.purpose += 1
				this.graph.addNode(JSON.parse(JSON.stringify(defaultNodeConfig.purposeNode(getRandomInt(300, 600),
					getRandomInt(300, 600), this.nodeNoData.purpose))))
			} else if (type === 'llm') {
				this.nodeNoData.llm += 1
				this.graph.addNode(JSON.parse(JSON.stringify(defaultNodeConfig.llmNode(getRandomInt(300, 600),
					getRandomInt(300, 600), this.nodeNoData.llm))))
			} else if (type === 'dataset') {
				this.nodeNoData.dataset += 1
				this.graph.addNode(JSON.parse(JSON.stringify(defaultNodeConfig.datasetNode(getRandomInt(300, 600),
					getRandomInt(300, 600), this.nodeNoData.dataset))))
			} else if (type === 'answer') {
				this.nodeNoData.answer += 1
				this.graph.addNode(JSON.parse(JSON.stringify(defaultNodeConfig.answerNode(getRandomInt(300, 600),
					getRandomInt(300, 600), this.nodeNoData.answer))))
			} else if (type === 'switch') {
				this.nodeNoData.switch += 1
				this.graph.addNode(JSON.parse(JSON.stringify(defaultNodeConfig.switchNode(getRandomInt(300, 600),
					getRandomInt(300, 600), this.nodeNoData.switch))))
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
.add-menu-box {
	position: absolute;
	bottom: 70px;
	left: 100px;
}
</style>
