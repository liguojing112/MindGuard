-- ============================================
-- MindGuard 智能校园心理情绪监测与倾诉平台
-- 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS mindguard
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE mindguard;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS evaluation;
DROP TABLE IF EXISTS consultation_record;
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS counselor;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS alert;
DROP TABLE IF EXISTS emotion_analysis_result;
DROP TABLE IF EXISTS emotion_post;
DROP TABLE IF EXISTS mood_checkin;
DROP TABLE IF EXISTS assessment_result;
DROP TABLE IF EXISTS user_assessment;
DROP TABLE IF EXISTS scale_option;
DROP TABLE IF EXISTS scale_question;
DROP TABLE IF EXISTS scale;
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色: STUDENT/COUNSELOR/ADMIN',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态: 1正常 0禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_role (role),
    INDEX idx_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 咨询师表
-- ============================================
CREATE TABLE counselor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    title VARCHAR(50) COMMENT '职称',
    specialty VARCHAR(200) COMMENT '专长领域',
    description TEXT COMMENT '个人简介',
    avatar VARCHAR(255) COMMENT '头像',
    is_available TINYINT DEFAULT 1 COMMENT '是否可用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_counselor_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='咨询师表';

-- ============================================
-- 3. 倾诉贴表
-- ============================================
CREATE TABLE emotion_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '发布用户ID',
    content TEXT NOT NULL COMMENT '倾诉内容',
    is_anonymous TINYINT DEFAULT 0 COMMENT '是否匿名: 1是 0否',
    mood_emoji VARCHAR(50) COMMENT '心情表情',
    status TINYINT DEFAULT 1 COMMENT '状态',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_post_user (user_id),
    INDEX idx_post_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='倾诉贴';

