export default {
	// 开始节点初始数据
	startData: {
		type: 'start',
		sysData: [
			{field: 'sys.question', name: '用户问题'},
			{field: 'sys.time', name: '当前时间'},
			{field: 'sys.ip', name: '用户IP'},
			{field: 'sys.sessionId', name: '对话ID'},
			{field: 'sys.appId', name: '应用ID'},
		]
	},
	// 意图分类节点初始数据
	purposeData: {
		type: 'purpose',
		cateList: [
			{name: '分类1'}
		],
		modeInfo: {
			modeId: "",
			modeName: "",
			temperature: 0
		},
		inputData: [],
		outData: [
			{field: 'sys.purposeName', name: '意图分类名'},
		]
	},
	// llm节点初始数据
	llmData: {
		type: 'llm',
		modeInfo: {
			modeId: "",
			modeName: "",
			temperature: 0
		},
		inputData: [],
		memory: 2,
		systemMsg: "",
		userMsg: "",
		outData: [
			{field: 'sys.content', name: '生成内容'},
		]
	},
	// 知识检索节点初始数据
	datasetData: {
		type: 'dataset',
		inputData: [],
		datasets: [],
		outData: [
			{field: 'sys.result', name: '检索结果'},
		]
	},
	// 回复节点的初始数据
	answerData: {
		type: 'answer',
		inputData: [],
		answerType: 1,
		answer: "",
		outData: [
			{field: 'sys.answer', name: '回复内容'},
		]
	},
	// 条件分支节点的初始数据
	switchData: {
		type: 'switch',
		inputData: [],
		ifBranch: [
			{type: 'if', data:[{input: "", tips: "", value: ""}]}
		],
		elseBranch: {input: "", tips: "", value: ""},
		outData: [
			{field: 'sys.branchName', name: '分支名称'},
		]
	},
	// 基础桩点
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
			rightPorts: {
				position: 'right', // 端口位于节点左侧
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
			{ group: 'leftPorts', type: 'input' }, // 将端口分配到左侧分组
			{ group: 'rightPorts', type: 'output' }
		]
	},
	// 左边桩点
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
}
