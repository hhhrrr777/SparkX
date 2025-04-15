import { register } from '@antv/x6-vue-shape'
import initConfig from "@/views/workflow/initConfig.js"

export default {
	// 开始节点
	startNode: (x, y) => {
		return {
			x: x,
			y: y,
			shape: 'start-node',
			width: 230,
			height: 40,
			data: {
				pages: 'start',
				checked: false,
				portsVisible: false,
				sysData: initConfig.startData.sysData,
				userData: [],
				// 输出参数
				outData: initConfig.startData.sysData,
			},
			ports: {
				groups: {
					rightPorts: {
						position: 'right', // 端口位于节点右侧
						attrs: {
							circle: {
								style: {visibility: 'hidden'},
								r: 4,          // 端口半径
								magnet: true,  // 启用磁吸
								stroke: 'var(--el-color-theme)', // 边框颜色
								strokeWidth: 1, // 边框宽度
								fill: '#fff'    // 填充颜色
							}
						}
					},
				},
				items: [
					{ group: 'rightPorts', type: 'output' } // 将端口分配到右侧分组
				]
			}
		}
	},
	// 结束节点
	endNode: (x, y) => {
		return {
			x: x,
			y: y,
			shape: 'end-node',
			width: 230,
			height: 40,
			data: {
				pages: 'end',
				checked: false,
				portsVisible: false
			},
			ports: {
				groups: {
					leftPorts: {
						...initConfig.leftPorts,
					}
				},
				items: [
					{ group: 'leftPorts', type: 'input' } // 将端口分配到左侧分组
				]
			}
		}
	},
	// 意图分类
	purposeNode: (x, y, no) => {
		return {
			x: x,
			y: y,
			shape: 'purpose-node',
			width: 230,
			height: 40,
			data: {
				no: no,
				pages: 'purpose',
				checked: false,
				portsVisible: false,
				...initConfig.purposeData,
			},
			ports: {
				groups: {
					leftPorts: {
						...initConfig.leftPorts,
					},
					rightPorts: {
						attrs: {
							circle: {
								style: {visibility: 'hidden'},
								r: 4,          // 端口半径
								magnet: true,  // 启用磁吸
								stroke: 'var(--el-color-theme)', // 边框颜色
								strokeWidth: 1, // 边框宽度
								fill: '#fff'    // 填充颜色
							}
						},
						position: {
							name: 'absolute',
						}
					}
				},
				items: [
					{ group: 'leftPorts', type: 'input' }, // 将端口分配到左侧分组
					{
						group: 'rightPorts',
						args: { x: 230, y: 100 },
					 	type: 'output'
					}
				]
			}
		}
	},
	// LLM节点
	llmNode: (x, y, no) => {
		console.log({
			x: x,
			y: y,
			shape: 'llm-node',
			width: 230,
			height: 40,
			data: {
				no: no,
				pages: 'llm',
				checked: false,
				portsVisible: false,
				...initConfig.llmData,
			},
			ports: {
				...initConfig.ports
			}
		})
		return {
			x: x,
			y: y,
			shape: 'llm-node',
			width: 230,
			height: 40,
			data: {
				no: no,
				pages: 'llm',
				checked: false,
				portsVisible: false,
				...initConfig.llmData,
			},
			ports: {
				...initConfig.ports
			}
		}
	},
	// 知识检索节点
	datasetNode: (x, y, no) => {
		return {
			x: x,
			y: y,
			shape: 'dataset-node',
			width: 230,
			height: 40,
			data: {
				no: no,
				pages: 'dataset',
				checked: false,
				portsVisible: false,
				...initConfig.datasetData,
			},
			ports: {
				...initConfig.ports
			}
		}
	},
}

import Start from './node/start.vue'
import End from './node/end.vue'
import Purpose from './node/purpose.vue'
import Llm from './node/llm.vue'
import Dataset from './node/dataset.vue'

// 制作组件节点
register({
	shape: 'start-node',
	width: 100,
	height: 100,
	component: Start,
})

register({
	shape: 'end-node',
	width: 100,
	height: 100,
	component: End,
})

register({
	shape: 'purpose-node',
	width: 100,
	height: 100,
	component: Purpose,
})

register({
	shape: 'llm-node',
	width: 100,
	height: 100,
	component: Llm,
})

register({
	shape: 'dataset-node',
	width: 100,
	height: 100,
	component: Dataset,
})
