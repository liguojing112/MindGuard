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
| 前端框架 | Vue 3 + Vite | 3.4+ |
| UI 组件库 | Element Plus | 2.x |
| 图表 | ECharts | 5.x |
| 状态管理 | Pinia | 2.x |
| HTTP 客户端 | Axios | 1.x |

## 项目结构

```
MindGuard/
├── mindguard-server/                 # 后端 Spring Boot 项目
│   ├── pom.xml
│   ├── .mvn/jvm.config               # JVM 参数（JDK 24 兼容）
│   └── src/main/
│       ├── java/com/mindguard/
│       │   ├── MindGuardApplication.java
│       │   ├── common/               # 通用类：Result, PageResult, Exception, Enums
│       │   ├── config/               # 配置：CORS, MyBatis-Plus, Security, AI, JWT
│       │   ├── security/             # JWT Token 提供器
│       │   ├── ai/                   # AI 服务：接口 + Mock + DeepSeek
│       │   ├── util/                 # 工具类：Excel导出, 分数计算器
│       │   └── module/
│       │       ├── user/             # 用户模块 (认证/注册)
│       │       ├── emotion/          # 情绪模块 (倾诉+AI分析+危机预警+打卡)
│       │       ├── assessment/       # 测评模块 (量表+作答+报告+科普文章)
│       │       ├── appointment/      # 预约模块 (预约+审核+咨询+评价+档案)
│       │       └── dashboard/        # 仪表盘模块 (数据大屏+统计)
│       └── resources/
│           ├── application.yml       # 主配置
│           └── db/init.sql           # 数据库初始化脚本（含初始数据）
└── mindguard-frontend/               # 前端 Vue 3 项目
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── main.js, App.vue
        ├── router/index.js           # 路由 + JWT 角色守卫
        ├── store/                    # Pinia 状态管理
        ├── utils/                    # Axios 封装, Token 工具, 常量
        ├── api/                      # 7 个 API 模块
        ├── views/                    # 25 个页面（学生端12+辅导员端8+公共5）
        ├── components/               # 19 个共享组件
        └── styles/                   # 主题色（柔和绿+薰衣草紫）
```

## 快速开始

### 环境要求

- **JDK 21+**（推荐 JDK 21，JDK 24 需额外配置）
- **MySQL 8.0+**
- **Node.js 18+**
- **Maven 3.8+**

### 1. 数据库初始化

```bash
# 登录 MySQL，执行初始化脚本
mysql -u root -p < mindguard-server/src/main/resources/db/init.sql
```

或直接复制 `init.sql` 内容在 MySQL 客户端中执行。

### 2. 启动后端

```bash
cd mindguard-server
mvn spring-boot:run
```

后端运行在 `http://localhost:8080`

**配置说明：**
- 数据库密码：默认 `root`，可通过环境变量 `DB_PASSWORD` 修改
- JWT 密钥：默认内置，可通过 `JWT_SECRET` 环境变量修改
- AI 模式：默认 Mock 模式 (`deepseek.mock=true`)，无需配置 API Key
- 若要使用真实 DeepSeek API：设置 `deepseek.mock=false` 并配置 `DEEPSEEK_API_KEY`

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
| 管理员 | admin | 123456 | 系统管理员 |
| 辅导员 | counselor1 | 123456 | 张老师 - 心理咨询师 |
| 辅导员 | counselor2 | 123456 | 李老师 - 朋辈辅导员 |
| 学生 | student1 | 123456 | 王同学 |
| 学生 | student2 | 123456 | 陈同学 |
| 学生 | student3 | 123456 | 刘同学 |

## 三大核心业务模块

### 模块 1：情绪倾诉 + AI 分析 + 危机预警

1. **学生发布倾诉**：支持实名/匿名，心情 emoji 打卡
2. **AI 情感分析**：自动分析文本，返回情绪分数(0-100)和标签(积极/中性/一般负面/高危负面)
3. **危机预警**：分数 < 40 自动生成预警单(PENDING)，通知所有辅导员
4. **预警状态流转**：
   ```
   PENDING → NOTICED（辅导员关注）
           → COMMUNICATED（已沟通）
           → RESOLVED（已解决）
   ```
5. **学生闭环反馈**：可查看预警处理进度

> **数据表**：emotion_post, mood_checkin, emotion_analysis_result, alert, notification

### 模块 2：心理测评 + 测评报告 + 科普推荐

1. **内置量表**：焦虑自评量表(SAS) 和 抑郁自评量表(SDS)，各 20 题
2. **自动评分**：提交后计算总分和标准分（= 原始分 × 1.25）
3. **程度判定**：
   - SAS：<50 正常 / 50-59 轻度 / 60-69 中度 / ≥70 重度
   - SDS：<53 正常 / 53-62 轻度 / 63-72 中度 / ≥73 重度
