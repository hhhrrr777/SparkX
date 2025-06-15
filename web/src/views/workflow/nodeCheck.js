export default {
	// 检测节点数据是否缺失
	check(graphData) {
		console.log(11, graphData)
		for (let node of graphData.cells) {
			// 分支节点
			if (node.shape === "switch-node") {

				for (let branch of node.data.ifBranch) {
					for (let item of branch.data) {
						if (item.input == null || item.tips === '' || item.value === '') {
							let name = '';
							if (node.data.no > 1) {
								name = node.data.no
							}
							return {code: -11, msg: "请设置【条件分支" + name + "】的条件", data: []}
						}
					}
				}
			} else if (node.shape === "dataset-node") { // 知识库检索节点
				let name = ''; 
				if (node.data.no > 1) {
					name = node.data.no
				}

				if (node.data.inputData.length === 0) {
					return {code: -12, msg: "请设置【知识检索" + name + "】输入参数", data: []}
				}

				if (node.data.datasets.length === 0) {
					return {code: -13, msg: "请设置【知识检索的" + name + "】关联知识库", data: []}
				}
			}
		}

		return {code: 0, msg: "success", data: []}
	}
}