-- ============================================
-- 4. 情绪分析结果表
-- ============================================
CREATE TABLE emotion_analysis_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL COMMENT '关联倾诉贴ID',
    emotion_score INT NOT NULL COMMENT '情绪分数 0-100',
    emotion_label VARCHAR(30) NOT NULL COMMENT '情绪标签',
    keywords VARCHAR(500) COMMENT '关键词JSON数组',
    analysis_text TEXT COMMENT '分析文本',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_analysis_post (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情绪分析结果';

-- ============================================
-- 5. 心情打卡表
-- ============================================
CREATE TABLE mood_checkin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    mood_emoji VARCHAR(50) NOT NULL COMMENT '心情表情',
    note VARCHAR(500) COMMENT '心情备注',
    checkin_date DATE NOT NULL COMMENT '打卡日期',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_checkin_user_date (user_id, checkin_date),
    INDEX idx_checkin_date (checkin_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心情打卡';

-- ============================================
-- 6. 预警单表
-- ============================================
CREATE TABLE alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT DEFAULT NULL COMMENT '关联倾诉贴ID(可空:AI聊一聊预警不关联帖子)',
    user_id BIGINT NOT NULL COMMENT '学生用户ID',
    alert_level VARCHAR(20) NOT NULL COMMENT '预警级别',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/NOTICED/COMMUNICATED/RESOLVED',
    assigned_counselor_id BIGINT COMMENT '负责辅导员ID',
    remarks TEXT COMMENT '处理备注',
    emotion_score INT COMMENT '情绪分数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_alert_status (status),
    INDEX idx_alert_user (user_id),
    INDEX idx_alert_counselor (assigned_counselor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='危机预警单';

-- ============================================
-- 7. 通知表
-- ============================================
CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    type VARCHAR(30) NOT NULL COMMENT '通知类型: ALERT/APPOINTMENT/SYSTEM',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读',
    related_id BIGINT COMMENT '关联业务ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notify_user (user_id, is_read),
    INDEX idx_notify_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知';

-- ============================================
-- 8. 量表信息表
-- ============================================
CREATE TABLE scale (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '量表名称',
    description TEXT COMMENT '量表说明',
    type VARCHAR(30) NOT NULL COMMENT '量表类型: SAS/SDS',
    total_questions INT NOT NULL COMMENT '题目总数',
    min_score INT NOT NULL COMMENT '最低分',
    max_score INT NOT NULL COMMENT '最高分',
    is_active TINYINT DEFAULT 1 COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心理量表';

-- ============================================
-- 9. 量表题目表
-- ============================================
CREATE TABLE scale_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scale_id BIGINT NOT NULL COMMENT '关联量表ID',
    question_number INT NOT NULL COMMENT '题号',
    question_text VARCHAR(500) NOT NULL COMMENT '题目内容',
    INDEX idx_question_scale (scale_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='量表题目';

-- ============================================
-- 10. 题目选项表
-- ============================================
CREATE TABLE scale_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL COMMENT '关联题目ID',
    option_text VARCHAR(200) NOT NULL COMMENT '选项内容',
    score_value INT NOT NULL COMMENT '分值(1-4)',
    sort_order INT DEFAULT 1 COMMENT '排序',
    INDEX idx_option_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项';

-- ============================================
-- 11. 用户测评记录表
-- ============================================
CREATE TABLE user_assessment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    scale_id BIGINT NOT NULL COMMENT '量表ID',
    status VARCHAR(20) DEFAULT 'IN_PROGRESS' COMMENT '状态: IN_PROGRESS/COMPLETED',
    answers TEXT COMMENT '答题记录JSON',
    started_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    completed_at DATETIME COMMENT '完成时间',
    INDEX idx_assessment_user (user_id, scale_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户测评记录';

-- ============================================
-- 12. 测评结果表
-- ============================================
CREATE TABLE assessment_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_id BIGINT NOT NULL COMMENT '关联测评记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    scale_id BIGINT NOT NULL COMMENT '量表ID',
    total_score INT NOT NULL COMMENT '总分',
    severity_level VARCHAR(20) NOT NULL COMMENT '程度: NORMAL/MILD/MODERATE/SEVERE',
    report_text TEXT COMMENT '测评报告',
    recommendations VARCHAR(500) COMMENT '推荐文章ID列表JSON',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_result_assessment (assessment_id),
    INDEX idx_result_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测评结果';

-- ============================================
-- 13. 科普文章表
-- ============================================
CREATE TABLE article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    content TEXT COMMENT '文章内容',
    summary VARCHAR(500) COMMENT '文章摘要',
    type VARCHAR(30) NOT NULL COMMENT '类型: 焦虑/抑郁/通用/VIDEO',
    tags VARCHAR(200) COMMENT '标签',
    severity_level VARCHAR(20) COMMENT '适用程度: NORMAL/MILD/MODERATE/SEVERE',
    source_url VARCHAR(500) COMMENT '来源链接',
    video_url VARCHAR(500) COMMENT '视频链接',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    is_active TINYINT DEFAULT 1 COMMENT '是否发布',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科普文章';

-- ============================================
-- 14. 咨询预约表
-- ============================================
CREATE TABLE appointment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    counselor_id BIGINT NOT NULL COMMENT '咨询师ID',
    appointment_date DATE NOT NULL COMMENT '预约日期',
    time_slot VARCHAR(30) NOT NULL COMMENT '时间段',
    issue_type VARCHAR(100) COMMENT '问题类型',
    is_anonymous TINYINT DEFAULT 0 COMMENT '是否匿名',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/APPROVED/REJECTED/IN_PROGRESS/COMPLETED/ARCHIVED',
    reject_reason VARCHAR(500) COMMENT '拒绝原因',
    priority TINYINT DEFAULT 0 COMMENT '优先级: 1高危优先',
    room_number VARCHAR(50) COMMENT '咨询房间号',
    location VARCHAR(200) COMMENT '咨询地点',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_appt_student (student_id),
    INDEX idx_appt_counselor (counselor_id),
    INDEX idx_appt_status (status),
    INDEX idx_appt_date (appointment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='咨询预约';

-- ============================================
-- 15. 咨询记录表
-- ============================================
CREATE TABLE consultation_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL COMMENT '关联预约ID',
    counselor_id BIGINT NOT NULL COMMENT '咨询师ID',
    content_summary TEXT COMMENT '咨询内容摘要',
    diagnosis TEXT COMMENT '初步诊断',
    suggestions TEXT COMMENT '跟进建议',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_record_appointment (appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='咨询记录';

-- ============================================
-- 16. 评价表
-- ============================================
CREATE TABLE evaluation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL COMMENT '关联预约ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    rating TINYINT NOT NULL COMMENT '评分 1-5',
    comment VARCHAR(500) COMMENT '评语',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_eval_appointment (appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='咨询评价';

-- ============================================
-- 初始数据
-- ============================================

-- 测试账号密码均为: 123456 (BCrypt加密)
-- BCrypt for "123456": $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi

-- 管理员
INSERT INTO user (username, password, real_name, email, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@mindguard.cn', 'ADMIN', 1);

-- 辅导员/咨询师账号
INSERT INTO user (username, password, real_name, email, role, status) VALUES
('counselor1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张老师', 'zhang@mindguard.cn', 'COUNSELOR', 1),
('counselor2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李老师', 'li@mindguard.cn', 'COUNSELOR', 1);

-- 学生测试账号
INSERT INTO user (username, password, real_name, email, role, status) VALUES
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王同学', 'wang@stu.edu.cn', 'STUDENT', 1),
('student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈同学', 'chen@stu.edu.cn', 'STUDENT', 1),
('student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '刘同学', 'liu@stu.edu.cn', 'STUDENT', 1);

-- 咨询师信息
INSERT INTO counselor (user_id, real_name, title, specialty, description, is_available) VALUES
(2, '张老师', '心理咨询师', '情绪管理,学业压力,人际关系',
 '国家二级心理咨询师，从事高校心理咨询工作8年，擅长认知行为疗法和正念减压。累计咨询时长超过3000小时，在青少年情绪管理、学业压力调适方面有丰富经验。', 1),
(3, '李老师', '朋辈辅导员', '情感困扰,职业规划,自我成长',
 '心理学硕士，朋辈辅导项目负责人。以温暖、共情的风格著称，擅长倾听和引导。接受过系统的危机干预培训，在学生群体中广受好评。', 1);

-- ============================================
-- 焦虑自评量表(SAS) - 20题
-- ============================================
INSERT INTO scale (name, description, type, total_questions, min_score, max_score, is_active) VALUES
('焦虑自评量表(SAS)', '焦虑自评量表(Self-Rating Anxiety Scale)由Zung于1971年编制，用于评估焦虑患者的主观感受。共20题，每题4级评分，总分乘以1.25取整数得标准分。', 'SAS', 20, 20, 80, 1);

INSERT INTO scale (name, description, type, total_questions, min_score, max_score, is_active) VALUES
('抑郁自评量表(SDS)', '抑郁自评量表(Self-Rating Depression Scale)由Zung于1965年编制，能直观反映抑郁患者的主观感受。共20题，每题4级评分，总分乘以1.25取整数得标准分。', 'SDS', 20, 20, 80, 1);

-- SAS题目 (scale_id=1)
INSERT INTO scale_question (scale_id, question_number, question_text) VALUES
(1, 1, '我觉得比平时容易紧张和着急'),
(1, 2, '我无缘无故地感到害怕'),
(1, 3, '我容易心里烦乱或觉得惊恐'),
(1, 4, '我觉得我可能将要发疯'),
(1, 5, '我觉得一切都很好，也不会发生什么不幸'),
(1, 6, '我手脚发抖打颤'),
(1, 7, '我因为头痛、颈痛和背痛而苦恼'),
(1, 8, '我感觉容易衰弱和疲乏'),
(1, 9, '我觉得心平气和，并且容易安静坐着'),
(1, 10, '我觉得心跳得很快'),
(1, 11, '我因为一阵阵头晕而苦恼'),
(1, 12, '我有过晕倒发作，或觉得要晕倒似的'),
(1, 13, '我呼气吸气都感到很容易'),
(1, 14, '我的手脚麻木和刺痛'),
(1, 15, '我因为胃痛和消化不良而苦恼'),
(1, 16, '我常常要小便'),
(1, 17, '我的手脚常常是干燥温暖的'),
(1, 18, '我脸红发热'),
(1, 19, '我容易入睡并且一夜睡得很好'),
(1, 20, '我做噩梦');

-- SAS选项（每题4个选项，分值1-4，但第5,9,13,17,19题为反向计分）
-- 正向题目: 1-没有或很少时间, 2-小部分时间, 3-相当多时间, 4-绝大部分或全部时间
-- 为简化，所有选项按正向计分插入，反向题在ScoreCalculator中处理

-- 为SAS所有20道题插入选项
INSERT INTO scale_option (question_id, option_text, score_value, sort_order) VALUES
(1, '没有或很少时间', 1, 1), (1, '小部分时间', 2, 2), (1, '相当多时间', 3, 3), (1, '绝大部分或全部时间', 4, 4),
(2, '没有或很少时间', 1, 1), (2, '小部分时间', 2, 2), (2, '相当多时间', 3, 3), (2, '绝大部分或全部时间', 4, 4),
(3, '没有或很少时间', 1, 1), (3, '小部分时间', 2, 2), (3, '相当多时间', 3, 3), (3, '绝大部分或全部时间', 4, 4),
(4, '没有或很少时间', 1, 1), (4, '小部分时间', 2, 2), (4, '相当多时间', 3, 3), (4, '绝大部分或全部时间', 4, 4),
(5, '没有或很少时间', 4, 1), (5, '小部分时间', 3, 2), (5, '相当多时间', 2, 3), (5, '绝大部分或全部时间', 1, 4),
(6, '没有或很少时间', 1, 1), (6, '小部分时间', 2, 2), (6, '相当多时间', 3, 3), (6, '绝大部分或全部时间', 4, 4),
(7, '没有或很少时间', 1, 1), (7, '小部分时间', 2, 2), (7, '相当多时间', 3, 3), (7, '绝大部分或全部时间', 4, 4),
(8, '没有或很少时间', 1, 1), (8, '小部分时间', 2, 2), (8, '相当多时间', 3, 3), (8, '绝大部分或全部时间', 4, 4),
(9, '没有或很少时间', 4, 1), (9, '小部分时间', 3, 2), (9, '相当多时间', 2, 3), (9, '绝大部分或全部时间', 1, 4),
(10, '没有或很少时间', 1, 1), (10, '小部分时间', 2, 2), (10, '相当多时间', 3, 3), (10, '绝大部分或全部时间', 4, 4),
(11, '没有或很少时间', 1, 1), (11, '小部分时间', 2, 2), (11, '相当多时间', 3, 3), (11, '绝大部分或全部时间', 4, 4),
(12, '没有或很少时间', 1, 1), (12, '小部分时间', 2, 2), (12, '相当多时间', 3, 3), (12, '绝大部分或全部时间', 4, 4),
(13, '没有或很少时间', 4, 1), (13, '小部分时间', 3, 2), (13, '相当多时间', 2, 3), (13, '绝大部分或全部时间', 1, 4),
(14, '没有或很少时间', 1, 1), (14, '小部分时间', 2, 2), (14, '相当多时间', 3, 3), (14, '绝大部分或全部时间', 4, 4),
(15, '没有或很少时间', 1, 1), (15, '小部分时间', 2, 2), (15, '相当多时间', 3, 3), (15, '绝大部分或全部时间', 4, 4),
(16, '没有或很少时间', 1, 1), (16, '小部分时间', 2, 2), (16, '相当多时间', 3, 3), (16, '绝大部分或全部时间', 4, 4),
(17, '没有或很少时间', 4, 1), (17, '小部分时间', 3, 2), (17, '相当多时间', 2, 3), (17, '绝大部分或全部时间', 1, 4),
(18, '没有或很少时间', 1, 1), (18, '小部分时间', 2, 2), (18, '相当多时间', 3, 3), (18, '绝大部分或全部时间', 4, 4),
(19, '没有或很少时间', 4, 1), (19, '小部分时间', 3, 2), (19, '相当多时间', 2, 3), (19, '绝大部分或全部时间', 1, 4),
(20, '没有或很少时间', 1, 1), (20, '小部分时间', 2, 2), (20, '相当多时间', 3, 3), (20, '绝大部分或全部时间', 4, 4);

-- SDS题目 (scale_id=2)
INSERT INTO scale_question (scale_id, question_number, question_text) VALUES
(2, 1, '我觉得闷闷不乐，情绪低沉'),
(2, 2, '我觉得一天之中早晨最好'),
(2, 3, '我一阵阵哭出来或觉得想哭'),
(2, 4, '我晚上睡眠不好'),
(2, 5, '我吃得跟平常一样多'),
(2, 6, '我与异性密切接触时和以往一样感到愉快'),
(2, 7, '我发觉我的体重在下降'),
(2, 8, '我有便秘的苦恼'),
(2, 9, '我心跳比平时快'),
(2, 10, '我无缘无故地感到疲乏'),
(2, 11, '我的头脑跟平常一样清楚'),
(2, 12, '我觉得经常做的事情并没有困难'),
(2, 13, '我觉得不安而平静不下来'),
(2, 14, '我对将来抱有希望'),
(2, 15, '我比平常容易生气激动'),
(2, 16, '我觉得作出决定是容易的'),
(2, 17, '我觉得自己是个有用的人，有人需要我'),
(2, 18, '我的生活过得很有意思'),
(2, 19, '我认为如果我死了别人会生活得好些'),
(2, 20, '我平常感兴趣的事我仍然照样感兴趣');

-- SDS选项（反向题: 2,5,6,11,12,14,16,17,18,20）
INSERT INTO scale_option (question_id, option_text, score_value, sort_order) VALUES
(21, '没有或很少时间', 1, 1), (21, '小部分时间', 2, 2), (21, '相当多时间', 3, 3), (21, '绝大部分或全部时间', 4, 4),
(22, '没有或很少时间', 4, 1), (22, '小部分时间', 3, 2), (22, '相当多时间', 2, 3), (22, '绝大部分或全部时间', 1, 4),
(23, '没有或很少时间', 1, 1), (23, '小部分时间', 2, 2), (23, '相当多时间', 3, 3), (23, '绝大部分或全部时间', 4, 4),
(24, '没有或很少时间', 1, 1), (24, '小部分时间', 2, 2), (24, '相当多时间', 3, 3), (24, '绝大部分或全部时间', 4, 4),
(25, '没有或很少时间', 4, 1), (25, '小部分时间', 3, 2), (25, '相当多时间', 2, 3), (25, '绝大部分或全部时间', 1, 4),
(26, '没有或很少时间', 4, 1), (26, '小部分时间', 3, 2), (26, '相当多时间', 2, 3), (26, '绝大部分或全部时间', 1, 4),
(27, '没有或很少时间', 1, 1), (27, '小部分时间', 2, 2), (27, '相当多时间', 3, 3), (27, '绝大部分或全部时间', 4, 4),
(28, '没有或很少时间', 1, 1), (28, '小部分时间', 2, 2), (28, '相当多时间', 3, 3), (28, '绝大部分或全部时间', 4, 4),
(29, '没有或很少时间', 1, 1), (29, '小部分时间', 2, 2), (29, '相当多时间', 3, 3), (29, '绝大部分或全部时间', 4, 4),
(30, '没有或很少时间', 1, 1), (30, '小部分时间', 2, 2), (30, '相当多时间', 3, 3), (30, '绝大部分或全部时间', 4, 4),
(31, '没有或很少时间', 4, 1), (31, '小部分时间', 3, 2), (31, '相当多时间', 2, 3), (31, '绝大部分或全部时间', 1, 4),
(32, '没有或很少时间', 4, 1), (32, '小部分时间', 3, 2), (32, '相当多时间', 2, 3), (32, '绝大部分或全部时间', 1, 4),
(33, '没有或很少时间', 1, 1), (33, '小部分时间', 2, 2), (33, '相当多时间', 3, 3), (33, '绝大部分或全部时间', 4, 4),
(34, '没有或很少时间', 4, 1), (34, '小部分时间', 3, 2), (34, '相当多时间', 2, 3), (34, '绝大部分或全部时间', 1, 4),
(35, '没有或很少时间', 1, 1), (35, '小部分时间', 2, 2), (35, '相当多时间', 3, 3), (35, '绝大部分或全部时间', 4, 4),
(36, '没有或很少时间', 4, 1), (36, '小部分时间', 3, 2), (36, '相当多时间', 2, 3), (36, '绝大部分或全部时间', 1, 4),
(37, '没有或很少时间', 4, 1), (37, '小部分时间', 3, 2), (37, '相当多时间', 2, 3), (37, '绝大部分或全部时间', 1, 4),
(38, '没有或很少时间', 4, 1), (38, '小部分时间', 3, 2), (38, '相当多时间', 2, 3), (38, '绝大部分或全部时间', 1, 4),
(39, '没有或很少时间', 1, 1), (39, '小部分时间', 2, 2), (39, '相当多时间', 3, 3), (39, '绝大部分或全部时间', 4, 4),
(40, '没有或很少时间', 4, 1), (40, '小部分时间', 3, 2), (40, '相当多时间', 2, 3), (40, '绝大部分或全部时间', 1, 4);

-- ============================================
-- 17. AI聊天消息表
-- ============================================
CREATE TABLE IF NOT EXISTS ai_chat_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role VARCHAR(20) NOT NULL COMMENT 'user/assistant',
    content TEXT NOT NULL COMMENT '消息内容',
    emotion_score INT COMMENT 'AI分析情绪分数',
    emotion_label VARCHAR(30) COMMENT 'AI分析情绪标签',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_chat_user (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天消息';

-- ============================================
-- 倾诉贴种子数据（学生4=王同学, 5=陈同学, 6=刘同学）
-- ============================================
INSERT INTO emotion_post (user_id, content, is_anonymous, mood_emoji, status) VALUES
(4, '今天参加了社团活动，认识了新朋友，感觉特别开心！大学生活越来越充实了，对未来充满期待。', 0, 'HAPPY', 1),
(4, '最近期末考试快到了，感觉压力好大，晚上总是失眠，白天又疲惫不堪，真的很焦虑，不知道能不能考好。', 0, 'ANXIOUS', 1),
(5, '刚刚完成了小组项目展示，大家都夸我做得好，特别有成就感！感恩有这样的团队伙伴，一起努力的感觉真好。', 0, 'PROUD', 1),
(5, '最近心情特别低落，感觉特别崩溃，做什么都提不起劲，觉得一切都没有意义，活着好累……不知道该怎么办。', 0, 'sad', 1),
(6, '今天天气真好，在操场上跑了几圈，吹着风感觉很放松很舒服。生活就是这样，简单的小事也能让人快乐。', 0, 'HAPPY', 1),
(6, '和室友闹了点矛盾，心情有点郁闷。我知道都是小事，但就是不知道该怎么开口沟通，人际关系真的好难。', 0, 'sad', 1);

-- 对应的AI分析结果（与MockAIService规则一致）
INSERT INTO emotion_analysis_result (post_id, emotion_score, emotion_label, keywords, analysis_text) VALUES
(1, 82, 'POSITIVE', '开心,充实,期待', '检测到积极情绪词汇，整体情绪状态良好。用户表现出较高的生活满意度和社交主动性，是心理健康的积极信号。'),
(2, 48, 'MILD_NEGATIVE', '压力,失眠,疲惫,焦虑', '检测到学业压力和睡眠问题相关的负面词汇，情绪分数偏低。建议合理安排复习计划，尝试放松训练改善睡眠，如有持续困扰可预约心理咨询。'),
(3, 85, 'POSITIVE', '成就感,感恩,努力', '检测到成就感、感恩等积极词汇，情绪状态非常健康。良好的团队合作体验对心理健康有积极促进作用。'),
(4, 22, 'SEVERE_NEGATIVE', '崩溃,无意义,绝望', '【高危预警】检测到严重负面情绪表达，包含无价值感和绝望倾向。分数极低（22分），建议立即进行危机干预，安排专业心理咨询师跟进。'),
(5, 78, 'POSITIVE', '放松,快乐,简单', '检测到放松、快乐等积极词汇，情绪状态良好。适当的体育锻炼是维持心理健康的有效方式。'),
(6, 52, 'MILD_NEGATIVE', '郁闷,矛盾,沟通', '检测到人际关系困扰相关的负面词汇，情绪分数略偏低。建议尝试非暴力沟通技巧，必要时可向辅导员寻求帮助。');

-- 高危预警（post_id=4 陈同学的帖子触发）
INSERT INTO alert (post_id, user_id, alert_level, status, assigned_counselor_id, remarks, emotion_score) VALUES
(4, 5, 'SEVERE', 'PENDING', 1, '系统自动检测到高危负面情绪，建议立即关注', 22);

-- ============================================
-- 心情打卡种子数据
-- ============================================
INSERT INTO mood_checkin (user_id, mood_emoji, note, checkin_date) VALUES
(4, 'OPTIMISTIC', '今天天气好，心情也不错~', CURDATE()),
(5, 'sad', '有点低落，但还在努力', CURDATE()),
(6, 'HAPPY', '和好朋友一起吃了好吃的！', CURDATE());

-- ============================================
-- 预约种子数据（咨询师1=张老师, 咨询师2=李老师）
-- ============================================
INSERT INTO appointment (student_id, counselor_id, appointment_date, time_slot, issue_type, is_anonymous, status, room_number) VALUES
(4, 1, CURDATE() + INTERVAL 1 DAY, '09:00-10:00', '学业压力', 0, 'APPROVED', CONCAT('ROOM-', DATE_FORMAT(CURDATE(), '%Y%m%d'), '-A1K')),
(5, 1, CURDATE() - INTERVAL 2 DAY, '14:00-15:00', '情绪困扰', 0, 'COMPLETED', CONCAT('ROOM-', DATE_FORMAT(CURDATE(), '%Y%m%d'), '-B2M'));

-- 已完成预约的咨询记录
INSERT INTO consultation_record (appointment_id, counselor_id, content_summary, diagnosis, suggestions) VALUES
(2, 1, '陈同学近期情绪低落，自述对学业和生活失去兴趣，持续时间约两周。交流中情绪逐渐稳定，表达了一些内心的困惑和压力来源。整体表现出一定的求助意愿。', '初步评估为轻度抑郁倾向，需持续关注。无自伤风险，暂不建议药物干预。', '1. 每周进行一次心理咨询，连续4周\n2. 建议每天进行30分钟有氧运动\n3. 尝试正念呼吸练习，早晚各5分钟\n4. 如有情绪急剧恶化，随时联系咨询师');

-- 学生对已完成咨询的评价
INSERT INTO evaluation (appointment_id, student_id, rating, comment) VALUES
(2, 5, 5, '张老师非常专业和耐心，交流后感觉心里轻松了很多。谢谢老师的建议，我会努力调整的。');

-- 系统通知种子数据
INSERT INTO notification (user_id, type, title, content, is_read, related_id) VALUES
(4, 'APPOINTMENT_APPROVED', '预约审核通过', '您预约的心理咨询已审核通过，请按时参加。咨询房间号已生成。', 0, 1),
(5, 'ALERT', '危机预警提醒', '系统检测到您的情绪状态需要关注，建议预约心理咨询。辅导员已收到通知，将有专人跟进。', 0, 4);

-- ============================================
-- 科普文章初始数据
-- ============================================
INSERT INTO article (title, content, type, tags, severity_level, is_active) VALUES
('认识焦虑：当担忧成为负担', '焦虑是人类面对压力时的正常反应，但当这种担忧持续存在并影响日常生活时，就可能发展为焦虑障碍。本文帮助你了解焦虑的成因、表现和应对方法。面对焦虑，首先要学会觉察自己的情绪变化，识别触发焦虑的情境。深呼吸练习、渐进式肌肉放松和正念冥想都被证明是有效的自我调节方法。如果焦虑持续存在，请不要犹豫寻求专业帮助。', '焦虑', '焦虑,情绪管理,自我调节', 'MILD', 1),
('走出抑郁的阴霾', '抑郁不是简单的"心情不好"，而是一种需要认真对待的心理状态。如果你感到持续的情绪低落、兴趣丧失、精力减退，这些可能是抑郁的信号。请记住，抑郁是可以治疗的。认知行为疗法(CBT)和人际心理治疗(IPT)都显示出良好的效果。建立规律的生活作息、适度的体育锻炼和保持社交联系都是康复路上的重要支持。你不是一个人在战斗。', '抑郁', '抑郁,康复,心理治疗', 'MODERATE', 1),
('考试焦虑应对指南', '考试前感到紧张是正常的，但过度的考试焦虑会影响你的发挥和身心健康。本指南提供实用的应对策略：合理规划复习时间、掌握放松技巧、调整不合理信念。记住，考试成绩不能定义你的价值。尝试将大目标分解为小步骤，每完成一步都给自己积极的反馈。', '焦虑', '考试焦虑,学习压力,应对策略', 'MILD', 1),
('情绪管理：与自己的情绪做朋友', '情绪不是敌人，而是信使。每种情绪都在告诉我们一些重要的信息。学会识别、接纳和管理情绪是心理健康的重要技能。本文介绍情绪ABC理论、情绪日记法和5-4-3-2-1感官着陆技术，帮助你更好地理解和调节自己的情绪。', '通用', '情绪管理,心理健康,自我成长', 'NORMAL', 1),
('建立健康的人际关系', '良好的人际关系是心理健康的重要保护因素。学会有效沟通、设立边界、处理冲突是建立健康关系的关键技能。本文提供实用的沟通技巧和关系维护策略，帮助你在校园生活中建立积极的人际网络。', '通用', '人际关系,沟通技巧,社交支持', 'NORMAL', 1),
('大学生心理健康自助手册', '大学生活充满了机遇和挑战。本手册涵盖学业压力、人际关系、情感困扰、职业规划等常见问题，提供实用的自我评估工具和应对策略。记住，寻求帮助是勇气的表现，而不是软弱。校园心理咨询中心随时欢迎你的到来。', '通用', '大学生,心理健康,自助', 'MILD', 1),
('严重心理问题的识别与求助', '如果你或身边的人出现持续的自伤念头、严重的睡眠和饮食问题、社会功能显著下降等情况，这可能是需要紧急干预的信号。请记住：生命可贵，永远有希望。本文提供危机干预热线和紧急求助途径，帮助你或你关心的人获得必要的支持。全国心理援助热线：400-161-9995。', '通用', '危机干预,求助,心理健康', 'SEVERE', 1),
('正念减压：活在当下的力量', '正念(Mindfulness)是一种有意识地、不加评判地关注当下的方法。研究表明，8周的正念练习可以显著降低焦虑和抑郁水平。本文提供简单的正念入门练习：正念呼吸、身体扫描和正念行走。每天只需10分钟，就能感受到变化。', '焦虑', '正念,减压,冥想', 'MILD', 1),
('睡眠与心理健康', '良好的睡眠是心理健康的基础。失眠与焦虑、抑郁密切相关。本文介绍睡眠卫生知识、刺激控制疗法和放松训练，帮助你改善睡眠质量。建立固定的睡眠时间表、避免睡前使用电子产品、创造舒适的睡眠环境都是有效措施。', '通用', '睡眠,失眠,自我调节', 'MILD', 1),
('培养心理韧性：在逆境中成长', '心理韧性(Resilience)不是天生的，而是可以通过练习获得的能力。本文介绍培养心理韧性的七个关键因素：乐观思维、情绪调节、自我效能感、社会支持、目标感、灵活应对和自我关怀。每一次挑战都是成长的机会。', '通用', '心理韧性,成长,逆境应对', 'NORMAL', 1);
