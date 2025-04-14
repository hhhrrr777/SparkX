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
	}
}
