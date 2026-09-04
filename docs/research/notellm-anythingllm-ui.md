# NoteLLM：AnythingLLM 官方界面与交互参考

研究日期：2026-09-04。对应决策：[调研 AnythingLLM 的官方界面与交互参考](https://github.com/hhhrrr777/SparkX/issues/2)。

## 结论与证据边界

可以借鉴左侧导航、中央聊天面板、分层中性色、浅深主题、按需展开详情和窄屏抽屉；无需为此引入 workspace 业务实体。下面的事实来自官方源码；NoteLLM 适配列是建议，仍需用户通过原型确认，不是已批准的视觉规格。

参考版本为官方 master 在研究时返回的固定提交 [`eb7df1e81c284236e1759ec7897904dc22a6704d`](https://github.com/Mintplex-Labs/anything-llm/commit/eb7df1e81c284236e1759ec7897904dc22a6704d)，不将它等同于某个 Desktop 发布版本。只查阅官方来源，未运行 AnythingLLM。

[官方文档首页](https://docs.anythingllm.com/)可读取，但其 [Chat Interface overview](https://docs.anythingllm.com/chat-ui)、[Appearance Customization](https://docs.anythingllm.com/features/customization)、[Attaching vs RAG](https://docs.anythingllm.com/chatting-with-documents/introduction) 子页本次返回 403。官方仓库的 [promo 图片](https://github.com/Mintplex-Labs/anything-llm/blob/eb7df1e81c284236e1759ec7897904dc22a6704d/images/promo.png) 未能由网页工具取回，因此本报告没有已视觉核验的截图，不从图片推断颜色、间距或交互。

## 可复用模式

| 范围 | 官方证据 | NoteLLM 适配建议（待原型确认） |
| --- | --- | --- |
| 导航与壳层 | Sidebar 以 292px 宽度展开，可收起；上方 Logo，中部 SearchBox、ActiveWorkspaces，下方 Footer；内部面板有 16px 圆角与外间距。[S1] | 以 NoteLLM 原创 Logo、对话入口和历史列表构成主导航；知识库、智能体、工作流保留独立入口。292px 只是参考尺寸，应按中文长名称验证。 |
| 聊天空态 | 空态将问候、输入框放在最大 750px 的居中区域，带创建 Agent、编辑 workspace、上传文档的 QuickActions 和 SuggestedMessages。[S2] | 提供简洁开聊空态和现有能力的快捷操作；不要显示尚未实现的建议问题或操作。 |
| 有消息的聊天 | 主面板为桌面 16px 圆角；上方有模型选择和聊天设置，正文由 ChatHistory 与 PromptInput 组成，文件拖入包装层包住聊天；SourcesSidebar 单独承载来源。[S2] | 保留现有消息、流式响应、模型选择和引用能力；把低频设置、引用详情做按需面板。空态与会话态输入区位置变化须在原型中评估。 |
| 知识资料 | Documents 同时渲染 Directory 与 WorkspaceDirectory，维护加入/移除集合，调用 modifyEmbeddings，并展示 embedding progress。[S3] | 保留“知识库 → 文件 → 处理状态”模型；借鉴列表选择、状态和明确保存反馈。不要把上传完成等同于可检索，也不要用 workspace 名称掩盖现有知识库关联。 |
| 设置 | SettingsSidebar 把 AI providers、管理项、Agent skills 分组；部分选项根据角色显示。[S4] | 建立独立设置区域，按现有权限过滤入口；智能体和工作流仍是本项目业务入口，不能机械搬到 AnythingLLM 的管理菜单下。 |
| 深浅主题 | CSS 定义语义 token：默认主背景 #0e0f0f、次背景 #1b1b1e、输入背景 #27282a、主文字白色；light 覆盖对应 token，主文字 #0e0f0f、主按钮 #0ba5ec。[S5] | 使用同类中性分层与单一强调色；通过 Naive UI themeOverrides 和项目 CSS 变量统一，最终 NoteLLM 色值待设计确认。CSS 默认值不能证明所有用户默认启用深色。 |
| 窄屏 | SidebarMobileHeader 为固定顶部栏，菜单展开带遮罩、80% 宽抽屉；聊天容器同时使用 isMobile 和 md 样式，移动端取消桌面外边距/圆角安排。[S1][S2] | 以视口宽度驱动 Vue 布局和 Naive UI Drawer；桌面侧栏与手机抽屉共享导航数据。验证软键盘、焦点、长消息、横向代码和上传状态，不能仅据源码宣称移动体验合格。 |

## 对现有技术与模型的约束

以下为已给定的 NoteLLM 方向，而非 AnythingLLM 产品事实：保留 Vue 3 + Naive UI，保留“对话、知识库、智能体、工作流”及现有数据归属，不新增 workspace 实体。官方参考前端是 React（可见上述 .jsx 源码）；本研究只提取布局与交互模式，不建议迁移组件框架。

建议统一实现范围为应用壳层、导航行、页面标题、列表/表单容器、按钮与输入框、空态、加载/错误反馈及主题 token；工作流画布采用统一外围样式，画布内部交互按现有能力单独验证。这些建议旨在形成后续原型的可比较起点，不属于本研究交付的产品实现。

## 留给设计原型的决策

1. 深浅主题均覆盖时的首选主题、NoteLLM 强调色、Logo 形态与中文字体层级。
2. 对话列表与知识库/智能体/工作流入口的层级；宽屏与窄屏的实际切换阈值。
3. 知识库是否采用双栏选择器、全页列表或抽屉，以及如何呈现处理失败/重试。
4. 聊天设置与引用面板的显示时机和空态输入框的位置变化。

原型至少应让用户比较桌面聊天空态、有引用的会话、知识库文件列表、设置表单和手机导航。尚未核实屏幕阅读器、键盘操作、视觉对比度或端到端行为；这些必须作为实现后的验收项目，不能从借鉴对象推定通过。

## 固定版本一手来源

- [S1：Sidebar](https://github.com/Mintplex-Labs/anything-llm/blob/eb7df1e81c284236e1759ec7897904dc22a6704d/frontend/src/components/Sidebar/index.jsx)
- [S2：ChatContainer](https://github.com/Mintplex-Labs/anything-llm/blob/eb7df1e81c284236e1759ec7897904dc22a6704d/frontend/src/components/WorkspaceChat/ChatContainer/index.jsx)
- [S3：Documents](https://github.com/Mintplex-Labs/anything-llm/blob/eb7df1e81c284236e1759ec7897904dc22a6704d/frontend/src/components/Modals/ManageWorkspace/Documents/index.jsx)
- [S4：SettingsSidebar](https://github.com/Mintplex-Labs/anything-llm/blob/eb7df1e81c284236e1759ec7897904dc22a6704d/frontend/src/components/SettingsSidebar/index.jsx)
- [S5：主题 CSS](https://github.com/Mintplex-Labs/anything-llm/blob/eb7df1e81c284236e1759ec7897904dc22a6704d/frontend/src/index.css)
