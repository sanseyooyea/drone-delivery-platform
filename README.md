# 无人机送货平台

基于 Web 的无人机送货平台，提供用户下单、商家接单备货、无人机配送全流程管理。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Kotlin + Spring Boot 3 + MyBatis-Plus |
| 前端 | Vue 3 + Element Plus + Vite |
| 构建 | Gradle (Kotlin DSL) |
| 数据库 | MySQL 8.0 |
| 认证 | JWT |
| 实时通信 | WebSocket (STOMP) |

## 功能模块

### 用户端
- 注册/登录、商品浏览与搜索、购物车、下单（模拟支付）
- 订单管理（查看/取消/确认收货）、配送状态跟踪
- 收货地址管理、在线客服、个人中心

### 商家端
- 商家入驻申请、商品管理（增删改查/上下架）
- 订单处理（接单/拒单/备货完成）、经营数据统计

### 管理端
- 用户管理、商家审核、无人机管理（录入/状态/配送日志）
- 全平台订单管理、客服工作台、数据概览

## 环境要求

- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Gradle 8.5+（或使用项目自带 Wrapper）

## 快速开始

### 1. 初始化数据库

```bash
mysql -u root -p < sql/init.sql
```

### 2. 配置数据库连接

编辑 `backend/src/main/resources/application.yml`，修改数据库用户名和密码。

### 3. 启动后端

```bash
cd backend
gradle bootRun
```

后端运行在 http://localhost:8080

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端运行在 http://localhost:5173

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 商家 | merchant1 | merchant123 |
| 商家 | merchant2 | merchant123 |
| 用户 | user1 ~ user5 | user123 |

## 项目结构

```
drone-delivery-platform/
├── backend/                # Kotlin Spring Boot 后端
│   ├── src/main/kotlin/    # 源码 (controller/service/mapper/entity/...)
│   ├── src/main/resources/ # 配置文件
│   └── build.gradle.kts    # Gradle 构建配置
├── frontend/               # Vue 3 前端
│   ├── src/                # 源码 (views/api/router/stores/...)
│   ├── package.json
│   └── vite.config.ts
├── sql/
│   └── init.sql            # 数据库初始化脚本（含测试数据）
├── 需求文档.md
├── 开发文档.md
└── 测试文档.md
```

## 文档

- [需求文档](需求文档.md) — 功能需求、角色定义、订单流转
- [开发文档](开发文档.md) — 技术架构、数据库设计、接口设计
- [测试文档](测试文档.md) — 测试账号、测试用例、业务流程测试
