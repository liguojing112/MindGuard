# MindGuard - 智能校园心理情绪监测与倾诉平台

## 项目简介

MindGuard 是一个基于 Spring Boot + Vue 3 的全栈校园心理情绪监测与倾诉平台。平台集成 AI 情感分析（DeepSeek + Mock 降级），为高校学生提供情绪倾诉、心理测评、心理咨询预约一站式服务，同时为辅导员提供危机预警、数据大屏、学生心理档案等管理功能。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.2.5 |
| ORM | MyBatis-Plus | 3.5.6 |
| 数据库 | MySQL | 8.0 |
| 安全 | JWT + BCrypt | jjwt 0.12.5 |
| AI | DeepSeek API + Mock 降级 | - |
| Excel 导出 | Apache POI | 5.2.5 |
| 前端框架 | Vue 3 + Vite | 3.5+ |
| UI 组件库 | Element Plus | 2.9+ |
| 图表 | ECharts | 5.x |
| 状态管理 | Pinia | 2.x |
| HTTP 客户端 | Axios | 1.x |

## 项目结构

```
MindGuard/
├── mindguard-server/                 # 后端 Spring Boot 项目
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/mindguard/
│       │   ├── MindGuardApplication.java
│       │   ├── common/               # 通用类：Result, PageResult, Exception, Enums
│       │   ├── config/               # 配置：CORS, MyBatis-Plus, Security, AI, JWT, DataFix
│       │   ├── security/             # JWT Token 提供器 + 认证拦截器
│       │   ├── ai/                   # AI 服务：接口 + Mock + DeepSeek + 设置管理
│       │   ├── util/                 # 工具类：Excel导出, 分数计算器
│       │   └── module/
│       │       ├── user/             # 用户模块 (认证/注册)
│       │       ├── emotion/          # 情绪模块 (倾诉+AI分析+危机预警+打卡)
│       │       ├── assessment/       # 测评模块 (量表+作答+报告+科普文章)
│       │       ├── appointment/      # 预约模块 (预约+审核+咨询+评价+档案)
│       │       └── dashboard/        # 仪表盘模块 (数据大屏+统计)
│       └── resources/
│           ├── application.yml       # 主配置
│           └── db/init.sql           # 数据库初始化脚本（16张表 + 初始数据）
└── mindguard-frontend/               # 前端 Vue 3 项目
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── main.js, App.vue
        ├── router/index.js           # 三角色路由 + JWT 角色守卫
        ├── store/                    # Pinia 状态管理（user, notification）
        ├── utils/                    # Axios 封装, Token 工具, 常量
        ├── api/                      # 9 个 API 模块
        ├── views/                    # 23 个页面
        ├── components/               # 19 个共享组件
        └── styles/                   # 全局样式（柔和绿主题）
```

## 快速开始

### 环境要求

- **JDK 17+**（推荐 JDK 21）
- **MySQL 8.0+**
- **Node.js 18+**
- **Maven 3.8+**

### 1. 数据库初始化

```bash
# 登录 MySQL，执行初始化脚本
mysql -u root -p < mindguard-server/src/main/resources/db/init.sql
```

或在 MySQL 客户端中直接执行 `init.sql` 文件内容。

### 2. 启动后端

```bash
cd mindguard-server
mvn spring-boot:run
```

后端运行在 `http://localhost:8080`

**配置说明：**
- 数据库密码：默认 `root123`，可通过环境变量 `DB_PASSWORD` 修改
- JWT 密钥：默认内置，可通过 `JWT_SECRET` 环境变量修改
- 上传路径：默认 `./uploads`，可通过 `UPLOAD_PATH` 环境变量修改
- AI 模式：默认 Mock 模式，无需配置 API Key
- 若要使用真实 DeepSeek API：登录管理员端 → 系统设置 → 关闭 Mock 开关并填写 API Key

### 3. 启动前端

```bash
cd mindguard-frontend
npm install
npm run dev
```

前端运行在 `http://localhost:5173`，API 请求自动代理到后端 8080 端口。

### 4. 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 系统管理员，可配置AI设置 |
| 辅导员 | counselor1 | 123456 | 张老师 |
| 辅导员 | counselor2 | 123456 | 李老师 |
| 学生 | student1 | 123456 | 王同学 |
| 学生 | student2 | 123456 | 陈同学 |
| 学生 | student3 | 123456 | 刘同学 |

> 登录页支持下拉选择账号，选中后自动填入用户名和密码。

## 三大核心业务模块

### 模块 1：情绪倾诉 + AI 分析 + 危机预警

1. **学生发布倾诉**：支持实名/匿名，心情 emoji 选择
2. **AI 情感分析**：自动分析文本，返回情绪分数(0-100)和三档标签
3. **情绪三档分类**：
   - 正常情绪：分数 ≥ 70 或积极/中性内容
   - 一般负面：分数 40-69，含一般负面词汇
   - 高危负面：分数 < 40，含自杀/自伤等关键词
4. **危机预警**：高危负面自动生成预警单(PENDING)，通知所有辅导员
5. **预警状态流转**：
   ```
   PENDING → NOTICED（辅导员关注）
           → COMMUNICATED（已沟通）
           → RESOLVED（已解决）
   ```
6. **高危预约提醒**：有高危预警的学生自动收到预约咨询提醒通知

> **数据表**：emotion_post, mood_checkin, emotion_analysis_result, alert, notification

### 模块 2：心理测评 + 测评报告 + 科普推荐

1. **内置量表**：焦虑自评量表(SAS) 和 抑郁自评量表(SDS)，各 20 题
2. **自动评分**：提交后计算总分和标准分（= 原始分 × 1.25）
3. **程度判定**：
   - SAS：<50 正常 / 50-59 轻度 / 60-69 中度 / ≥70 重度
   - SDS：<53 正常 / 53-62 轻度 / 63-72 中度 / ≥73 重度
