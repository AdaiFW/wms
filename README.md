# 仓储管理系统 (Warehouse Management System)

Spring Boot + Vue3 全栈仓储管理系统，支持商品管理、入库/出库、库存预警、乐观锁并发控制、数据大屏。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2、MyBatis-Plus 3.5、MySQL 8、Redis、Spring Security、JWT |
| 前端 | Vue 3、Element Plus、ECharts、Pinia、Vue Router、Axios |
| 安全 | JWT + Spring Security、BCrypt 密码加密、角色权限控制 |
| 并发 | MyBatis-Plus 乐观锁（version 字段）+ 重试机制 |

## 功能模块

- 登录 / 注册
- 用户管理 + 角色权限（RBAC）
- 商品分类管理
- 商品管理（CRUD）
- 供应商管理
- 入库管理（新增 → 自动确认 → 更新库存）
- 出库管理（新增 → 自动确认 → 扣减库存）
- 库存管理（实时库存查看、预警配置）
- 库存预警（阈值告警、库存不足提醒）
- 数据统计大屏（出入库趋势图、预警分布饼图）
- 库存流水日志

## 快速启动

### 1. 环境准备

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Node.js 18+
- Maven 3.8+

### 2. 创建数据库

```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/seed.sql
```

或在 MySQL 客户端中依次执行 `sql/schema.sql` 和 `sql/seed.sql`。

默认管理员账号：`admin` / `admin123`

### 3. 修改配置

编辑 `backend/src/main/resources/application.yml`，修改 MySQL 和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/warehouse?...
    username: root
    password: 你的密码
  data:
    redis:
      host: localhost
      port: 6379
      password: 你的Redis密码（没有就留空）
```

### 4. 启动后端

```bash
cd backend
mvn clean package -DskipTests
java -jar target/warehouse-backend-1.0.0.jar
```

或者用 Maven 直接运行：

```bash
cd backend
mvn spring-boot:run
```

后端启动后运行在 http://localhost:8080

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动后运行在 http://localhost:5173

### 6. 访问系统

打开浏览器访问 http://localhost:5173，使用 `admin` / `admin123` 登录。

## 接口联调方案

### API 基础地址

- 开发环境：`http://localhost:8080/api`
- 前端代理：Vite 自动将 `/api` 请求代理到 `http://localhost:8080`

### JWT 认证流程

```
1. POST /api/auth/login     → 返回 token + refreshToken
2. 后续请求 Header: Authorization: Bearer <token>
3. Token 过期后 POST /api/auth/refresh → 返回新 token
4. POST /api/auth/logout    → 退出登录
```

### 核心接口清单

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/auth/login` | 登录 | 公开 |
| POST | `/api/auth/register` | 注册 | 公开 |
| POST | `/api/auth/refresh` | 刷新令牌 | 公开 |
| GET | `/api/users` | 用户列表 | ROLE_ADMIN |
| POST | `/api/users` | 创建用户 | ROLE_ADMIN |
| PUT | `/api/users/{id}` | 更新用户 | ROLE_ADMIN |
| DELETE | `/api/users/{id}` | 删除用户 | ROLE_ADMIN |
| GET | `/api/goods` | 商品列表 | 登录 |
| POST | `/api/goods` | 创建商品 | 登录 |
| PUT | `/api/goods/{id}` | 更新商品 | 登录 |
| DELETE | `/api/goods/{id}` | 删除商品 | 登录 |
| GET | `/api/inventory` | 库存列表 | 登录 |
| PUT | `/api/inventory/config` | 库存配置 | 登录 |
| GET | `/api/inventory/alerts` | 库存预警 | 登录 |
| GET | `/api/inventory/overview` | 库存概览 | 登录 |
| POST | `/api/stock-in` | 创建入库单 | 登录 |
| PUT | `/api/stock-in/{id}/confirm` | 确认入库 | 登录 |
| PUT | `/api/stock-in/{id}/cancel` | 取消入库 | 登录 |
| POST | `/api/stock-out` | 创建出库单 | 登录 |
| PUT | `/api/stock-out/{id}/confirm` | 确认出库 | 登录 |
| PUT | `/api/stock-out/{id}/cancel` | 取消出库 | 登录 |
| GET | `/api/statistics/dashboard` | 仪表盘数据 | 登录 |
| GET | `/api/statistics/trend` | 出入趋势 | 登录 |
| GET | `/api/categories` | 分类列表 | 登录 |
| GET | `/api/suppliers` | 供应商列表 | 登录 |

### 测试用 curl 命令

```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 获取商品列表
curl http://localhost:8080/api/goods \
  -H "Authorization: Bearer YOUR_TOKEN"

# 创建入库单
curl -X POST http://localhost:8080/api/stock-in \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"goodsId":1,"quantity":50,"unitPrice":100,"totalPrice":5000}'

# 获取库存预警
curl http://localhost:8080/api/inventory/alerts \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 项目结构

```
warehouse-management/
├── backend/                          # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/java/com/warehouse/
│       ├── WarehouseApplication.java
│       ├── config/                   # 配置类
│       │   ├── SecurityConfig.java   # Spring Security + CORS
│       │   ├── RedisConfig.java      # Redis 配置
│       │   ├── MybatisPlusConfig.java # MyBatis-Plus（分页+乐观锁）
│       │   └── MetaObjectHandler.java # 自动填充
│       ├── security/                 # 安全组件
│       │   ├── JwtUtils.java         # JWT 工具类
│       │   ├── JwtAuthenticationFilter.java
│       │   ├── LoginUser.java        # UserDetails 实现
│       │   └── UserDetailsServiceImpl.java
│       ├── entity/                   # 数据实体
│       ├── dto/                      # 数据传输对象
│       ├── mapper/                   # MyBatis-Plus Mapper
│       ├── service/                  # 业务接口
│       │   └── impl/                 # 业务实现
│       ├── controller/               # REST 控制器
│       └── common/                   # 通用类
│           ├── Result.java           # 统一响应
│           ├── PageResult.java       # 分页响应
│           └── exception/            # 全局异常处理
├── frontend/                         # Vue3 前端
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── main.js
│       ├── App.vue
│       ├── router/                   # 路由配置
│       ├── store/                    # Pinia 状态管理
│       ├── utils/request.js          # Axios 封装 + 拦截器
│       ├── api/                      # 接口模块
│       ├── layout/                   # 布局组件
│       └── views/                    # 页面
│           ├── login/                # 登录注册
│           ├── dashboard/            # 数据大屏
│           ├── user/                 # 用户管理
│           ├── goods/                # 商品管理
│           ├── stockin/              # 入库管理
│           ├── stockout/             # 出库管理
│           ├── inventory/            # 库存管理
│           └── statistics/           # 数据统计
└── sql/
    ├── schema.sql                    # 建表语句
    └── seed.sql                      # 初始化数据
```

## 乐观锁并发方案

库存表 `wms_inventory` 使用 `version` 字段实现乐观锁：

```java
// 更新库存时带版本号条件
UPDATE wms_inventory
SET quantity = ?, version = version + 1
WHERE id = ? AND version = ?

// 更新失败自动重试（最多3次）
// 重试间隔 50ms
```

## 库存预警机制

- 每个商品可配置预警阈值 `alert_threshold`
- 当 `quantity <= alert_threshold` 时触发预警
- 仪表盘和库存管理页面显示预警数量
- 支持 `/api/inventory/alerts` 接口查询所有预警商品
