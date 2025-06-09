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
			}
		}

		return {code: 0, msg: "success", data: []}
	}
}
