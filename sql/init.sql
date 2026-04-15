DROP DATABASE IF EXISTS drone_delivery;
CREATE DATABASE drone_delivery DEFAULT CHARSET utf8mb4;
USE drone_delivery;

-- 用户表
CREATE TABLE user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    avatar      VARCHAR(255),
    role        TINYINT      NOT NULL DEFAULT 0 COMMENT '0-用户 1-商家 2-管理员',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '0-禁用 1-正常',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 商家表
CREATE TABLE merchant (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT       NOT NULL UNIQUE,
    shop_name    VARCHAR(100) NOT NULL,
    contact      VARCHAR(50),
    category     VARCHAR(100) COMMENT '经营类目',
    description  VARCHAR(500),
    audit_status TINYINT      NOT NULL DEFAULT 0 COMMENT '0-待审核 1-通过 2-驳回',
    audit_remark VARCHAR(255),
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 商品分类表
CREATE TABLE category (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(50) NOT NULL,
    sort_order  INT         NOT NULL DEFAULT 0,
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE product (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT         NOT NULL,
    category_id BIGINT,
    name        VARCHAR(200)   NOT NULL,
    price       DECIMAL(10, 2) NOT NULL,
    image       VARCHAR(255),
    description VARCHAR(1000),
    status      TINYINT        NOT NULL DEFAULT 1 COMMENT '0-下架 1-上架',
    sales       INT            NOT NULL DEFAULT 0,
    create_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchant(id),
    FOREIGN KEY (category_id) REFERENCES category(id)
);

-- 收货地址表
CREATE TABLE address (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id       BIGINT       NOT NULL,
    contact_name  VARCHAR(50)  NOT NULL,
    contact_phone VARCHAR(20)  NOT NULL,
    address       VARCHAR(500) NOT NULL,
    is_default    TINYINT      NOT NULL DEFAULT 0 COMMENT '0-否 1-是',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 购物车表
CREATE TABLE cart (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT   NOT NULL,
    product_id  BIGINT   NOT NULL,
    quantity    INT      NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)    REFERENCES user(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- 无人机表
CREATE TABLE drone (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    code        VARCHAR(50)  NOT NULL UNIQUE COMMENT '无人机编号',
    model       VARCHAR(100) COMMENT '型号',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '0-空闲 1-配送中 2-维护中',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE `order` (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no      VARCHAR(50)    NOT NULL UNIQUE,
    user_id       BIGINT         NOT NULL,
    merchant_id   BIGINT         NOT NULL,
    address_id    BIGINT,
    total_price   DECIMAL(10, 2) NOT NULL,
    status        TINYINT        NOT NULL DEFAULT 0
        COMMENT '0-待接单 1-已接单/备货中 2-配送中 3-已送达 4-已取消 5-已拒单',
    drone_id      BIGINT,
    remark        VARCHAR(500),
    reject_reason VARCHAR(255),
    create_time   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)     REFERENCES user(id),
    FOREIGN KEY (merchant_id) REFERENCES merchant(id),
    FOREIGN KEY (drone_id)    REFERENCES drone(id)
);

-- 订单商品表
CREATE TABLE order_item (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id      BIGINT           NOT NULL,
    product_id    BIGINT           NOT NULL,
    product_name  VARCHAR(200)     NOT NULL,
    product_price DECIMAL(10, 2)   NOT NULL,
    quantity      INT              NOT NULL,
    FOREIGN KEY (order_id)   REFERENCES `order`(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- 无人机配送记录表
CREATE TABLE drone_log (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    drone_id    BIGINT   NOT NULL,
    order_id    BIGINT   NOT NULL,
    start_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time    DATETIME,
    status      TINYINT  NOT NULL DEFAULT 0 COMMENT '0-配送中 1-已完成',
    FOREIGN KEY (drone_id) REFERENCES drone(id),
    FOREIGN KEY (order_id) REFERENCES `order`(id)
);

-- 客服会话表
CREATE TABLE chat_session (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT   NOT NULL,
    admin_id    BIGINT,
    order_id    BIGINT   COMMENT '关联订单（可选）',
    status      TINYINT  NOT NULL DEFAULT 0 COMMENT '0-进行中 1-已关闭',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)  REFERENCES user(id),
    FOREIGN KEY (admin_id) REFERENCES user(id),
    FOREIGN KEY (order_id) REFERENCES `order`(id)
);

-- 聊天消息表
CREATE TABLE chat_message (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id  BIGINT   NOT NULL,
    sender_id   BIGINT   NOT NULL,
    sender_role TINYINT  NOT NULL COMMENT '0-用户 2-管理员',
    content     TEXT     NOT NULL,
    send_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES chat_session(id),
    FOREIGN KEY (sender_id)  REFERENCES user(id)
);

-- ========================================
-- 测试数据
-- ========================================

-- ----------------------------------------
-- 1. 用户账号（密码均为明文对应的 BCrypt 哈希）
-- ----------------------------------------
-- admin / admin123 (管理员)
INSERT INTO user (username, password, phone, email, role, status) VALUES
('admin',    '$2b$10$nYsNwr0X0/WY9mRpRWuxhOU65PFCAaqoyCpTBhKsdJ4Ans9WkW4bC', '13800000001', 'admin@drone.com',    2, 1);

-- 商家账号 merchant1 / merchant123, merchant2 / merchant123
INSERT INTO user (username, password, phone, email, role, status) VALUES
('merchant1', '$2b$10$qn6W11U75KwBybsr5yirbei/s018HQRxxSFzjpxEc64PQLP2sc4Ii', '13800000002', 'merchant1@drone.com', 1, 1),
('merchant2', '$2b$10$qn6W11U75KwBybsr5yirbei/s018HQRxxSFzjpxEc64PQLP2sc4Ii', '13800000003', 'merchant2@drone.com', 1, 1);

-- 普通用户 user1~user5 / user123
INSERT INTO user (username, password, phone, email, role, status) VALUES
('user1', '$2b$10$mGZf6g2Hl.3CjpwJaVH47.8tqTa9FAOUU.hS1ijrBq8kqlsR4g/EO', '13900000001', 'user1@test.com', 0, 1),
('user2', '$2b$10$mGZf6g2Hl.3CjpwJaVH47.8tqTa9FAOUU.hS1ijrBq8kqlsR4g/EO', '13900000002', 'user2@test.com', 0, 1),
('user3', '$2b$10$mGZf6g2Hl.3CjpwJaVH47.8tqTa9FAOUU.hS1ijrBq8kqlsR4g/EO', '13900000003', 'user3@test.com', 0, 1),
('user4', '$2b$10$mGZf6g2Hl.3CjpwJaVH47.8tqTa9FAOUU.hS1ijrBq8kqlsR4g/EO', '13900000004', 'user4@test.com', 0, 1),
('user5', '$2b$10$mGZf6g2Hl.3CjpwJaVH47.8tqTa9FAOUU.hS1ijrBq8kqlsR4g/EO', '13900000005', 'user5@test.com', 0, 1);

-- ----------------------------------------
-- 2. 商家信息（已审核通过）
-- ----------------------------------------
INSERT INTO merchant (user_id, shop_name, contact, category, description, audit_status, audit_remark) VALUES
(2, '校园美食坊',   '13800000002', '餐饮美食', '提供各类校园特色餐饮，快速配送到宿舍楼下', 1, '审核通过'),
(3, '鲜果时光',     '13800000003', '生鲜果蔬', '每日新鲜水果，产地直供，无人机极速送达',   1, '审核通过');

-- ----------------------------------------
-- 3. 商品分类
-- ----------------------------------------
INSERT INTO category (name, sort_order) VALUES
('餐饮美食', 1),
('生鲜果蔬', 2),
('日用百货', 3),
('医药健康', 4),
('文件快递', 5);

-- ----------------------------------------
-- 4. 商品（商家1：餐饮 8 个，商家2：生鲜 6 个）
-- ----------------------------------------
INSERT INTO product (merchant_id, category_id, name, price, image, description, status, sales) VALUES
-- 校园美食坊 (merchant_id=1)
(1, 1, '招牌鸡腿饭',      15.00, NULL, '秘制酱香鸡腿配米饭，含时蔬一份',        1, 156),
(1, 1, '红烧牛肉面',      18.00, NULL, '大块牛肉搭配手工拉面，汤浓味美',        1, 98),
(1, 1, '番茄鸡蛋盖饭',    12.00, NULL, '经典家常口味，酸甜开胃',               1, 203),
(1, 1, '麻辣香锅（小份）', 22.00, NULL, '多种蔬菜肉类任选，麻辣鲜香',           1, 87),
(1, 1, '皮蛋瘦肉粥',      8.00,  NULL, '慢火熬制，口感绵密',                   1, 134),
(1, 1, '手工水饺（12只）', 16.00, NULL, '猪肉白菜/韭菜鸡蛋，现包现煮',         1, 76),
(1, 1, '炸鸡套餐',        25.00, NULL, '香脆炸鸡3块+薯条+可乐',               1, 65),
(1, 1, '冰镇酸梅汤',      5.00,  NULL, '自制酸梅汤，冰爽解暑',                 1, 320),
-- 鲜果时光 (merchant_id=2)
(2, 2, '精选草莓（500g）',  25.00, NULL, '当季新鲜草莓，个大饱满，甜度高',       1, 88),
(2, 2, '进口车厘子（500g）',58.00, NULL, '智利进口车厘子，JJ级大果',            1, 42),
(2, 2, '水果拼盘（大份）',  35.00, NULL, '西瓜+哈密瓜+葡萄+猕猴桃，切好装盒',   1, 67),
(2, 2, '现榨橙汁（500ml）', 12.00, NULL, '鲜橙现榨，不加水不加糖',              1, 156),
(2, 2, '有机香蕉（1kg）',   9.90,  NULL, '有机种植，自然成熟',                  1, 210),
(2, 2, '混合坚果礼包',     38.00, NULL, '腰果+巴旦木+核桃+夏威夷果，每日坚果',  1, 53);

-- ----------------------------------------
-- 5. 无人机
-- ----------------------------------------
INSERT INTO drone (code, model, status) VALUES
('UAV-001', 'DJI FlyCart 30',  0),
('UAV-002', 'DJI FlyCart 30',  1),
('UAV-003', 'DJI Mavic 3',    0),
('UAV-004', 'DJI Mavic 3',    2),
('UAV-005', 'EHang 216',      0),
('UAV-006', 'EHang 216',      0),
('UAV-007', 'DJI FlyCart 30',  0),
('UAV-008', 'DJI Mavic 3',    0);

-- ----------------------------------------
-- 6. 收货地址
-- ----------------------------------------
INSERT INTO address (user_id, contact_name, contact_phone, address, is_default) VALUES
-- user1 的地址
(4, '张三', '13900000001', '北京市海淀区XX大学 1号宿舍楼 302室',   1),
(4, '张三', '13900000001', '北京市海淀区XX大学 图书馆一楼大厅',     0),
-- user2 的地址
(5, '李四', '13900000002', '北京市海淀区XX大学 2号宿舍楼 518室',   1),
-- user3 的地址
(6, '王五', '13900000003', '北京市海淀区XX大学 3号宿舍楼 101室',   1),
(6, '王五', '13900000003', '北京市海淀区XX大学 体育馆南门',         0),
-- user4 的地址
(7, '赵六', '13900000004', '北京市海淀区XX大学 4号宿舍楼 206室',   1);

-- ----------------------------------------
-- 7. 订单（覆盖各种状态）
-- ----------------------------------------
-- 订单1: user1 在校园美食坊下单，已送达（完整流程走完）
INSERT INTO `order` (order_no, user_id, merchant_id, address_id, total_price, status, drone_id, remark, create_time) VALUES
('DD202604100001', 4, 1, 1, 33.00, 3, 1, '不要辣', '2026-04-10 11:30:00');
INSERT INTO order_item (order_id, product_id, product_name, product_price, quantity) VALUES
(1, 1, '招牌鸡腿饭',   15.00, 1),
(1, 3, '番茄鸡蛋盖饭', 12.00, 1),
(1, 8, '冰镇酸梅汤',    5.00, 1);
INSERT INTO drone_log (drone_id, order_id, start_time, end_time, status) VALUES
(1, 1, '2026-04-10 11:45:00', '2026-04-10 12:05:00', 1);

-- 订单2: user1 在鲜果时光下单，配送中
INSERT INTO `order` (order_no, user_id, merchant_id, address_id, total_price, status, drone_id, remark, create_time) VALUES
('DD202604120002', 4, 2, 1, 60.00, 2, 2, NULL, '2026-04-12 15:00:00');
INSERT INTO order_item (order_id, product_id, product_name, product_price, quantity) VALUES
(2, 9,  '精选草莓（500g）', 25.00, 1),
(2, 11, '水果拼盘（大份）', 35.00, 1);
INSERT INTO drone_log (drone_id, order_id, start_time, end_time, status) VALUES
(2, 2, '2026-04-12 15:20:00', NULL, 0);

-- 订单3: user2 在校园美食坊下单，待接单
INSERT INTO `order` (order_no, user_id, merchant_id, address_id, total_price, status, drone_id, remark, create_time) VALUES
('DD202604140003', 5, 1, 3, 40.00, 0, NULL, '多加一份米饭', '2026-04-14 12:00:00');
INSERT INTO order_item (order_id, product_id, product_name, product_price, quantity) VALUES
(3, 2, '红烧牛肉面',        18.00, 1),
(3, 4, '麻辣香锅（小份）',   22.00, 1);

-- 订单4: user2 在鲜果时光下单，已接单/备货中
INSERT INTO `order` (order_no, user_id, merchant_id, address_id, total_price, status, drone_id, remark, create_time) VALUES
('DD202604140004', 5, 2, 3, 67.90, 1, NULL, NULL, '2026-04-14 14:30:00');
INSERT INTO order_item (order_id, product_id, product_name, product_price, quantity) VALUES
(4, 10, '进口车厘子（500g）', 58.00, 1),
(4, 13, '有机香蕉（1kg）',    9.90,  1);

-- 订单5: user3 在校园美食坊下单，已取消
INSERT INTO `order` (order_no, user_id, merchant_id, address_id, total_price, status, drone_id, remark, create_time) VALUES
('DD202604110005', 6, 1, 4, 25.00, 4, NULL, NULL, '2026-04-11 18:00:00');
INSERT INTO order_item (order_id, product_id, product_name, product_price, quantity) VALUES
(5, 7, '炸鸡套餐', 25.00, 1);

-- 订单6: user3 在校园美食坊下单，已拒单
INSERT INTO `order` (order_no, user_id, merchant_id, address_id, total_price, status, drone_id, reject_reason, create_time) VALUES
('DD202604120006', 6, 1, 4, 16.00, 5, NULL, '该商品已售罄，非常抱歉', '2026-04-12 12:30:00');
INSERT INTO order_item (order_id, product_id, product_name, product_price, quantity) VALUES
(6, 6, '手工水饺（12只）', 16.00, 1);

-- 订单7: user4 在鲜果时光下单，已送达
INSERT INTO `order` (order_no, user_id, merchant_id, address_id, total_price, status, drone_id, create_time) VALUES
('DD202604130007', 7, 2, 6, 47.00, 3, 3, '2026-04-13 16:00:00');
INSERT INTO order_item (order_id, product_id, product_name, product_price, quantity) VALUES
(7, 12, '现榨橙汁（500ml）', 12.00, 1),
(7, 11, '水果拼盘（大份）',  35.00, 1);
INSERT INTO drone_log (drone_id, order_id, start_time, end_time, status) VALUES
(3, 7, '2026-04-13 16:20:00', '2026-04-13 16:42:00', 1);

-- 订单8: user1 在校园美食坊下单，待接单（今日新单）
INSERT INTO `order` (order_no, user_id, merchant_id, address_id, total_price, status, drone_id, remark, create_time) VALUES
('DD202604150008', 4, 1, 2, 43.00, 0, NULL, '送到图书馆', NOW());
INSERT INTO order_item (order_id, product_id, product_name, product_price, quantity) VALUES
(8, 2, '红烧牛肉面', 18.00, 1),
(8, 7, '炸鸡套餐',   25.00, 1);

-- ----------------------------------------
-- 8. 购物车（user1 有几件待结算商品）
-- ----------------------------------------
INSERT INTO cart (user_id, product_id, quantity) VALUES
(4, 5,  2),
(4, 14, 1);

-- ----------------------------------------
-- 9. 客服会话 + 消息
-- ----------------------------------------
-- 会话1: user1 咨询配送问题（进行中）
INSERT INTO chat_session (user_id, admin_id, order_id, status) VALUES
(4, 1, 2, 0);
INSERT INTO chat_message (session_id, sender_id, sender_role, content, send_time) VALUES
(1, 4, 0, '你好，我的订单 DD202604120002 已经配送很久了，请问大概还要多久能到？', '2026-04-12 15:40:00'),
(1, 1, 2, '您好！已为您查询，无人机 UAV-002 正在配送途中，预计5分钟内送达，请留意接收。', '2026-04-12 15:42:00'),
(1, 4, 0, '好的，谢谢！', '2026-04-12 15:43:00');

-- 会话2: user3 咨询拒单原因（已关闭）
INSERT INTO chat_session (user_id, admin_id, order_id, status) VALUES
(6, 1, 6, 1);
INSERT INTO chat_message (session_id, sender_id, sender_role, content, send_time) VALUES
(2, 6, 0, '请问我的水饺订单为什么被拒了？', '2026-04-12 13:00:00'),
(2, 1, 2, '非常抱歉，商家反馈该商品当日已售罄。您可以尝试重新下单选择其他商品，给您带来不便深表歉意。', '2026-04-12 13:05:00'),
(2, 6, 0, '明白了，谢谢', '2026-04-12 13:06:00');
