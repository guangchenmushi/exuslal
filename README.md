<div align="center">

# UniHub 校园管理系统

[中文](#中文) · [EN](#english)

</div>

---

# 中文

## 📋 简介

基于 Spring Boot 3 + Vue 3 的现代化校园管理系统。覆盖学生信息、选课、成绩、考勤、公告等完整教务流程，支持管理员/教师/学生三种角色权限分离。

## ✨ 功能亮点

- **JWT 认证** — 无状态令牌，前后端分离安全架构
- **Redis 缓存** — 公告缓存 + 选课分布式锁
- **EasyExcel 导入导出** — 高性能流式读写，解决 OOM
- **ECharts 仪表盘** — 数据可视化大屏
- **Element Plus UI** — 现代化组件库

## 🛠 技术栈

| 层级 | 技术 |
|------|------|
| **后端** | Spring Boot 3.4.3, Spring Security, JWT |
| **ORM** | MyBatis-Plus 3.5.9 |
| **前端** | Vue 3 + Vite 6 + Element Plus |
| **状态管理** | Pinia |
| **路由** | Vue Router 4 |
| **数据库** | MySQL 8 |
| **缓存** | Redis (Spring Data Redis) |
| **Excel** | EasyExcel 4.0.3 |
| **图表** | ECharts 5.6.0 |
| **构建** | Maven 3.x (后端) / npm (前端) |

## 🚀 快速开始

```bash
# 前置要求：JDK 17+, MySQL 8+, Node.js 18+, Maven 3+

# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS demo2 DEFAULT CHARACTER SET utf8mb4;"

# 2. 配置数据库连接
# 编辑 backend/src/main/resources/application.yml

# 3. 启动后端
cd backend
mvnw spring-boot:run

# 4. 新开终端，启动前端
cd frontend
npm install
npm run dev

# 5. 访问 http://localhost:5173
```

## 👤 测试账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | 123456 |
| 教师 | teacher00 | 123456 |
| 学生 | 2501330000 | 123456 |

## 📁 项目结构

```
├── backend/                        # Spring Boot 后端
│   ├── pom.xml
│   ├── src/main/java/com/example/unihub/
│   │   ├── config/                 # Security + MyBatis-Plus 配置
│   │   ├── controller/             # REST API (Auth, User, Course, Grade...)
│   │   ├── dto/                    # 数据传输对象
│   │   ├── entity/                 # MyBatis-Plus 实体
│   │   ├── mapper/                 # 数据访问接口
│   │   ├── security/               # JWT 过滤器
│   │   ├── service/                # 业务逻辑层
│   │   └── util/                   # JWT 工具类
│   └── src/main/resources/
│       └── application.yml         # 配置文件
├── frontend/                       # Vue 3 前端
│   ├── package.json
│   └── src/
│       ├── api/                    # Axios API 封装
│       ├── router/                 # Vue Router 路由
│       ├── stores/                 # Pinia 状态管理
│       └── views/                  # 页面组件 (9 个)
├── .gitignore
├── LICENSE                         # MIT
└── README.md
```

## 📄 许可

MIT License — 详见 [LICENSE](LICENSE)。

---

# English

## 📋 Overview

A modern campus management system built with Spring Boot 3 + Vue 3. Role-based access control (Admin / Teacher / Student). Covers student records, course selection, grades, attendance, homework, announcements, and more.

## ✨ Features

- **JWT Authentication** — stateless token-based auth for SPA architecture
- **Redis Caching** — announcement cache + distributed lock for course selection
- **EasyExcel Import/Export** — streaming read/write, solves OOM issues
- **ECharts Dashboard** — data visualization with bar/pie charts
- **Element Plus UI** — modern component library

## 🛠 Tech Stack

| Layer | Stack |
|-------|-------|
| **Backend** | Spring Boot 3.4.3, Spring Security, JWT |
| **ORM** | MyBatis-Plus 3.5.9 |
| **Frontend** | Vue 3 + Vite 6 + Element Plus |
| **State** | Pinia |
| **Router** | Vue Router 4 |
| **Database** | MySQL 8 |
| **Cache** | Redis (Spring Data Redis) |
| **Excel** | EasyExcel 4.0.3 |
| **Charts** | ECharts 5.6.0 |
| **Build** | Maven 3.x / npm |

## 🚀 Quick Start

```bash
# Requirements: JDK 17+, MySQL 8+, Node.js 18+, Maven 3+

# 1. Create database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS demo2 DEFAULT CHARACTER SET utf8mb4;"

# 2. Configure connection
# Edit backend/src/main/resources/application.yml

# 3. Start backend
cd backend && mvnw spring-boot:run

# 4. Start frontend (new terminal)
cd frontend && npm install && npm run dev

# 5. Open http://localhost:5173
```

## 👤 Test Accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | 123456 |
| Teacher | teacher00 | 123456 |
| Student | 2501330000 | 123456 |

## 📁 Project Structure

```
├── backend/                  # Spring Boot REST API
├── frontend/                 # Vue 3 + Vite
├── .gitignore
├── LICENSE
└── README.md
```

## 📄 License

MIT License — see [LICENSE](LICENSE).

## License

MIT License — see [LICENSE](LICENSE).
