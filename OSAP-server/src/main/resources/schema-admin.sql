-- Admin module tables for OSAP
-- Run this script against the osap database

CREATE TABLE IF NOT EXISTS survey_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    category VARCHAR(50),
    questions TEXT,
    question_count INT DEFAULT 0,
    use_count INT DEFAULT 0,
    creator_id BIGINT,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(20) NOT NULL COMMENT 'OPERATION / LOGIN / ERROR',
    operator VARCHAR(50),
    action VARCHAR(50),
    target VARCHAR(200),
    ip VARCHAR(50),
    detail TEXT,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS backup_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(200),
    file_size BIGINT DEFAULT 0,
    creator_id BIGINT,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
