# NoteLLM 原型 A 视觉与交互检查

final result: passed

## 比较依据

- Source: docs/prototypes/notellm/selected-reference.png，1487 × 1058 原始生成图。
- Implementation: docs/prototypes/notellm/evidence/chat-desktop-final.png，1487 × 1058；浏览器 CSS viewport 1487 × 1058，devicePixelRatio=1。未拉伸源图。
- 同状态：浅色、聊天空态、侧栏展开。使用同一工具输入同时打开源图与实现图进行全视图比较。
- 初版：evidence/chat-desktop.png；修正后：evidence/chat-desktop-final.png。
- 局部检查：全尺寸图内的品牌、侧栏行、主标题、输入框和底部版权文字均可辨；未另做图像裁切。生成图字体与操作系统字体栅格化存在差异，不声称像素完全一致。
- 其余截图：knowledge-desktop.png、source-drawer.png、workflow-desktop.png、settings-desktop.png、chat-dark.png、chat-mobile.png、navigation-mobile.png。

## 比较历史

1. 初版 [P2] 主面板左界比源图左移 10px、空态整体偏下约 10px；修正 sidebar 284→294px、empty-chat 26vh→25vh、history 上间距减 8px。
2. 重新以相同尺寸捕获最终图并与源图同时查看：主区域比例、标题与输入区的相对位置恢复，无阻塞级视觉差异。
3. 手机检查发现关闭的侧栏仍暴露于可访问树；增加 inert，弹层加入焦点与 Tab 边界处理。重新查看手机状态，关闭侧栏已不在可访问树中；Escape 关闭导航已验证。

## 五项视觉检查

- 字体与层级：品牌、主标题、说明、输入文本、侧栏分层与参考一致；使用系统中文字体，不引入网络字体依赖。细小字重与栅格化差异属于 P3。
- 布局与间距：桌面主体、侧栏、空态及输入区与参考相符；手机 390 × 844 下无 body 水平溢出（scrollWidth=innerWidth=390），导航抽屉正常。列表/表单可滚动。
- 颜色与 token：浅色中性底、白面板、蓝强调色；深色通过语义变量同步。参考有轻微色彩渐变，实现使用可维护的平色，为预期差异。
- 图像：使用内置 ImageGen 生成的透明 N logo，无自制 SVG/文字替代品牌；标准 UI 图标来自项目已有 Ionicons。小尺寸 logo 清晰，最终正式 logo 的光学细化属 P3。
- 文案：保留选定图的 NoteLLM、问候、说明及主入口；版权依照已确认边界保留。左下原型状态标记属于原型控制，在生产构建中隐藏。

## 浏览器交互证据

已通过真实浏览器操作验证：发送问题进入等待态；打开历史会话；打开/关闭引用抽屉；知识库导航及文件列表；重试进入处理中；搜索无结果；工作流节点配置及模拟运行完成；设置表单保存反馈；深浅主题；390px 手机导航；创建智能体后显示新增项。浏览器 error/warn 日志查询为空。独立 Vite 构建通过。

## 范围与未覆盖项

这是外观决策原型，未连接真实 API、登录、文件存储或生产数据；未验证真实 AI 结果、工作流拖拽引擎、全部业务设置、屏幕阅读器及真实手机软键盘。正式实现需保留原有功能、权限与业务校验，不能把本检查解释为生产验收。

## 后续微调

[P3] 正式实现时细化中文字体回退、图标视觉重量和 logo 光学尺寸。其他扩展页面属于沿选定方案提出的设计，仍待用户确认；QA 通过不等于产品决策已批准。
