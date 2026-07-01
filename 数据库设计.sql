-- ============================================================
-- 电商商品管理模块 - 数据库设计
-- 版本：v1.0
-- 数据库：MySQL
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS ecms_product DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ecms_product;

-- ============================================================
-- 1. 品牌表
-- ============================================================
DROP TABLE IF EXISTS `brands`;
CREATE TABLE `brands` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '品牌ID',
    `brand_name`    VARCHAR(100)     NOT NULL                 COMMENT '品牌名称',
    `brand_logo`    VARCHAR(500)     DEFAULT NULL             COMMENT '品牌Logo图片URL',
    `brand_desc`    VARCHAR(500)     DEFAULT NULL             COMMENT '品牌描述',
    `sort_order`    INT              NOT NULL DEFAULT 0       COMMENT '排序值（越小越靠前）',
    `status`        TINYINT(1)       NOT NULL DEFAULT 1       COMMENT '状态：0-禁用，1-启用',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_brand_name` (`brand_name`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='品牌表';


-- ============================================================
-- 2. 商品分类表（三级分类）
-- ============================================================
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    `parent_id`     BIGINT UNSIGNED  NOT NULL DEFAULT 0       COMMENT '父分类ID（0表示顶级）',
    `level`         TINYINT(1)       NOT NULL DEFAULT 1       COMMENT '分类层级：1-一级，2-二级，3-三级',
    `cat_name`      VARCHAR(100)     NOT NULL                 COMMENT '分类名称',
    `cat_icon`      VARCHAR(500)     DEFAULT NULL             COMMENT '分类图标URL',
    `sort_order`    INT              NOT NULL DEFAULT 0       COMMENT '排序值',
    `status`        TINYINT(1)       NOT NULL DEFAULT 1       COMMENT '状态：0-禁用，1-启用',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_level` (`level`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';


-- ============================================================
-- 3. 标签表
-- ============================================================
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '标签ID',
    `tag_name`      VARCHAR(50)      NOT NULL                 COMMENT '标签名称',
    `tag_type`      TINYINT(1)       NOT NULL DEFAULT 1       COMMENT '标签类型：1-系统固定标签，2-自定义标签',
    `tag_color`     VARCHAR(20)      DEFAULT '#409EFF'        COMMENT '标签展示颜色',
    `sort_order`    INT              NOT NULL DEFAULT 0       COMMENT '排序值',
    `status`        TINYINT(1)       NOT NULL DEFAULT 1       COMMENT '状态：0-禁用，1-启用',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tag_name` (`tag_name`),
    KEY `idx_tag_type` (`tag_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';


-- ============================================================
-- 4. SPU商品主表
-- ============================================================
DROP TABLE IF EXISTS `spu`;
CREATE TABLE `spu` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT 'SPU主键ID',
    `spu_code`      VARCHAR(32)      NOT NULL                 COMMENT 'SPU编码（系统自动生成，全局唯一）',
    `spu_name`      VARCHAR(120)     NOT NULL                 COMMENT '商品名称（2-60字符）',
    `sub_title`     VARCHAR(200)     DEFAULT NULL             COMMENT '商品副标题',
    `description`   TEXT             DEFAULT NULL             COMMENT '商品简介',
    `brand_id`      BIGINT UNSIGNED  DEFAULT NULL             COMMENT '品牌ID',
    `category_id`   BIGINT UNSIGNED  NOT NULL                 COMMENT '三级分类ID',
    `product_type`  TINYINT(1)       NOT NULL DEFAULT 1       COMMENT '商品类型：1-实物商品，2-虚拟商品',
    -- 状态字段
    `status`        TINYINT(1)       NOT NULL DEFAULT 0       COMMENT '商品状态：0-草稿，1-待审核，2-已上架，3-已下架，4-审核驳回',
    `is_deleted`    TINYINT(1)       NOT NULL DEFAULT 0       COMMENT '是否删除：0-正常，1-已删除（回收站）',
    `deleted_at`    DATETIME         DEFAULT NULL             COMMENT '删除时间（用于回收站7天恢复）',
    -- 售后配置
    `seven_day_return` TINYINT(1)    NOT NULL DEFAULT 0       COMMENT '是否支持七天无理由：0-否，1-是',
    `warranty_desc` VARCHAR(500)     DEFAULT NULL             COMMENT '质保说明',
    `return_policy` TEXT             DEFAULT NULL             COMMENT '退换货规则',
    -- 物流配置（仅实物商品）
    `freight_template_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '运费模板ID',
    `is_free_shipping`   TINYINT(1)  NOT NULL DEFAULT 0       COMMENT '是否包邮：0-否，1-是',
    `ship_from`          VARCHAR(200) DEFAULT NULL            COMMENT '发货地',
    `ship_hours`         INT         DEFAULT NULL             COMMENT '发货时效（小时）',
    -- 审核信息
    `audit_status`  TINYINT(1)       DEFAULT NULL             COMMENT '审核状态：NULL-未提交审核，0-待审核，1-审核通过，2-审核驳回',
    `auditor_id`    BIGINT UNSIGNED  DEFAULT NULL             COMMENT '审核人ID',
    `audit_time`    DATETIME         DEFAULT NULL             COMMENT '审核时间',
    `reject_reason` VARCHAR(500)     DEFAULT NULL             COMMENT '驳回原因',
    -- 自动规则
    `auto_offline`  TINYINT(1)       NOT NULL DEFAULT 0       COMMENT '缺货自动下架开关：0-关闭，1-开启',
    -- 时间与操作人
    `created_by`    BIGINT UNSIGNED  NOT NULL                 COMMENT '创建人ID',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_spu_code` (`spu_code`),
    KEY `idx_status` (`status`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_brand_id` (`brand_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_spu_name` (`spu_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='SPU商品主表';


-- ============================================================
-- 5. SPU-标签关联表
-- ============================================================
DROP TABLE IF EXISTS `spu_tag`;
CREATE TABLE `spu_tag` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `spu_id`        BIGINT UNSIGNED  NOT NULL                 COMMENT 'SPU ID',
    `tag_id`        BIGINT UNSIGNED  NOT NULL                 COMMENT '标签ID',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_spu_tag` (`spu_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='SPU-标签关联表';


-- ============================================================
-- 6. SPU商品图片表
-- ============================================================
DROP TABLE IF EXISTS `spu_images`;
CREATE TABLE `spu_images` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `spu_id`        BIGINT UNSIGNED  NOT NULL                 COMMENT 'SPU ID',
    `image_type`    TINYINT(1)       NOT NULL DEFAULT 1       COMMENT '图片类型：1-主图，2-详情图，3-视频封面',
    `image_url`     VARCHAR(500)     NOT NULL                 COMMENT '图片URL',
    `video_url`     VARCHAR(500)     DEFAULT NULL             COMMENT '视频URL（仅视频类型）',
    `sort_order`    INT              NOT NULL DEFAULT 0       COMMENT '排序值（主图按此排序，首张为主图）',
    `is_cover`      TINYINT(1)       NOT NULL DEFAULT 0       COMMENT '是否首图：0-否，1-是',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_spu_id` (`spu_id`),
    KEY `idx_image_type` (`image_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='SPU商品图片表';


-- ============================================================
-- 7. 规格模板表
-- ============================================================
DROP TABLE IF EXISTS `spec_templates`;
CREATE TABLE `spec_templates` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '规格模板ID',
    `spec_name`     VARCHAR(50)      NOT NULL                 COMMENT '规格名称（如颜色、尺寸、型号）',
    `sort_order`    INT              NOT NULL DEFAULT 0       COMMENT '排序值',
    `status`        TINYINT(1)       NOT NULL DEFAULT 1       COMMENT '状态：0-禁用，1-启用',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_spec_name` (`spec_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='规格模板表';


-- ============================================================
-- 8. 规格值表
-- ============================================================
DROP TABLE IF EXISTS `spec_values`;
CREATE TABLE `spec_values` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '规格值ID',
    `spec_id`       BIGINT UNSIGNED  NOT NULL                 COMMENT '规格模板ID',
    `value_name`    VARCHAR(100)     NOT NULL                 COMMENT '规格值名称（如红色、M码）',
    `sort_order`    INT              NOT NULL DEFAULT 0       COMMENT '排序值',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_spec_value` (`spec_id`, `value_name`),
    KEY `idx_spec_id` (`spec_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='规格值表';


-- ============================================================
-- 9. SKU库存表
-- ============================================================
DROP TABLE IF EXISTS `sku`;
CREATE TABLE `sku` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT 'SKU主键ID',
    `sku_code`      VARCHAR(32)      NOT NULL                 COMMENT 'SKU编码（系统自动生成，全局唯一）',
    `spu_id`        BIGINT UNSIGNED  NOT NULL                 COMMENT '所属SPU ID',
    `spec_info`     VARCHAR(500)     NOT NULL                 COMMENT '规格信息（JSON格式，如{"颜色":"红色","尺寸":"M"}）',
    -- 价格字段
    `market_price`  DECIMAL(10,2)    NOT NULL DEFAULT 0.00    COMMENT '市场价',
    `sale_price`    DECIMAL(10,2)    NOT NULL DEFAULT 0.00    COMMENT '销售价',
    `member_price`  DECIMAL(10,2)    NOT NULL DEFAULT 0.00    COMMENT '会员价',
    `activity_price` DECIMAL(10,2)   DEFAULT NULL             COMMENT '活动价',
    `price_start_time` DATETIME      DEFAULT NULL             COMMENT '价格生效开始时间',
    `price_end_time`   DATETIME      DEFAULT NULL             COMMENT '价格生效结束时间',
    -- 库存字段
    `stock`         INT              NOT NULL DEFAULT 0       COMMENT '可用库存',
    `locked_stock`  INT              NOT NULL DEFAULT 0       COMMENT '锁定库存（下单未付款）',
    `warn_threshold` INT             NOT NULL DEFAULT 0       COMMENT '库存预警阈值',
    -- 状态
    `status`        TINYINT(1)       NOT NULL DEFAULT 1       COMMENT 'SKU状态：0-禁用/下架，1-启用/上架',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_code` (`sku_code`),
    KEY `idx_spu_id` (`spu_id`),
    KEY `idx_stock` (`stock`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='SKU库存表';


-- ============================================================
-- 10. 库存变动日志表
-- ============================================================
DROP TABLE IF EXISTS `inventory_log`;
CREATE TABLE `inventory_log` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '日志ID',
    `sku_id`        BIGINT UNSIGNED  NOT NULL                 COMMENT 'SKU ID',
    `spu_id`        BIGINT UNSIGNED  NOT NULL                 COMMENT 'SPU ID（冗余，便于查询）',
    `change_type`   TINYINT(1)       NOT NULL                 COMMENT '变动类型：1-新增入库，2-手动调整，3-下单锁定，4-订单释放，5-订单扣减，6-批量调整',
    `change_qty`    INT              NOT NULL                 COMMENT '变动数量（正数为增，负数为减）',
    `before_stock`  INT              NOT NULL DEFAULT 0       COMMENT '变动前库存',
    `after_stock`   INT              NOT NULL DEFAULT 0       COMMENT '变动后库存',
    `operator_id`   BIGINT UNSIGNED  DEFAULT NULL             COMMENT '操作人ID（系统操作为NULL）',
    `remark`        VARCHAR(500)     DEFAULT NULL             COMMENT '备注/变动原因',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变动时间',
    PRIMARY KEY (`id`),
    KEY `idx_sku_id` (`sku_id`),
    KEY `idx_spu_id` (`spu_id`),
    KEY `idx_change_type` (`change_type`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存变动日志表';


-- ============================================================
-- 11. 状态流转记录表
-- ============================================================
DROP TABLE IF EXISTS `status_log`;
CREATE TABLE `status_log` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '日志ID',
    `spu_id`        BIGINT UNSIGNED  NOT NULL                 COMMENT 'SPU ID',
    `from_status`   TINYINT(1)       NOT NULL                 COMMENT '变更前状态：0-草稿，1-待审核，2-已上架，3-已下架，4-审核驳回',
    `to_status`     TINYINT(1)       NOT NULL                 COMMENT '变更后状态：0-草稿，1-待审核，2-已上架，3-已下架，4-审核驳回',
    `operator_id`   BIGINT UNSIGNED  NOT NULL                 COMMENT '操作人ID',
    `remark`        VARCHAR(500)     DEFAULT NULL             COMMENT '备注',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_spu_id` (`spu_id`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='状态流转记录表';


-- ============================================================
-- 12. 审核记录表
-- ============================================================
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '日志ID',
    `spu_id`        BIGINT UNSIGNED  NOT NULL                 COMMENT 'SPU ID',
    `audit_result`  TINYINT(1)       NOT NULL                 COMMENT '审核结果：1-通过，2-驳回',
    `auditor_id`    BIGINT UNSIGNED  NOT NULL                 COMMENT '审核人ID',
    `reject_reason` VARCHAR(500)     DEFAULT NULL             COMMENT '驳回原因（审核通过时为NULL）',
    `audit_time`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
    PRIMARY KEY (`id`),
    KEY `idx_spu_id` (`spu_id`),
    KEY `idx_auditor_id` (`auditor_id`),
    KEY `idx_audit_time` (`audit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核记录表';


-- ============================================================
-- 13. 价格变动记录表
-- ============================================================
DROP TABLE IF EXISTS `price_log`;
CREATE TABLE `price_log` (
    `id`            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '日志ID',
    `sku_id`        BIGINT UNSIGNED  NOT NULL                 COMMENT 'SKU ID',
    `price_type`    VARCHAR(20)      NOT NULL                 COMMENT '价格类型：market_price/sale_price/member_price/activity_price',
    `before_price`  DECIMAL(10,2)    NOT NULL                 COMMENT '变动前价格',
    `after_price`   DECIMAL(10,2)    NOT NULL                 COMMENT '变动后价格',
    `operator_id`   BIGINT UNSIGNED  NOT NULL                 COMMENT '操作人ID',
    `created_at`    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变动时间',
    PRIMARY KEY (`id`),
    KEY `idx_sku_id` (`sku_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='价格变动记录表';


-- ============================================================
-- 初始化系统固定标签数据
-- ============================================================
INSERT INTO `tags` (`tag_name`, `tag_type`, `tag_color`, `sort_order`) VALUES
('新品',   1, '#FF6600', 1),
('热销',   1, '#FF0000', 2),
('爆款',   1, '#FF3366', 3),
('包邮',   1, '#00CC66', 4);


-- ============================================================
-- 说明书
-- ============================================================
-- 1. 所有表使用 InnoDB 引擎，支持事务和外键（外键在应用层控制）
-- 2. 字符集统一使用 utf8mb4，支持 emoji 等特殊字符
-- 3. 主键统一使用 BIGINT UNSIGNED 自增
-- 4. 所有价格字段使用 DECIMAL(10,2)，避免浮点精度问题
-- 5. 库存字段使用 INT，保证整数和性能
-- 6. 所有日志表不设外键，保证写入性能
-- 7. SPU和SKU编码由应用层生成（雪花算法或UUID），数据库仅做唯一约束
