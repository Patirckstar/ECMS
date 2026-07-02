
-- ============================================================
-- 电商商品管理模块 - 测试数据插入脚本
-- 运行方式：Get-Content 测试数据.sql | mysql -u root -p2400320210 ecms_product
-- ============================================================
USE ecms_product;

-- ============================================================
-- 1. 品牌（5条）
-- ============================================================
INSERT INTO brands (brand_name, brand_logo, brand_desc, sort_order, status) VALUES
('Apple', '/static/logo/apple.png', '美国苹果公司', 1, 1),
('华为', '/static/logo/huawei.png', '华为技术有限公司', 2, 1),
('小米', '/static/logo/xiaomi.png', '小米科技', 3, 1),
('Nike', '/static/logo/nike.png', '耐克全球运动品牌', 4, 1),
('良品铺子', '/static/logo/liangpin.png', '休闲零食品牌', 5, 1);

-- ============================================================
-- 2. 三级分类（15条：5一级 + 5二级 + 5三级）
-- ============================================================
INSERT INTO categories (parent_id, level, cat_name, sort_order, status) VALUES
(0, 1, '手机数码', 1, 1),
(0, 1, '服装鞋帽', 2, 1),
(0, 1, '食品饮料', 3, 1),
(0, 1, '家用电器', 4, 1),
(0, 1, '运动户外', 5, 1);

INSERT INTO categories (parent_id, level, cat_name, sort_order, status) VALUES
(1, 2, '智能手机', 1, 1),
(2, 2, '男装', 1, 1),
(3, 2, '休闲零食', 1, 1),
(4, 2, '厨房电器', 1, 1),
(5, 2, '运动鞋', 1, 1);

INSERT INTO categories (parent_id, level, cat_name, sort_order, status) VALUES
(6, 3, '5G手机', 1, 1),
(7, 3, 'T恤', 1, 1),
(8, 3, '坚果炒货', 1, 1),
(9, 3, '电饭煲', 1, 1),
(10, 3, '跑步鞋', 1, 1);

-- ============================================================
-- 3. 自定义标签（补充已有的4条系统标签）
-- ============================================================
INSERT INTO tags (tag_name, tag_type, tag_color, sort_order) VALUES
('限时特惠', 2, '#FF4500', 5),
('会员专享', 2, '#8B00FF', 6);

-- ============================================================
-- 4. 规格模板（4个）
-- ============================================================
INSERT INTO spec_templates (spec_name, sort_order) VALUES
('颜色', 1), ('尺寸', 2), ('容量', 3), ('口味', 4);

-- ============================================================
-- 5. 规格值（每个模板4个值）
-- ============================================================
INSERT INTO spec_values (spec_id, value_name, sort_order) VALUES
(1, '深空黑', 1), (1, '星光白', 2), (1, '远峰蓝', 3), (1, '暗夜绿', 4),
(2, 'S', 1), (2, 'M', 2), (2, 'L', 3), (2, 'XL', 4),
(3, '128GB', 1), (3, '256GB', 2), (3, '512GB', 3), (3, '1TB', 4),
(4, '原味', 1), (4, '焦糖味', 2), (4, '奶香味', 3), (4, '麻辣味', 4);

-- ============================================================
-- 6. SPU商品（8条，覆盖5种状态）
-- ============================================================
INSERT INTO spu (spu_code, spu_name, sub_title, description, brand_id, category_id,
    product_type, status, is_deleted, freight_template_id, is_free_shipping,
    ship_from, ship_hours, audit_status, created_by)
VALUES
('SPU20240101001', 'Apple iPhone 16 Pro Max',
 'A18 Pro芯片，钛金属设计',
 '搭载A18 Pro芯片，6.9英寸超视网膜XDR显示屏，4800万像素主摄。',
 1, 11, 1, 2, 0, 1, 0, '广东省深圳市', 24, 1, 1001),

('SPU20240101002', '华为 Mate 70 Pro',
 '麒麟9100，卫星通信',
 '搭载麒麟9100芯片，支持北斗卫星消息，XMAGE影像系统。',
 2, 11, 1, 2, 0, 1, 1, '广东省深圳市', 24, 1, 1001),

