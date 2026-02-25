CREATE DATABASE IF NOT EXISTS health_diet DEFAULT CHARSET utf8mb4;
USE health_diet;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username        VARCHAR(50) NOT NULL UNIQUE COMMENT '登录名（管理员/营养师/企业用户使用）',
    password        VARCHAR(255) COMMENT '密码（普通用户可为空）',
    wx_open_id      VARCHAR(64) UNIQUE COMMENT '微信 openid（普通用户）',
    nick_name       VARCHAR(50) COMMENT '昵称',
    phone           VARCHAR(20) COMMENT '手机号',
    email           VARCHAR(100) COMMENT '邮箱',
    user_type       TINYINT NOT NULL COMMENT '用户类型：1普通用户 2管理员 3营养师 4企业用户',
    status          TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code   VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码，如ROLE_USER',
    role_name   VARCHAR(50) NOT NULL COMMENT '角色名称',
    remark      VARCHAR(200) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3. 用户角色关联
CREATE TABLE IF NOT EXISTS sys_user_role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    role_id     BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- 4. 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    perm_code   VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    perm_name   VARCHAR(100) NOT NULL COMMENT '权限名称',
    menu_path   VARCHAR(100) COMMENT '前端菜单路径',
    remark      VARCHAR(200),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 5. 角色权限关联
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id         BIGINT NOT NULL,
    permission_id   BIGINT NOT NULL,
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES sys_role(id),
    CONSTRAINT fk_rp_perm FOREIGN KEY (permission_id) REFERENCES sys_permission(id),
    UNIQUE KEY uk_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';