4. **生成报告**：包含分数、程度描述、个性化建议
5. **智能推荐**：根据测评程度自动匹配科普文章
6. **辅导员查看**：全班测评数据表格 + Excel 导出

> **数据表**：scale, scale_question, scale_option, user_assessment, assessment_result, article

### 模块 3：心理咨询预约 + 状态流转 + 高危联动

1. **查看咨询师**：咨询师列表，含简介和擅长领域
2. **预约申请**：选择日期/时段/问题类型/匿名选项
3. **审核流转**：
   ```
   PENDING → APPROVED/REJECTED → IN_PROGRESS → COMPLETED → ARCHIVED
   ```
4. **房间号生成**：审核通过后自动生成线上咨询房间号
5. **咨询完成**：咨询师填写咨询记录（内容摘要+诊断+建议） + 学生评价打分
6. **高危联动**：有预警/重度测评的学生优先展示
7. **学生档案**：综合查询（情绪记录 + 测评历史 + 预约记录 + 咨询记录）

> **数据表**：counselor, appointment, consultation_record, evaluation

## AI 集成方案

### 架构设计

```
AIService (接口)
├── MockAIService (默认，无需 API Key)
│   - 基于关键词匹配 + 规则算法
│   - 积极词 → 70-95 分
│   - 一般负面词 → 40-69 分
│   - 高危负面词 → 15-39 分
├── DeepSeekAIService (可选，需配置 API Key)
│   - 调用 DeepSeek Chat API
│   - 结构化 JSON 返回
│   - 失败时自动降级为 Mock
└── 动态代理 (运行时读取配置)
    - 管理员可在系统设置页面切换模式
    - 无需重启服务即可生效
```

### 配置方式

管理员登录后，在左侧菜单"系统设置"中：
- 切换 Mock 模拟 / DeepSeek API 模式
- 填写 DeepSeek API Key（sk-...）
- 保存后即刻生效，无需重启

## 角色权限

| 角色 | 路由前缀 | 功能范围 |
|------|---------|------|
| STUDENT | `/student/*` | 情绪广场、心情打卡、心理测评、咨询预约、科普文章、个人中心 |
| COUNSELOR | `/counselor/*` | 数据大屏、预警管理、预约审核、测评数据、文章管理、学生档案 |
| ADMIN | `/admin/*` | 辅导员全部功能 + 系统设置（AI 配置） |

- 前端路由守卫 + 后端 JWT 拦截器双重校验
- ADMIN 可访问所有辅导员功能
- 辅导员只能看到自己相关的预约（非全部辅导员数据）

## 安全方案

- **JWT 认证**：登录后返回 Token，有效期 24 小时
- **BCrypt 加密**：用户密码使用 BCryptPasswordEncoder 加密存储
- **角色鉴权**：前端路由守卫 + 后端 HandlerInterceptor
- **数据隔离**：学生只能操作自己的数据

## 页面展示

### 学生端（12 个页面）

| 页面 | 说明 |
|------|------|
| 首页 | 欢迎页，快捷入口 |
| 情绪广场 | 公开倾诉墙，AI 分析标签展示 |
| 我的倾诉 | 个人倾诉列表 + AI 分析反馈 |
| 心情打卡 | Emoji 选择 + 日历热力图 |
| 心理测评 | 量表列表 → 逐题作答 → 报告展示 |
| 我的测评 | 测评历史记录 |
| 心理咨询 | 咨询师列表 → 时段选择 → 预约 |
| 我的预约 | 预约记录 + 咨询记录 + 评价 |
| 科普文章 | 分类列表 + 详情页 |
| 个人中心 | 信息编辑 + 头像上传 + 统计概览 |
| 通知列表 | 系统通知 + 预警通知 |

### 辅导员/管理员端（10 个页面）

| 页面 | 说明 |
|------|------|
| 数据大屏 | 统计卡片 + 情绪饼图 + 预警趋势折线图 + 测评柱状图 |
| 预警管理 | 预警列表 + 状态流转(PENDING→NOTICED→COMMUNICATED→RESOLVED) |
| 预约管理 | 审核通过/拒绝 + 开始咨询 + 填写咨询记录 |
| 测评数据 | 全班测评表格 + Excel 导出 |
| 文章管理 | 科普文章 CRUD（支持视频类型） |
| 学生档案 | 综合查询：情绪+测评+预约+咨询记录 |
| 系统设置 | 仅管理员可见：AI 模式切换 + API Key 配置 |
| 个人中心 | 信息编辑 + 头像上传 |
| 通知列表 | 系统通知 |

## 交付校验

- [x] 后端 105 个 Java 文件，编译零错误
- [x] 前端 67 个文件，Vite 构建零错误
- [x] 16 张数据库表，完整初始数据（6个用户 + 2个量表(40题+160选项) + 10篇文章）
- [x] 三大业务模块状态流转完整闭环
- [x] AI Mock/DeepSeek 动态切换 + 自动降级
- [x] JWT 认证 + BCrypt 密码加密
- [x] 前端三角色独立路由 + 后端角色鉴权
- [x] 数据大屏可视化（ECharts 饼图/折线图/柱状图）
- [x] Excel 测评数据导出
- [x] 文件上传（头像）
- [x] Element Plus 柔和绿色主题
- [x] 登录页用户快速选择
- [x] 管理员 AI 设置页面

## 后续扩展建议

- 集成 Redis 实现 Token 黑名单和会话管理
- 接入更多心理量表（SCL-90、PHQ-9 等）
- WebSocket 实时消息推送
- 移动端适配（响应式或小程序）
- 心理咨询视频通话集成
- 数据导出支持 PDF 报告
