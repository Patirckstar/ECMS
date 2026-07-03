# ECMS 电商商品管理系统

> **E-Commerce Product Management System** — 基于 Spring Boot 4 + Vue 3 + MySQL 9.5 的电商后台商品管理全栈项目。
>
> **🔥 云端部署状态**：后端已部署至阿里云服务器 `http://你的服务器IP:8080`，数据库与图片存储（OSS）均已上云，开箱即用。

---

## 目录

- [项目简介](#项目简介)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [页面功能](#页面功能)
- [API 概览](#api-概览)
- [数据库设计](#数据库设计)
- [常见问题](#常见问题)
- [开发计划](#开发计划)

---

## 项目简介

ECMS 是一个电商后台的**商品管理模块**，覆盖商品从创建、编辑、审核、上下架、库存管理到删除的全生命周期管理。
(虽然有用户管理模块，但是用户管理模块的功能与我们的项目需求相关性较小，因此未在项目中实现用户管理功能。)
### 核心功能

| 功能模块 | 说明 |
|----------|------|
| **商品管理** | 商品 CRUD，4 步分步表单（基础信息 / 素材 / 价格物流 / 售后） |
| **SKU 规格** | 多规格组合（颜色 / 尺寸 / 容量等），批量改价 / 改库存 / 启用禁用 |
| **状态流转** | 草稿 → 待审核 → 已上架 / 已下架 / 审核驳回，完整状态机 |
| **库存管理** | 库存调整、锁定库存、预警阈值、变动日志全记录 |
| **批量操作** | 批量上架 / 下架 / 删除 |
| **审核流程** | 提交审核 → 通过 / 驳回（含原因），审核记录留存 |
| **筛选查询** | 关键词、状态、分类、标签、价格区间、时间范围多条件筛选 |
| **图片上传** | 阿里云 OSS 对象存储，支持多图上传与缩略图展示 |

---

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| **前端框架** | Vue 3 + Vite | ^3.5 / ^8.1 |
| **UI 组件库** | Element Plus | ^2.9 |
| **状态管理** | Pinia | ^3.0 |
| **路由** | Vue Router | ^4.5 |
| **HTTP** | Axios | ^1.7 |
| **后端框架** | Spring Boot | 4.0.7 |
| **ORM** | MyBatis (注解模式) | 4.0.1 |
| **数据库** | MySQL | 9.5 |
| **JDBC** | MySQL Connector/J | 9.7.0 |
| **对象存储** | 阿里云 OSS SDK | 3.17.4 |
| **构建** | Maven (自带 wrapper) | - |
| **语言** | TypeScript / Java 21 | - |

---

## 项目结构

```
ECMS/
├── ECMS_Front/                         # 前端项目
│   └── src/
│       ├── api/                        # API 接口封装层
│       │   ├── product.ts
│       │   ├── category.ts
│       │   ├── brand.ts
│       │   └── tag.ts
│       ├── types/product.ts            # TS 类型定义
│       ├── layout/MainLayout.vue       # 后台主布局（左侧菜单 + 顶部导航）
│       ├── components/product/         # 通用组件
│       │   └── StatusTag.vue           # 商品状态彩色标签
│       ├── views/product/              # ★ 4 个核心页面
│       │   ├── ProductList.vue         # 商品列表页
│       │   ├── ProductForm.vue         # 商品新增/编辑页（含图片上传）
│       │   ├── SkuConfig.vue           # SKU 规格配置页
│       │   └── InventoryManage.vue     # 库存管理与日志页
│       ├── router/index.ts             # 路由
│       ├── utils/request.ts            # Axios 封装
│       └── stores/counter.ts
│
├── ecms_backend/                       # 后端项目
│   └── src/main/java/com/ecms_backend/
│       ├── common/                     # 通用
│       │   ├── ApiResult.java          # 统一响应 {code, message, data}
│       │   ├── PageResult.java         # 分页
│       │   └── GlobalExceptionHandler.java
│       ├── config/
│       │   ├── CorsConfig.java         # CORS 跨域
│       │   └── WebMvcConfig.java       # Web 配置
│       ├── entity/                     # ★ 13 个实体类
│       ├── mapper/                     # ★ 13 个 Mapper（注解 SQL）
│       ├── service/                    # ★ 7 个 Service
│       │   └── OssService.java         # OSS 对象存储服务
│       └── controller/                 # ★ 7 个 Controller（28 个 API）
│           └── UploadController.java   # 图片/视频上传接口
│
├── 数据库设计.sql                       # 建表脚本（13 张表）
├── 测试数据.sql                         # 测试数据（8 个 SPU / 24 个 SKU）
├── 测试数据2.sql                        # 补充测试数据（16 个 SPU / 49 个 SKU）
├── 需求规约.md                          # 需求规格文档
├── 界面UI设计.md                        # UI 设计说明文档
└── README.md                           # 本文件
```

---

## 快速开始

### 使用方式

ECMS 提供两种使用方式：

| 方式 | 说明 | 适用场景 |
|------|------|----------|
| **☁️ 连接云端（推荐）** | 前端直连已部署的云服务器后端，无需启动本地后端 | 体验/演示 |
| **💻 本地全栈开发** | 本地启动后端 + 前端，完整开发环境 | 二次开发/调试 |

---

### 方式一：☁️ 连接云端（开箱即用）

后端、数据库、OSS 均已部署在阿里云，只需启动前端即可使用。

```powershell
cd ECMS_Front
npm install
npm run dev
```

前端启动后访问 [http://localhost:5173](http://localhost:5173)，自动连接云端后端 `http://你的服务器IP:8080`。

---

### 方式二：💻 本地全栈开发

#### 前置条件

- JDK 21+
- Node.js 18+
- MySQL 9.5（8.0+ 也可兼容）
- Maven（项目内置 `mvnw.cmd`，无需手动安装）

#### 1. 修改配置为本地环境

**数据库连接**（打开 `ecms_backend/src/main/resources/application.properties`）：

```properties
# 将云服务器地址改为本地
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/ecms_product?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码
```

**前端 API 地址**（打开 `ECMS_Front/src/utils/request.ts`）：

```typescript
// 将云服务器地址改为本地
baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
```

#### 2. 初始化数据库

**创建数据库表**：

```powershell
cd d:\Desktop\实验课\智能软件开发\ECMS
Get-Content "数据库设计.sql" | mysql -u root -p ecms_product
```

**插入测试数据**：

```powershell
Get-Content "测试数据.sql" | mysql -u root -p ecms_product
Get-Content "测试数据2.sql" | mysql -u root -p ecms_product
```

#### 3. 配置阿里云 OSS（可选）

如需使用图片上传功能，需配置阿里云 OSS：

```properties
aliyun.oss.endpoint=https://oss-cn-guangzhou.aliyuncs.com
aliyun.oss.bucket-name=你的Bucket名称
aliyun.oss.access-key-id=你的AccessKeyId
aliyun.oss.access-key-secret=你的AccessKeySecret
```

#### 4. 启动后端

```powershell
cd ecms_backend
.\mvnw.cmd spring-boot:run
```

后端启动在 → [http://localhost:8080](http://localhost:8080)

> 首次启动会自动下载 Maven 依赖，耗时约 1-2 分钟。

#### 5. 启动前端

```powershell
cd ECMS_Front
npm install
npm run dev
```

前端启动在 → [http://localhost:5173](http://localhost:5173)

#### 6. 验证

访问 [http://localhost:5173](http://localhost:5173)，应该能看到后台商品管理页面。

---

## 云服务器部署

后端已成功部署至阿里云服务器（你的服务器IP），数据库和 OSS 均已上云。以下为部署步骤摘要。

### 环境

| 项目 | 配置 |
|------|------|
| **服务器** | 阿里云 ECS（CentOS 7），公网 IP: `你的服务器IP` |
| **JDK** | `21.0.2`（宝塔面板安装，路径 `/www/server/java/jdk-21.0.2`） |
| **MySQL** | 阿里云 RDS / 自建 MySQL（已配置远程访问） |
| **对象存储** | 阿里云 OSS，Bucket: `你的Bucket名称`（广州，公共读） |

### 部署步骤

1. **打包后端**：
   ```powershell
   cd ecms_backend
   .\mvnw.cmd clean package -DskipTests
   ```
   生成的 JAR 包位于 `target/ECMS_Backend-0.0.1-SNAPSHOT.jar`

2. **上传至服务器**（使用宝塔面板或 scp）：
   ```bash
   # 本地执行（PowerShell）
   scp ecms_backend/target/ECMS_Backend-0.0.1-SNAPSHOT.jar root@你的服务器IP:/opt/ecms/
   ```

3. **启动服务**：
   ```bash
   # 服务器上执行
   mkdir -p /opt/ecms/logs
   nohup java -jar /opt/ecms/ECMS_Backend-0.0.1-SNAPSHOT.jar > /opt/ecms/logs/app.log 2>&1 &
   ```

4. **配置安全组**（阿里云控制台 → 安全组 → 添加规则）：
   - 协议: TCP
   - 端口: 8080
   - 授权对象: 0.0.0.0/0

5. **验证**：
   访问 [http://你的服务器IP:8080/api/products](http://你的服务器IP:8080/api/products) 查看商品列表数据。

### 管理服务

**使用 systemd 实现开机自启**（推荐）：

```bash
# 创建 systemd 服务
cat > /etc/systemd/system/ecms.service << 'EOF'
[Unit]
Description=ECMS Backend Service
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/ecms
ExecStart=/www/server/java/jdk-21.0.2/bin/java -jar /opt/ecms/ECMS_Backend-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

# 启用并启动
systemctl daemon-reload
systemctl enable ecms
systemctl start ecms
```

---

## 页面功能

| 页面 | 路由 | 功能要点 |
|------|------|----------|
| **商品列表** | `/product` | 多条件筛选 / 模糊搜索 / 分页 / 批量上下架删除 / 缩略图展示 / 状态标签 |
| **新增商品** | `/product/add` | 4 步分步表单：基础信息 → 素材（图片上传）→ 价格物流 → 售后 |
| **编辑商品** | `/product/edit/:id` | 同新增，预填已有数据（含图片回显） |
| **SKU 配置** | `/product/:id/sku` | SKU 明细表格 / 批量改价 / 批量改库存 / 批量启用禁用 / 新增 SKU |
| **库存管理** | `/product/:id/inventory` | 3 个 Tab：库存概况 / 库存日志 / 状态流转 |

---

## API 概览

共 **28 个 REST API**，统一返回格式 `{ code, message, data }`。

### 商品 API

| 方法 | 端点 | 说明 |
|------|------|------|
| `GET` | `/api/products` | 分页列表（支持 keyword/status/categoryId/tagId/价格区间/时间，含图片） |
| `GET` | `/api/products/{id}` | 商品详情（含图片、标签） |
| `POST` | `/api/products` | 新增商品（自动生成 SPU 编码） |
| `PUT` | `/api/products/{id}` | 更新商品 |
| `DELETE` | `/api/products/{id}` | 软删除（进入回收站） |
| `PUT` | `/api/products/{id}/on-shelf` | 上架 |
| `PUT` | `/api/products/{id}/off-shelf` | 下架 |
| `PUT` | `/api/products/{id}/submit-audit` | 提交审核 |
| `PUT` | `/api/products/{id}/audit/approve` | 审核通过 |
| `PUT` | `/api/products/{id}/audit/reject` | 审核驳回（需传 rejectReason） |
| `POST` | `/api/products/batch-on-shelf` | 批量上架 |
| `POST` | `/api/products/batch-off-shelf` | 批量下架 |
| `POST` | `/api/products/batch-delete` | 批量删除 |

### SKU 与库存 API

| 方法 | 端点 | 说明 |
|------|------|------|
| `GET` | `/api/products/{spuId}/skus` | SKU 列表 |
| `POST` | `/api/products/{spuId}/skus` | 新增 SKU |
| `PUT` | `/api/products/{spuId}/skus/{skuId}` | 更新单个 SKU |
| `PUT` | `/api/products/{spuId}/skus/batch` | 批量更新 SKU |
| `POST` | `/api/products/{spuId}/skus/{skuId}/adjust-stock` | 调整库存 |
| `POST` | `/api/products/{spuId}/skus/batch-adjust-stock` | 批量调整库存 |
| `GET` | `/api/products/{spuId}/inventory` | 库存列表 |
| `GET` | `/api/products/{spuId}/inventory-logs` | 库存日志（支持分页 + changeType 筛选） |
| `GET` | `/api/products/{spuId}/status-logs` | 状态流转记录 |
| `GET` | `/api/products/{spuId}/audit-logs` | 审核记录 |

### 上传 API

| 方法 | 端点 | 说明 |
|------|------|------|
| `POST` | `/api/upload/image` | 上传图片到阿里云 OSS |
| `POST` | `/api/upload/video` | 上传视频到阿里云 OSS |

### 基础数据 API

| 方法 | 端点 | 说明 |
|------|------|------|
| `GET` | `/api/categories/tree` | 三级分类树 |
| `GET` | `/api/categories` | 分类列表 |
| `GET` | `/api/brands` | 品牌列表 |
| `GET` | `/api/tags` | 标签列表 |

---

## 数据库设计

共 **13 张表**，使用 InnoDB 引擎，统一 `utf8mb4` 字符集，主键使用 `BIGINT UNSIGNED AUTO_INCREMENT`。

```
brands                    ─────────────        spu_tag
categories                              │          │
tags                                    │          │
spec_templates ─── spec_values           ├── spu ──┤
                                        │          │
sku ─────── inventory_log               │    spu_images
│                                       │
└── price_log                           │
                              status_log│
                              audit_log─┘
```

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `brands` | 品牌 | brand_name, brand_logo, sort_order |
| `categories` | 三级分类 | parent_id, level, cat_name |
| `tags` | 标签（系统/自定义） | tag_name, tag_type, tag_color |
| **`spu`** | 商品主表 | spu_code, spu_name, status, 审核/物流/售后字段 |
| `spu_tag` | 商品-标签关联 | spu_id, tag_id |
| `spu_images` | 商品图片 | image_type(主图/详情图), is_cover(首图), image_url(OSS地址) |
| `spec_templates` | 规格模板 | spec_name(颜色/尺寸/容量等) |
| `spec_values` | 规格值 | spec_id, value_name |
| **`sku`** | SKU 库存表 | sku_code, 4 种价格, stock, locked_stock, warn_threshold, status(启用/禁用) |
| `inventory_log` | 库存日志 | change_type(6 种), change_qty, 前后库存 |
| `status_log` | 状态流转 | from_status, to_status, operator_id |
| `audit_log` | 审核记录 | audit_result, auditor_id, reject_reason |
| `price_log` | 价格变动 | price_type, before_price, after_price |

---

## 常见问题

### Q: 后端启动报 "Failed to obtain JDBC Connection"

原因：Connector/J 9.x 不支持 `characterEncoding=utf8mb4`（用 `UTF-8` 或删掉该参数）。配置文件已移除该参数，如仍有问题请检查：
1. MySQL 服务是否启动
2. 密码是否正确
3. 数据库 `ecms_product` 是否存在

### Q: Git 推送失败 "Could not connect to server"

通常是 GitHub 网络问题：
- 配置代理：`git config --global http.proxy http://127.0.0.1:7890`
- 或切换网络（手机热点）
- 或 `git config --global http.sslverify false`

### Q: 前端开发服务器自动打开页面失败

手动访问 [http://localhost:5173](http://localhost:5173) 即可。

### Q: 图片上传失败 "AccessDenied"

请检查阿里云 OSS 配置：
1. Bucket 是否设置为公共读
2. RAM 子用户是否有 `AliyunOSSFullAccess` 权限
3. AccessKeyId 和 AccessKeySecret 是否正确

### Q: 访问云服务器 IP:8080 超时

1. 检查阿里云安全组是否开放了 TCP:8080 端口
2. 检查宝塔面板或服务器防火墙是否放行 8080 端口
3. 确认后端服务是否正常运行：`ps -ef | grep ECMS_Backend`

### Q: 云服务器重启后端需要重新启动

推荐配置 systemd 开机自启（见上方「云服务器部署 → 管理服务」）。或用 `nohup` 启动后，即使关闭 SSH 终端服务也不会停止。

### Q: 如何查看后端运行日志？

```bash
# 查看实时日志
tail -f /opt/ecms/logs/app.log

# 查看 systemd 日志
journalctl -u ecms -f
```

---

## 开发计划

```
第一阶段 ✅ 已完成
├── 需求规约 / UI 设计 / 数据库设计文档
├── 前端 4 个核心页面（列表 / 表单 / SKU / 库存）
├── 后端 28 个 REST API + 完整数据库操作
└── 前后端联调验证通过

第二阶段 ✅ 已完成
├── 图片/视频上传功能（对接阿里云 OSS 对象存储）
├── SKU 启用/禁用完整过滤逻辑
├── SKU 规格新增与编辑
├── 商品列表缩略图展示
└── 云服务器部署（后端 + 数据库 + OSS 全量上云）

第三阶段 ◻ 规划中
├── 用户登录与 JWT 权限控制
├── 商品数据统计报表（ECharts）
├── 富文本编辑器（退换货规则字段）
├── 单元测试覆盖（后端 + 前端）
└── CI/CD 自动化部署（Docker + GitHub Actions）
```

---

> **相关文档**：[需求规约](需求规约.md) | [界面UI设计](界面UI设计.md) | [数据库设计](数据库设计.sql)
>
> **项目地址**：[https://github.com/Patirckstar/ECMS](https://github.com/Patirckstar/ECMS)