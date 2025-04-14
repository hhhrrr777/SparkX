export default {
	// 遍历节点
	getPreviousNodes(currentNode, graph) {
		// 获取画布中所有边
		const edges = graph.getEdges();
		let nodesArr = []
		let findNodeData = findNode(currentNode)
		while (findNodeData.length > 0) {
			nodesArr.push(findNodeData[0])
			findNodeData = findNode(findNodeData[0])
		}

		function findNode(currentNode) {
			return edges
				.filter(edge => edge.getTargetNode().id === currentNode.id)
				.map(edge => edge.getSourceNode())
		}

		return nodesArr
	},
	// 获取节点的输入数据
	getNodeInputData(nowNode, graph) {

		let nodeInputData = []
		const inputParams = this.getPreviousNodes(nowNode, graph)

		function formatData(data) {
			return data.map(item => {
				return {
					label: item.name,
					value: item.field
				}
			})
		}

		inputParams.forEach(param => {
			const data = param.getData()
			if (data.pages === 'start') {

				nodeInputData.push({
					value: nowNode.id,
					label: '开始',
					icon: 'iconfont icon-ai23',
					color: 'var(--el-color-theme)',
					children: formatData(data.sysData.concat(data.userData))
				})
			} else if (data.pages === 'purpose') {

				let label = '意图分类'
				if (data.no > 1) {
					label += data.no
				}

				nodeInputData.push({
					value: nowNode.id,
					label: label,
					icon: 'iconfont icon-fenlei',
					color: '#f79009',
					children: formatData(data.outData)
				})
			}
		})

		nodeInputData.reverse()

		return nodeInputData
	}
}