4. **生成报告**：包含分数、程度描述、个性化建议
5. **智能推荐**：根据测评程度自动匹配科普文章
6. **数据大屏**：辅导员端 ECharts 可视化（饼图/折线图/柱状图）+ Excel 导出

> **数据表**：scale, scale_question, scale_option, user_assessment, assessment_result, article

### 模块 3：心理咨询预约 + 状态流转 + 高危联动

1. **查看咨询师**：校内咨询师和朋辈互助员列表
2. **预约申请**：选择日期/时段/问题类型/匿名选项
3. **审核流转**：
   ```
   PENDING → APPROVED/REJECTED → IN_PROGRESS → COMPLETED → ARCHIVED
   ```
4. **咨询完成**：咨询师填写记录 + 学生评价打分
5. **高危联动**：有预警/重度测评 → 优先匹配咨询师(priority 标记)
6. **学生档案**：综合查询（情绪记录 + 测评历史 + 预约记录）

> **数据表**：counselor, appointment, consultation_record, evaluation

## AI 集成方案

### 架构设计

```
AIService (接口)
├── MockAIService (默认，无需 API Key)
│   - 基于关键词匹配 + 随机算法
│   - 积极词 → 70-95 分
│   - 一般负面词 → 40-69 分
│   - 高危负面词 → 15-39 分
└── DeepSeekAIService (可选，需配置 API Key)
    - 调用 DeepSeek Chat API
    - 结构化 JSON 返回
    - 失败时自动降级为 Mock
```

### 配置

```yaml
deepseek:
  api:
    key: sk-your-key-here       # 免费试用 Key（可能已失效）
    url: https://api.deepseek.com/v1/chat/completions
  mock: true                     # true=强制Mock / false=尝试真实API
```

**降级策略**：当 `mock=false` 但 API Key 无效或调用失败时，自动降级为 Mock 模式，确保系统正常运行。

### AI 应用场景

1. **文本情感分析**：倾诉内容 → 情绪分数/标签/关键词/分析文本
2. **咨询建议生成**：综合学生情绪和测评数据 → 个性化咨询建议（辅导员工位展示）

## 安全方案

- **JWT 认证**：登录后返回 Token，有效期 24 小时
- **BCrypt 加密**：用户密码使用 BCryptPasswordEncoder 加密存储
- **角色鉴权**：前端路由守卫 + 后端方法级权限控制（STUDENT / COUNSELOR / ADMIN）
- **数据隔离**：学生只能操作自己的数据，辅导员可查看分管数据

## 页面展示

### 学生端

| 页面 | 说明 |
|------|------|
| 情绪广场 | 公开倾诉墙，AI 分析标签展示 |
| 心情打卡 | Emoji 选择 + 日历热力图 |
| 心理测评 | 量表列表 → 逐题作答 → 报告展示（仪表盘+程度+推荐） |
| 咨询预约 | 咨询师列表 → 时段选择 → 预约提交 |
| 科普文章 | 分类列表 + 个性化推荐 + 详情页 |
| 个人中心 | 我的倾诉/测评/预约 + 统计概览 |

### 辅导员端

| 页面 | 说明 |
|------|------|
| 数据大屏 | 统计卡片 + 情绪饼图 + 预警趋势折线图 + 测评柱状图 |
| 危机预警 | 预警列表 + 状态流转(PENDING→RESOLVED) |
| 预约审核 | 审核通过/拒绝 + 咨询记录填写 |
| 学生档案 | 综合查询：情绪+测评+预约历史 |
| 文章管理 | 科普文章 CRUD |

## 交付校验

- [x] 后端 98 个 Java 文件，编译零错误
- [x] 前端 62 个文件，Vite 构建零错误
- [x] 16 张数据库表，完整初始数据（6个用户 + 2个量表(40题) + 10篇文章）
- [x] 三大业务模块状态流转完整闭环
- [x] AI Mock/DeepSeek 降级方案
- [x] JWT 认证 + BCrypt 密码加密
- [x] 数据大屏可视化（ECharts）
- [x] Excel 数据导出
- [x] Element Plus 柔和绿色主题

## JDK 24 兼容性说明

本项目使用 JDK 24 编译通过。由于 JDK 24 移除了部分内部 API，需要：
1. 使用 **Lombok 1.18.38**（已在 pom.xml 中配置）
2. `.mvn/jvm.config` 中添加 `--add-opens` 参数（已配置）

如使用 JDK 21，无需上述额外配置。

## 后续扩展建议

- 集成 Redis 实现 Token 黑名单和会话管理
- 接入更多心理量表（SCL-90、PHQ-9 等）
- WebSocket 实时消息推送
- 移动端适配（响应式或小程序）
- 心理咨询视频通话集成
- 数据导出支持 PDF 报告