('SPU20240101003', '小米智能电饭煲 4L',
 'IH电磁加热，24小时预约',
 'IH立体加热技术，4L容量，支持米家APP远程控制。',
 3, 14, 1, 2, 0, 2, 1, '北京市', 48, 1, 1002),

('SPU20240101004', 'Nike Air Zoom Pegasus 41',
 '全掌Zoom Air，轻盈缓震',
 '搭载全掌Zoom Air气垫，React泡棉中底，透气飞织鞋面。',
 4, 15, 1, 2, 0, 3, 0, '上海市', 24, 1, 1003),

('SPU20240101005', '良品铺子 每日坚果 750g',
 '7种坚果果干，科学配比',
 '甄选7种优质坚果果干，独立小包装，每日一袋。',
 5, 13, 1, 2, 0, 4, 1, '湖北省武汉市', 24, 1, 1002),

('SPU20240101006', '良品铺子 手撕牛肉干 500g',
 '内蒙古风干牛肉，香辣可口',
 '精选内蒙古优质牛后腿肉，传统风干工艺。',
 5, 13, 1, 0, 0, 4, 1, '湖北省武汉市', 24, NULL, 1002),

('SPU20240101007', '小米 Redmi Note 14 Pro',
 '天玑8300，2亿像素',
 '天玑8300-Ultra处理器，2亿像素OIS主摄，5100mAh电池。',
 3, 11, 1, 1, 0, 1, 1, '北京市', 48, 0, 1002),

('SPU20240101008', '华为 FreeBuds Pro 4',
 '主动降噪，Hi-Res音质',
 'LDAC高清音频，智慧动态降噪，星闪连接技术。',
 2, 11, 1, 3, 0, 1, 1, '广东省深圳市', 24, 1, 1001);

-- ============================================================
-- 7. SPU-标签关联
-- ============================================================
INSERT INTO spu_tag (spu_id, tag_id) VALUES
(1, 2), (1, 1), (2, 2), (2, 3), (3, 1), (3, 4),
(4, 2), (4, 6), (5, 3), (5, 4), (5, 5),
(6, 1), (7, 1), (7, 5), (8, 3);

-- ============================================================
-- 8. 商品图片
-- ============================================================
INSERT INTO spu_images (spu_id, image_type, image_url, sort_order, is_cover) VALUES
(1, 1, '/static/product/iphone16_01.jpg', 1, 1),
(1, 1, '/static/product/iphone16_02.jpg', 2, 0),
(1, 1, '/static/product/iphone16_03.jpg', 3, 0),
(1, 2, '/static/product/iphone16_detail.jpg', 1, 0),
(2, 1, '/static/product/huawei_mate70_01.jpg', 1, 1),
(2, 1, '/static/product/huawei_mate70_02.jpg', 2, 0),
(3, 1, '/static/product/mi_cooker_01.jpg', 1, 1),
(4, 1, '/static/product/nike_pegasus41_01.jpg', 1, 1),
(4, 1, '/static/product/nike_pegasus41_02.jpg', 2, 0),
(4, 1, '/static/product/nike_pegasus41_03.jpg', 3, 0),
(5, 1, '/static/product/nuts_01.jpg', 1, 1),
(5, 2, '/static/product/nuts_detail.jpg', 1, 0),
(6, 1, '/static/product/beef_jerky_01.jpg', 1, 1),
(7, 1, '/static/product/redmi14_01.jpg', 1, 1),
(7, 1, '/static/product/redmi14_02.jpg', 2, 0),
(8, 1, '/static/product/freebuds4_01.jpg', 1, 1);

-- ============================================================
-- 9. SKU库存（24条，覆盖正常/缺货/禁用等状态）
-- ============================================================
-- iPhone: 颜色×容量 → 6个SKU
INSERT INTO sku (sku_code, spu_id, spec_info, market_price, sale_price, member_price,
    activity_price, price_start_time, price_end_time, stock, locked_stock, warn_threshold, status) VALUES
