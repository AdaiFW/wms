-- H2 Seed Data
MERGE INTO sys_role (id, role_name, role_code, description) VALUES
(1, '超级管理员', 'ROLE_ADMIN', '系统最高权限'),
(2, '仓库管理员', 'ROLE_WAREHOUSE', '仓库管理权限'),
(3, '普通用户', 'ROLE_USER', '基本查看权限');

MERGE INTO wms_category (id, category_name, parent_id, sort) VALUES
(1, '电子产品', 0, 1),
(2, '办公用品', 0, 2),
(3, '生活用品', 0, 3),
(4, '食品饮料', 0, 4),
(5, '电脑配件', 1, 1),
(6, '手机配件', 1, 2);

MERGE INTO wms_supplier (id, supplier_name, contact_person, contact_phone, address) VALUES
(1, '华为科技', '张三', '13800001111', '深圳市龙岗区'),
(2, '小米科技', '李四', '13800002222', '北京市海淀区'),
(3, '得力办公', '王五', '13800003333', '浙江省宁波市');

MERGE INTO wms_goods (id, goods_code, goods_name, category_id, unit, spec, price) VALUES
(1, 'G-001', '华为MateBook X Pro', 5, '台', '14英寸 i7 16GB 512GB', 8999.00),
(2, 'G-002', '小米14 Ultra', 6, '台', '6.73英寸 12GB 256GB', 5999.00),
(3, 'G-003', '得力A4打印纸', 2, '箱', '70g 500张/包 5包/箱', 120.00),
(4, 'G-004', '农夫山泉矿泉水', 4, '箱', '550ml 24瓶/箱', 48.00),
(5, 'G-005', '罗技MX Master 3S鼠标', 5, '个', '无线蓝牙 8000DPI', 699.00);

MERGE INTO wms_inventory (id, goods_id, quantity, min_stock, max_stock, alert_threshold) VALUES
(1, 1, 100, 5, 500, 10),
(2, 2, 200, 10, 1000, 20),
(3, 3, 500, 20, 2000, 50),
(4, 4, 1000, 50, 5000, 100),
(5, 5, 50, 5, 300, 10);