-- 6. 用户健康档案
CREATE TABLE IF NOT EXISTS user_profile (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    gender          TINYINT COMMENT '性别：1男 2女',
    age             INT COMMENT '年龄',
    height_cm       DECIMAL(5,2) COMMENT '身高cm',
    weight_kg       DECIMAL(5,2) COMMENT '体重kg',
    bmi             DECIMAL(4,2) COMMENT 'BMI',
    chronic_disease VARCHAR(255) COMMENT '慢性病信息',
    allergy         VARCHAR(255) COMMENT '过敏食材',
    taste_prefer    VARCHAR(255) COMMENT '口味偏好',
    goal            VARCHAR(255) COMMENT '健康目标',
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户健康档案';

-- 7. 食材表
CREATE TABLE IF NOT EXISTS ingredient (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL COMMENT '食材名称',
    category        VARCHAR(50) COMMENT '分类',
    calories        DECIMAL(8,2) COMMENT '每100g热量',
    protein         DECIMAL(8,2) COMMENT '蛋白质',
    fat             DECIMAL(8,2) COMMENT '脂肪',
    carbohydrate    DECIMAL(8,2) COMMENT '碳水',
    fiber           DECIMAL(8,2) COMMENT '膳食纤维',
    suitable_for    VARCHAR(255) COMMENT '适宜人群标签',
    avoid_for       VARCHAR(255) COMMENT '不宜人群标签',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食材表';

-- 8. 食谱表
CREATE TABLE IF NOT EXISTS recipe (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL COMMENT '食谱名称',
    type            TINYINT NOT NULL COMMENT '1早餐 2午餐 3晚餐 4加餐',
    total_calories  DECIMAL(8,2) COMMENT '总热量',
    suitable_for    VARCHAR(255) COMMENT '适宜标签',
    avoid_tags      VARCHAR(255) COMMENT '需要避免的疾病/情况标签',
    difficulty      TINYINT COMMENT '难度 1-5',
    cook_time_min   INT COMMENT '制作时长',
    cover_img       VARCHAR(255) COMMENT '封面图 URL',
    description     VARCHAR(500) COMMENT '描述',
    steps           TEXT COMMENT '制作步骤',
    create_by       BIGINT COMMENT '创建人ID',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食谱表';

-- 9. 食谱-食材明细
CREATE TABLE IF NOT EXISTS recipe_ingredient (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id       BIGINT NOT NULL,
    ingredient_id   BIGINT NOT NULL,
    amount_gram     DECIMAL(8,2) COMMENT '用量 g',
    remark          VARCHAR(255),
    CONSTRAINT fk_ri_recipe FOREIGN KEY (recipe_id) REFERENCES recipe(id),
    CONSTRAINT fk_ri_ing FOREIGN KEY (ingredient_id) REFERENCES ingredient(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食谱-食材关联';

-- 10. 饮食记录
CREATE TABLE IF NOT EXISTS diet_record (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    record_date     DATE NOT NULL COMMENT '记录日期',
    meal_type       TINYINT NOT NULL COMMENT '1早餐 2午餐 3晚餐 4加餐',
    recipe_id       BIGINT COMMENT '推荐食谱ID',
    description     VARCHAR(500) COMMENT '自定义描述',
    calories        DECIMAL(8,2) COMMENT '估算热量',
    satisfaction    TINYINT COMMENT '满意度 1-5',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_diet_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_diet_recipe FOREIGN KEY (recipe_id) REFERENCES recipe(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饮食记录';

-- 11. 推荐结果表
CREATE TABLE IF NOT EXISTS recommendation (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    rec_date        DATE NOT NULL COMMENT '推荐日期',
    plan_index      TINYINT NOT NULL COMMENT '当日第几套方案：1/2/3',
    breakfast_id    BIGINT COMMENT '推荐早餐食谱ID',
    lunch_id        BIGINT COMMENT '推荐午餐食谱ID',
    dinner_id       BIGINT COMMENT '推荐晚餐食谱ID',
    algorithm_type  TINYINT NOT NULL COMMENT '1规则匹配 2协同过滤 3混合',
    score           DECIMAL(5,2) COMMENT '推荐得分',
    status          TINYINT NOT NULL DEFAULT 1 COMMENT '1生效 0已失效',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rec_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个性化推荐结果';

-- 12. 营养师建议
CREATE TABLE IF NOT EXISTS dietitian_advice (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL COMMENT '被建议的用户',
    dietitian_id    BIGINT NOT NULL COMMENT '营养师ID',
    title           VARCHAR(100) NOT NULL COMMENT '建议标题',
    content         TEXT NOT NULL COMMENT '建议内容',
    advice_date     DATE NOT NULL,
    read_flag       TINYINT NOT NULL DEFAULT 0 COMMENT '0未读 1已读',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_advice_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_advice_dietitian FOREIGN KEY (dietitian_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营养师建议';

-- 13. 企业用户信息
CREATE TABLE IF NOT EXISTS enterprise (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL COMMENT '关联sys_user',
    company_name    VARCHAR(200) NOT NULL,
    contact         VARCHAR(50) COMMENT '联系人',
    contact_phone   VARCHAR(20),
    address         VARCHAR(255),
    remark          VARCHAR(255),
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ent_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业用户信息';

-- 14. 企业反馈
CREATE TABLE IF NOT EXISTS enterprise_feedback (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id   BIGINT NOT NULL,
    feedback_date   DATE NOT NULL,
    content         TEXT NOT NULL,
    attachment_url  VARCHAR(255) COMMENT '附件',
    reply           TEXT COMMENT '管理员回复',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ef_ent FOREIGN KEY (enterprise_id) REFERENCES enterprise(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业反馈';

-- 15. 系统日志
CREATE TABLE IF NOT EXISTS sys_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT COMMENT '操作人',
    action          VARCHAR(100) NOT NULL COMMENT '操作名称',
    request_uri     VARCHAR(255) COMMENT '请求URI',
    request_method  VARCHAR(10) COMMENT 'GET/POST',
    request_params  TEXT COMMENT '请求参数JSON',
    ip_address      VARCHAR(50) COMMENT 'IP地址',
    result_status   TINYINT COMMENT '结果状态：1成功 0失败',
    error_msg       TEXT COMMENT '错误信息',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志';

-- 基础测试数据（角色等）
INSERT INTO sys_role (role_code, role_name)
VALUES ('ROLE_USER', '普通用户'),
       ('ROLE_ADMIN', '管理员'),
       ('ROLE_DIETITIAN', '营养师'),
       ('ROLE_ENTERPRISE', '企业用户')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);