('SKU001', 1, '{"颜色":"深空黑","容量":"256GB"}', 10999.00, 9999.00, 9799.00, NULL, NULL, NULL, 150, 5, 10, 1),
('SKU002', 1, '{"颜色":"深空黑","容量":"512GB"}', 12999.00, 11999.00, 11699.00, NULL, NULL, NULL, 80, 3, 10, 1),
('SKU003', 1, '{"颜色":"星光白","容量":"256GB"}', 10999.00, 9999.00, 9799.00, NULL, NULL, NULL, 120, 2, 10, 1),
('SKU004', 1, '{"颜色":"星光白","容量":"512GB"}', 12999.00, 11999.00, 11699.00, NULL, NULL, NULL, 60, 1, 10, 1),
('SKU005', 1, '{"颜色":"远峰蓝","容量":"256GB"}', 10999.00, 9999.00, 9799.00, 9499.00, '2024-07-01', '2024-07-15', 200, 8, 10, 1),
('SKU006', 1, '{"颜色":"远峰蓝","容量":"512GB"}', 12999.00, 11999.00, 11699.00, NULL, NULL, NULL, 0, 0, 5, 1);

-- 华为 Mate 70 Pro
INSERT INTO sku (sku_code, spu_id, spec_info, market_price, sale_price, member_price,
    activity_price, price_start_time, price_end_time, stock, locked_stock, warn_threshold, status) VALUES
('SKU007', 2, '{"颜色":"深空黑","容量":"256GB"}', 7999.00, 6999.00, 6799.00, NULL, NULL, NULL, 200, 10, 20, 1),
('SKU008', 2, '{"颜色":"深空黑","容量":"512GB"}', 8999.00, 7999.00, 7799.00, NULL, NULL, NULL, 100, 5, 10, 1),
('SKU009', 2, '{"颜色":"暗夜绿","容量":"256GB"}', 7999.00, 6999.00, 6799.00, NULL, NULL, NULL, 3, 0, 5, 1),
('SKU010', 2, '{"颜色":"暗夜绿","容量":"512GB"}', 8999.00, 7999.00, 7799.00, NULL, NULL, NULL, 50, 0, 5, 0);

-- 小米电饭煲 单SKU
INSERT INTO sku (sku_code, spu_id, spec_info, market_price, sale_price, member_price,
    activity_price, price_start_time, price_end_time, stock, locked_stock, warn_threshold, status) VALUES
('SKU011', 3, '{"颜色":"白色"}', 499.00, 399.00, 369.00, 349.00, '2024-07-01', '2024-07-31', 500, 12, 30, 1);

-- Nike 跑鞋: 颜色×尺寸 → 6个SKU
INSERT INTO sku (sku_code, spu_id, spec_info, market_price, sale_price, member_price,
    activity_price, price_start_time, price_end_time, stock, locked_stock, warn_threshold, status) VALUES
('SKU012', 4, '{"颜色":"黑白","尺寸":"M"}',  999.00, 899.00, 849.00, NULL, NULL, NULL, 80, 3, 10, 1),
('SKU013', 4, '{"颜色":"黑白","尺寸":"L"}',  999.00, 899.00, 849.00, NULL, NULL, NULL, 65, 2, 10, 1),
('SKU014', 4, '{"颜色":"黑白","尺寸":"XL"}', 999.00, 899.00, 849.00, NULL, NULL, NULL, 40, 1, 5, 1),
('SKU015', 4, '{"颜色":"灰白","尺寸":"M"}',  999.00, 899.00, 849.00, NULL, NULL, NULL, 70, 4, 10, 1),
('SKU016', 4, '{"颜色":"灰白","尺寸":"L"}',  999.00, 899.00, 849.00, NULL, NULL, NULL, 55, 2, 10, 1),
('SKU017', 4, '{"颜色":"灰白","尺寸":"XL"}', 999.00, 899.00, 849.00, NULL, NULL, NULL, 0, 0, 5, 0);

-- 每日坚果: 口味
INSERT INTO sku (sku_code, spu_id, spec_info, market_price, sale_price, member_price,
    activity_price, price_start_time, price_end_time, stock, locked_stock, warn_threshold, status) VALUES
('SKU018', 5, '{"口味":"原味"}',   149.00, 129.00, 119.00, NULL,    NULL, NULL,           500, 20, 50, 1),
('SKU019', 5, '{"口味":"奶香味"}', 149.00, 129.00, 119.00, 109.00, '2024-07-01', '2024-07-10', 300, 15, 50, 1);

