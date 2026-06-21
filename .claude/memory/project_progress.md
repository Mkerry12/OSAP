---
name: Project Progress
description: Current development progress of the OSAP backend project
type: project
---

## Current Progress (as of 2026-06-20)

- **已完成**: 认证模块 (AuthController + AuthServiceImpl) 和用户模块 (UserController + UserServiceImpl)，包括注册、登录、退出、忘记密码、重置密码、个人信息查看/修改、修改密码、修改手机号
- **当前阶段**: 准备开发用户端的问卷管理模块 (Survey CRUD)
- **数据库设计已确定**: survey → question → question_option (1:N)，加上 survey_response → answer 用于答卷
- **技术栈**: Spring Boot 3.5.15, MyBatis, MySQL, Redis, Aliyun OSS
- **API风格**: 参考接口文档但不完全照搬，按自己理解调整

## 注意事项

- User 表中有冗余字段 `survey_count` 和 `response_count`，创建/删除问卷和提交回答时需要更新
- 密码使用 MD5（旧项目遗留，无盐值）
- 认证方式：Redis 存储 UUID token，非 JWT
