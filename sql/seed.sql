-- =============================================
-- 仓储管理系统 初始化数据
-- =============================================
USE warehouse;

-- 角色 (BCrypt加密的密码)
-- 密码: admin123 -> $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh
-- 简化: 实际使用时通过应用注册接口创建

INSERT INTO sys_role (role_name, role_code, description) VALUES
('超级管理员', 'ROLE_ADMIN', '系统最高权限'),
('仓库管理员', 'ROLE_WAREHOUSE', '仓库管理权限'),
('普通用户', 'ROLE_USER', '基本查看权限');

-- 管理员用户 (密码: admin123, BCrypt编码)
INSERT INTO sys_user (username, password, nickname, email, status) VALUES
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '系统管理员', 'admin@warehouse.com', 1),
('warehouse', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '仓库管理员', 'wh@warehouse.com', 1);

-- 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2);

-- 商品分类
INSERT INTO wms_category (category_name, parent_id, sort) VALUES
('电子产品', 0, 1),
('办公用品', 0, 2),
('生活用品', 0, 3),
('食品饮料', 0, 4),
('电脑配件', 1, 1),
('手机配件', 1, 2);

-- 供应商
INSERT INTO wms_supplier (supplier_name, contact_person, contact_phone, address) VALUES
('华为科技', '张三', '13800001111', '深圳市龙岗区'),
('小米科技', '李四', '13800002222', '北京市海淀区'),
('得力办公', '王五', '13800003333', '浙江省宁波市');

-- 商品
INSERT INTO wms_goods (goods_code, goods_name, category_id, unit, spec, price) VALUES
('G-001', '华为MateBook X Pro', 5, '台', '14英寸 i7 16GB 512GB', 8999.00),
('G-002', '小米14 Ultra', 6, '台', '6.73英寸 12GB 256GB', 5999.00),
('G-003', '得力A4打印纸', 2, '箱', '70g 500张/包 5包/箱', 120.00),
('G-004', '农夫山泉矿泉水', 4, '箱', '550ml 24瓶/箱', 48.00),
('G-005', '罗技MX Master 3S鼠标', 5, '个', '无线蓝牙 8000DPI', 699.00);

-- 库存
INSERT INTO wms_inventory (goods_id, quantity, min_stock, max_stock, alert_threshold) VALUES
(1, 100, 5, 500, 10),
(2, 200, 10, 1000, 20),
(3, 500, 20, 2000, 50),
(4, 1000, 50, 5000, 100),
(5, 50, 5, 300, 10);