-- 牛肉干（草稿）
INSERT INTO sku (sku_code, spu_id, spec_info, market_price, sale_price, member_price,
    activity_price, price_start_time, price_end_time, stock, locked_stock, warn_threshold, status) VALUES
('SKU020', 6, '{"口味":"麻辣味"}', 89.00, 69.00, 59.00, NULL, NULL, NULL, 200, 0, 20, 1),
('SKU021', 6, '{"口味":"原味"}',   89.00, 69.00, 59.00, NULL, NULL, NULL, 150, 0, 20, 1);

-- Redmi（待审核）
INSERT INTO sku (sku_code, spu_id, spec_info, market_price, sale_price, member_price,
    activity_price, price_start_time, price_end_time, stock, locked_stock, warn_threshold, status) VALUES
('SKU022', 7, '{"颜色":"深空黑","容量":"256GB"}', 2499.00, 1999.00, 1899.00, NULL, NULL, NULL, 300, 0, 20, 1),
('SKU023', 7, '{"颜色":"星光白","容量":"512GB"}', 2999.00, 2499.00, 2399.00, NULL, NULL, NULL, 180, 0, 20, 1);

-- FreeBuds（已下架）
INSERT INTO sku (sku_code, spu_id, spec_info, market_price, sale_price, member_price,
    activity_price, price_start_time, price_end_time, stock, locked_stock, warn_threshold, status) VALUES
('SKU024', 8, '{"颜色":"陶瓷白"}', 1599.00, 1399.00, 1299.00, NULL, NULL, NULL, 100, 0, 10, 0);

-- ============================================================
-- 10. 库存变动日志（10条）
-- ============================================================
INSERT INTO inventory_log (sku_id, spu_id, change_type, change_qty, before_stock, after_stock, operator_id, remark, created_at) VALUES
(1,  1, 1, 150,   0, 150, 1001, '首次入库',         '2024-01-15 10:00:00'),
(1,  1, 2,  10, 140, 150, 1001, '补货入库',         '2024-03-20 14:30:00'),
(5,  1, 1, 200,   0, 200, 1001, '活动备货入库',     '2024-06-28 09:00:00'),
(6,  1, 2, -50,  50,   0, 1001, '盘点调整',         '2024-06-15 16:00:00'),
(11, 3, 3,  -2, 512, 510, NULL, NULL,               '2024-07-01 10:00:00'),
(12, 4, 5,  -1,  85,  84, NULL, NULL,               '2024-07-01 09:15:00'),
(18, 5, 1, 500,   0, 500, 1002, '首次入库',         '2024-01-20 11:00:00'),
(18, 5, 3,  -3, 500, 497, NULL, NULL,               '2024-07-01 08:30:00'),
(19, 5, 1, 300,   0, 300, 1002, '活动备货',         '2024-06-25 15:00:00'),
(9,  2, 2, 100,   3, 103, 1001, '库存预警补货',     '2024-06-30 11:00:00');

