<div align="center">

# UniHub 校园管理系统

[中文](#中文) · [EN](#english)

</div>

---

# 中文

## 📋 简介

全栈教务管理平台，包含**两套架构**：

| 版本 | 架构 | 状态 |
|------|------|------|
| **v1** (稳定版) | Servlet + JSP + MyBatis + 嵌入式 Tomcat | ✅ 生产可用 |
| **v2** (重构中) | Spring Boot 3 + Vue 3 + MyBatis-Plus + JWT | 🚧 开发中 |

覆盖学生信息、选课、成绩、考勤、作业、公告等完整教务流程，支持管理员/教师/学生三种角色权限分离。

## ✨ 功能亮点

- **BCrypt 密码加密** — 兼容旧版 SHA-256 哈希，登录时自动升级
- **Redis 缓存** — 公告缓存 + 选课分布式锁，支持静默降级
- **EasyExcel 导入导出** — 替代 Apache POI，解决 OOM 问题
- **ECharts 仪表盘** — 数据可视化大屏
- **验证码** — 点击刷新，仅在登录失败时自动刷新
- **BaseServlet 基类** — 31 个 Servlet 统一继承，消除重复代码

## 🛠 技术栈

| | v1 (Servlet/JSP) | v2 (Spring Boot + Vue3) |
|--|-------------------|------------------------|
| **后端** | Java 17, Jakarta Servlet 6.0 | Spring Boot 3.4.3, Spring Security |
| **ORM** | MyBatis 3.5.19 | MyBatis-Plus 3.5.9 |
| **前端** | JSP + CSS3 | Vue 3 + Vite 6 + Element Plus |
| **安全** | Session + Filter | JWT (jjwt 0.12.6) |
| **数据库** | MySQL 8 | MySQL 8 |
| **缓存** | Redis (Jedis 5.2) | Redis (Spring Data Redis) |
| **Excel** | EasyExcel 4.0.3 | EasyExcel 4.0.3 |
| **图表** | ECharts 5.6.0 | ECharts 5.6.0 |
| **服务器** | Tomcat 10.1.39 (嵌入式) | Tomcat (Spring Boot 内嵌) |
| **构建** | Maven 3.x | Maven 3.x / npm |

## 🚀 快速开始

### v1（原 Servlet/JSP 版本）

```bash
# 依赖：JDK 17+, MySQL 8+, Maven 3+

# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS test DEFAULT CHARACTER SET utf8mb4;"

# 2. 初始化数据
mysql -u root -p test < src/main/resources/db/schema-new.sql
mysql -u root -p test < src/main/resources/db/seed-data.sql

# 3. 配置数据库连接（默认 root/123456）
# 编辑 src/main/resources/mybatis-config.xml

# 4. 编译运行
mvnw clean compile exec:java

# 5. 访问
http://localhost:8080/
```

### v2（Spring Boot + Vue3 版本）

```bash
# 启动后端
cd backend
mvnw spring-boot:run

# 启动前端（新开终端）
cd frontend
npm install
npm run dev

# 访问 http://localhost:5173
```

## 👤 测试账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | 123456 |
| 教师 | teacher00 | 123456 |
| 学生 | 2501330000 | 123456 |

## 📁 项目结构

```
├── backend/                        # v2: Spring Boot 后端
│   ├── pom.xml
│   └── src/main/java/com/example/unihub/
│       ├── config/                 # Security + MyBatis-Plus 配置
│       ├── controller/             # REST API
│       ├── dto/                    # 数据传输对象
│       ├── entity/                 # MyBatis-Plus 实体
│       ├── mapper/                 # 数据访问接口
│       ├── security/               # JWT 过滤器
│       ├── service/                # 业务逻辑
│       └── util/                   # JWT 工具类
├── frontend/                       # v2: Vue 3 前端
│   ├── package.json
│   └── src/
│       ├── api/                    # Axios API 封装
│       ├── router/                 # Vue Router
│       ├── stores/                 # Pinia 状态管理
│       └── views/                  # 页面组件
├── src/                            # v1: Servlet/JSP 项目
│   ├── main/java/com/example/
│   │   ├── TomcatLauncher.java     # 嵌入式 Tomcat 入口
│   │   ├── entity/                 # 实体类
│   │   ├── mapper/                 # MyBatis 接口
│   │   ├── servlet/                # 31 个 Servlet
│   │   └── util/                   # 工具类 (BCrypt, Redis, Excel)
│   ├── main/resources/
│   │   ├── mybatis-config.xml
│   │   └── db/                     # SQL 脚本 + 索引优化
│   └── main/webapp/
│       ├── *.jsp                   # 页面视图
│       ├── scripts/app.js
│       └── styles/ui.css
├── pom.xml                         # v1 Maven 配置
├── .gitignore
└── LICENSE                         # MIT
```

## 📄 许可

MIT License — 详见 [LICENSE](LICENSE)。

---

# English

## 📋 Overview

A full-stack campus management platform with **two architectures**:

| Version | Stack | Status |
|---------|-------|--------|
| **v1** (stable) | Servlet + JSP + MyBatis + embedded Tomcat | ✅ Production |
| **v2** (refactoring) | Spring Boot 3 + Vue 3 + MyBatis-Plus + JWT | 🚧 In Progress |

Role-based access control (Admin / Teacher / Student). Covers student records, course selection, grades, attendance, homework, announcements, and audit logging.

## ✨ Features

- **BCrypt password hashing** — backward compatible with legacy SHA-256, auto-upgrade on login
- **Redis caching** — announcement cache + distributed lock for course selection, graceful degradation
- **EasyExcel import/export** — replaces Apache POI, solves OOM issues
- **ECharts dashboard** — data visualization with bar/pie charts
- **CAPTCHA** — click to refresh, auto-refresh only on login failure
- **BaseServlet** — 31 servlets inherit from a single abstract base class

## 🛠 Tech Stack

| | v1 (Servlet/JSP) | v2 (Spring Boot + Vue3) |
|--|-------------------|------------------------|
| **Backend** | Java 17, Jakarta Servlet 6.0 | Spring Boot 3.4.3, Spring Security |
| **ORM** | MyBatis 3.5.19 | MyBatis-Plus 3.5.9 |
| **Frontend** | JSP + CSS3 | Vue 3 + Vite 6 + Element Plus |
| **Security** | Session + Filter | JWT (jjwt 0.12.6) |
| **Database** | MySQL 8 | MySQL 8 |
| **Cache** | Redis (Jedis 5.2) | Redis (Spring Data Redis) |
| **Excel** | EasyExcel 4.0.3 | EasyExcel 4.0.3 |
| **Charts** | ECharts 5.6.0 | ECharts 5.6.0 |
| **Server** | Tomcat 10.1.39 (embedded) | Tomcat (Spring Boot embedded) |
| **Build** | Maven 3.x | Maven 3.x / npm |

## 🚀 Quick Start

### v1 (Servlet/JSP)

```bash
# Requirements: JDK 17+, MySQL 8+, Maven 3+

mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS test DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p test < src/main/resources/db/schema-new.sql
mysql -u root -p test < src/main/resources/db/seed-data.sql
mvnw clean compile exec:java
# → http://localhost:8080/
```

### v2 (Spring Boot + Vue3)

```bash
cd backend && mvnw spring-boot:run    # Backend :8080
cd frontend && npm install && npm run dev  # Frontend :5173
```

## 👤 Test Accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | 123456 |
| Teacher | teacher00 | 123456 |
| Student | 2501330000 | 123456 |

## 📁 Project Structure

```
├── backend/                  # v2: Spring Boot
├── frontend/                 # v2: Vue 3 + Vite
├── src/                      # v1: Servlet/JSP
├── pom.xml
├── .gitignore
└── LICENSE
```

## 📄 License

MIT License — see [LICENSE](LICENSE).

## License

MIT License — see [LICENSE](LICENSE).
