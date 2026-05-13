-- =============================================
-- 仓储管理系统 数据库建表脚本
-- Database: warehouse
-- =============================================

CREATE DATABASE IF NOT EXISTS warehouse DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE warehouse;

-- =============================================
-- 用户表
-- =============================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt)',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =============================================
-- 角色表
-- =============================================
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) DEFAULT NULL COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- =============================================
-- 用户角色关联表
-- =============================================
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- =============================================
-- 商品分类表
-- =============================================
DROP TABLE IF EXISTS wms_category;
CREATE TABLE wms_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- =============================================
-- 商品表
-- =============================================
DROP TABLE IF EXISTS wms_goods;
CREATE TABLE wms_goods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    goods_code VARCHAR(50) NOT NULL UNIQUE COMMENT '商品编码',
    goods_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    unit VARCHAR(20) DEFAULT '个' COMMENT '单位',
    spec VARCHAR(100) DEFAULT NULL COMMENT '规格',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '单价',
    image_url VARCHAR(255) DEFAULT NULL COMMENT '图片地址',
    description VARCHAR(500) DEFAULT NULL COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_goods_code (goods_code),
    INDEX idx_goods_name (goods_name),
    INDEX idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- =============================================
-- 供应商表
-- =============================================
DROP TABLE IF EXISTS wms_supplier;
CREATE TABLE wms_supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    address VARCHAR(200) DEFAULT NULL COMMENT '地址',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- =============================================
-- 库存表 (使用version字段实现乐观锁)
-- =============================================
DROP TABLE IF EXISTS wms_inventory;
CREATE TABLE wms_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    goods_id BIGINT NOT NULL UNIQUE COMMENT '商品ID',
    quantity INT DEFAULT 0 COMMENT '当前库存数量',
    min_stock INT DEFAULT 0 COMMENT '最小库存',
    max_stock INT DEFAULT 99999 COMMENT '最大库存',
    alert_threshold INT DEFAULT 10 COMMENT '预警阈值',
    version INT DEFAULT 0 COMMENT '乐观锁版本号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_goods_id (goods_id),
    INDEX idx_alert (quantity, alert_threshold)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- =============================================
-- 入库单表
-- =============================================
DROP TABLE IF EXISTS wms_stock_in;
CREATE TABLE wms_stock_in (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock_in_no VARCHAR(32) NOT NULL UNIQUE COMMENT '入库单号',
    goods_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '入库数量',
    unit_price DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
    total_price DECIMAL(12,2) DEFAULT NULL COMMENT '总价',
    supplier_id BIGINT DEFAULT NULL COMMENT '供应商ID',
    operator_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-草稿 1-已入库 2-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_stock_in_no (stock_in_no),
    INDEX idx_goods (goods_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表';

-- =============================================
-- 出库单表
-- =============================================
DROP TABLE IF EXISTS wms_stock_out;
CREATE TABLE wms_stock_out (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock_out_no VARCHAR(32) NOT NULL UNIQUE COMMENT '出库单号',
    goods_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '出库数量',
    unit_price DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
    total_price DECIMAL(12,2) DEFAULT NULL COMMENT '总价',
    target_info VARCHAR(200) DEFAULT NULL COMMENT '目标信息(客户/部门等)',
    operator_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-草稿 1-已出库 2-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_stock_out_no (stock_out_no),
    INDEX idx_goods (goods_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

-- =============================================
-- 库存流水日志表
-- =============================================
DROP TABLE IF EXISTS wms_inventory_log;
CREATE TABLE wms_inventory_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    goods_id BIGINT NOT NULL COMMENT '商品ID',
    before_quantity INT DEFAULT 0 COMMENT '变更前数量',
    change_quantity INT NOT NULL COMMENT '变更数量(正=入库,负=出库)',
    after_quantity INT DEFAULT 0 COMMENT '变更后数量',
    type VARCHAR(10) NOT NULL COMMENT '类型: IN/OUT/ADJUST',
    biz_no VARCHAR(32) DEFAULT NULL COMMENT '关联业务单号',
    operator_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_goods (goods_id),
    INDEX idx_create_time (create_time),
    INDEX idx_biz_no (biz_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水日志表';
