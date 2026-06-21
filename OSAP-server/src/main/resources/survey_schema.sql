-- OSAP 问卷管理模块建表语句
-- 在线问卷调查与分析平台

-- 1. 问卷主表
CREATE TABLE IF NOT EXISTS `survey`
(
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '问卷ID',
    `title`             VARCHAR(200) NOT NULL COMMENT '问卷标题',
    `description`       TEXT COMMENT '问卷说明',
    `type`              VARCHAR(20)  NOT NULL DEFAULT 'PUBLIC' COMMENT '类型：PUBLIC-公开, ASSIGNED-指定用户',
    `target_phones`     TEXT COMMENT '指定用户手机号(逗号分隔)，仅ASSIGNED类型',
    `status`            VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿, PUBLISHED-已发布, CLOSED-已关闭',
    `is_anonymous`      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否匿名(0-否, 1-是)',
    `allow_multi_submit` TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '是否允许多次提交(0-否, 1-是)',
    `theme`             VARCHAR(100) COMMENT '主题样式',
    `start_time`        DATETIME COMMENT '开始时间',
    `end_time`          DATETIME COMMENT '结束时间',
    `question_count`    INT          NOT NULL DEFAULT 0 COMMENT '题目数量(冗余)',
    `response_count`    INT          NOT NULL DEFAULT 0 COMMENT '答卷数量(冗余)',
    `creator_id`        BIGINT       NOT NULL COMMENT '创建者ID',
    `create_at`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_at`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='问卷表';


-- 2. 题目表
CREATE TABLE IF NOT EXISTS `question`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `survey_id`   BIGINT      NOT NULL COMMENT '所属问卷ID',
    `type`        VARCHAR(20) NOT NULL COMMENT '题型：RADIO-单选, CHECKBOX-多选, TEXT-填空, RATING-评分, DROPDOWN-下拉',
    `title`       TEXT        NOT NULL COMMENT '题目内容',
    `required`    TINYINT(1)  NOT NULL DEFAULT 1 COMMENT '是否必填(0-否, 1-是)',
    `sort_order`  INT         NOT NULL DEFAULT 0 COMMENT '排序序号',
    `min_rating`  INT COMMENT 'RATING题型的最小分',
    `max_rating`  INT COMMENT 'RATING题型的最大分',
    `create_at`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_at`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_survey_id` (`survey_id`),
    CONSTRAINT `fk_question_survey` FOREIGN KEY (`survey_id`) REFERENCES `survey` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='题目表';


-- 3. 题目选项表
CREATE TABLE IF NOT EXISTS `question_option`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `question_id` BIGINT       NOT NULL COMMENT '所属题目ID',
    `label`       VARCHAR(500) NOT NULL COMMENT '选项文本',
    `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '排序序号',
    `create_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_question_id` (`question_id`),
    CONSTRAINT `fk_option_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='题目选项表';
