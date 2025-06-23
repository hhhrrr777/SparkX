export default {
	// 检测节点数据是否缺失
	check(graphData) {
		console.log(11, graphData)
		for (let node of graphData.cells) {

			if (node.shape === "switch-node") { // 分支节点

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
					return {code: -12, msg: "请设置【知识检索" + name + "】的输入参数", data: []}
				}

				if (node.data.datasets.length === 0) {
					return {code: -13, msg: "请设置【知识检索" + name + "】的关联知识库", data: []}
				}
			} else if (node.shape === "answer-node") { // 回复节点

				let name = '';
				if (node.data.no > 1) {
					name = node.data.no
				}

				if (node.data.answerType === 1 && node.data.inputData.length === 0) {
					return {code: -14, msg: "请设置【回复节点" + name + "】的输入参数", data: []}
				}

				if (node.data.answerType === 2 && node.data.answer === '') {
					return {code: -15, msg: "请设置【回复节点" + name + "】的回复内容", data: []}
				}
			}
		}

		return {code: 0, msg: "success", data: []}
	}
}