-- ============================================================
-- 11. 状态流转记录（25条）
-- ============================================================
INSERT INTO status_log (spu_id, from_status, to_status, operator_id, remark, created_at) VALUES
-- iPhone: 创建→草稿→审核→上架
(1, 0, 0, 1001, '创建商品', '2024-01-15 10:00:00'),
(1, 0, 1, 1001, '提交审核', '2024-01-15 11:00:00'),
(1, 1, 2, 2001, '审核通过', '2024-01-15 14:30:00'),
-- 华为
(2, 0, 0, 1001, '创建商品', '2024-01-18 09:00:00'),
(2, 0, 1, 1001, '提交审核', '2024-01-18 10:30:00'),
(2, 1, 2, 2001, '审核通过', '2024-01-18 15:00:00'),
-- 电饭煲: 创建→驳回→修改→重新审核→上架
(3, 0, 0, 1002, '创建商品', '2024-02-01 09:00:00'),
(3, 0, 1, 1002, '提交审核', '2024-02-01 10:00:00'),
(3, 1, 4, 2001, '审核驳回：图片尺寸不符合要求', '2024-02-01 14:00:00'),
(3, 4, 0, 1002, '修改保存草稿', '2024-02-02 09:00:00'),
(3, 0, 1, 1002, '重新提交审核', '2024-02-02 10:00:00'),
(3, 1, 2, 2001, '审核通过', '2024-02-02 15:30:00'),
-- Nike跑鞋
(4, 0, 0, 1003, '创建商品', '2024-03-01 08:00:00'),
(4, 0, 1, 1003, '提交审核', '2024-03-01 09:00:00'),
(4, 1, 2, 2002, '审核通过', '2024-03-01 14:00:00'),
-- 坚果
(5, 0, 0, 1002, '创建商品', '2024-01-20 10:00:00'),
(5, 0, 1, 1002, '提交审核', '2024-01-20 10:30:00'),
(5, 1, 2, 2002, '审核通过', '2024-01-20 15:00:00'),
-- 牛肉干（仅草稿）
(6, 0, 0, 1002, '创建商品(草稿)', '2024-06-30 16:00:00'),
-- Redmi（待审核）
(7, 0, 0, 1002, '创建商品', '2024-07-01 08:00:00'),
(7, 0, 1, 1002, '提交审核', '2024-07-01 08:30:00'),
-- FreeBuds: 创建→上架→下架
(8, 0, 0, 1001, '创建商品', '2024-04-01 09:00:00'),
(8, 0, 1, 1001, '提交审核', '2024-04-01 10:00:00'),
(8, 1, 2, 2001, '审核通过', '2024-04-01 14:00:00'),
(8, 2, 3, 1001, '库存售罄手动下架', '2024-06-30 10:00:00');

-- ============================================================
-- 12. 审核记录（7条，含1条驳回）
-- ============================================================
INSERT INTO audit_log (spu_id, audit_result, auditor_id, reject_reason, audit_time) VALUES
(1, 1, 2001, NULL,                                          '2024-01-15 14:30:00'),
(2, 1, 2001, NULL,                                          '2024-01-18 15:00:00'),
(3, 2, 2001, '图片尺寸不符合要求，请上传≥800×800的主图',     '2024-02-01 14:00:00'),
(3, 1, 2001, NULL,                                          '2024-02-02 15:30:00'),
(4, 1, 2002, NULL,                                          '2024-03-01 14:00:00'),
(5, 1, 2002, NULL,                                          '2024-01-20 15:00:00'),
(8, 1, 2001, NULL,                                          '2024-04-01 14:00:00');

-- ============================================================
-- 13. 价格变动记录（4条）
-- ============================================================
INSERT INTO price_log (sku_id, price_type, before_price, after_price, operator_id, created_at) VALUES
(5,  'activity_price',    0.00,  9499.00, 1001, '2024-06-28 09:30:00'),
(11, 'activity_price',    0.00,   349.00, 1002, '2024-06-25 16:00:00'),
(19, 'activity_price',    0.00,   109.00, 1002, '2024-06-25 15:30:00'),
(1,  'sale_price',    10999.00, 9999.00, 1001, '2024-03-20 14:00:00');

-- ============================================================
-- 验证统计
-- ============================================================
SELECT '=== 数据插入完成，统计如下 ===' AS '';
SELECT 'brands' AS 表名, COUNT(*) AS 数据量 FROM brands
UNION ALL SELECT 'categories', COUNT(*) FROM categories
UNION ALL SELECT 'tags', COUNT(*) FROM tags
UNION ALL SELECT 'spec_templates', COUNT(*) FROM spec_templates
UNION ALL SELECT 'spec_values', COUNT(*) FROM spec_values
UNION ALL SELECT 'spu', COUNT(*) FROM spu
UNION ALL SELECT 'spu_tag', COUNT(*) FROM spu_tag
UNION ALL SELECT 'spu_images', COUNT(*) FROM spu_images
UNION ALL SELECT 'sku', COUNT(*) FROM sku
UNION ALL SELECT 'inventory_log', COUNT(*) FROM inventory_log
UNION ALL SELECT 'status_log', COUNT(*) FROM status_log
UNION ALL SELECT 'audit_log', COUNT(*) FROM audit_log
UNION ALL SELECT 'price_log', COUNT(*) FROM price_log;