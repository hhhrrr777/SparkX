import { register } from '@antv/x6-vue-shape'

export default {
	// 开始节点
	startNode: (x, y) => {
		return {
			x: x,
			y: y,
			shape: 'start-node',
			width: 190,
			height: 40,
			data: {
				checked: false,
				portsVisible: false
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
					{ group: 'rightPorts' } // 将端口分配到右侧分组
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
			width: 190,
			height: 40,
			data: {
				checked: false,
				portsVisible: false
			},
			ports: {
				groups: {
					leftPorts: {
						position: 'left', // 端口位于节点左侧
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
					{ group: 'leftPorts' } // 将端口分配到左侧分组
				]
			}
		}
	}
}

import Start from './node/start.vue'
import End from './node/end.vue'

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
