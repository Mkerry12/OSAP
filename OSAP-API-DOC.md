# OSAP 前端接口文档

**版本**: v1.0  
**基础地址**: `http://localhost:8081`  

---

## 目录

1. [通用说明](#1-通用说明)
2. [认证模块 `/auth`](#2-认证模块-auth)
3. [文件上传 `/common`](#3-文件上传-common)
4. [用户端 — 用户信息 `/user/user`](#4-用户端--用户信息-useruser)
5. [用户端 — 问卷管理 `/user/survey`](#5-用户端--问卷管理-usersurvey)
6. [用户端 — 题目管理 `/user/question`](#6-用户端--题目管理-userquestion)
7. [用户端 — 填写问卷 `/user/fill`](#7-用户端--填写问卷-userfill)
8. [用户端 — 数据分析 `/user/analysis`](#8-用户端--数据分析-useranalysis)
9. [管理端 — 用户管理 `/admin/user`](#9-管理端--用户管理-adminuser)
10. [管理端 — 答卷查看 `/admin/fill`](#10-管理端--答卷查看-adminfill)
11. [管理端 — 系统管理 `/admin`](#11-管理端--系统管理-admin)
12. [管理端 — 问卷模板 `/admin/templates`](#12-管理端--问卷模板-admintemplates)

---

## 1. 通用说明

### 1.1 响应格式

所有接口返回统一的 JSON 响应体：

```json
// 成功
{ "code": 1, "msg": "success", "data": { ... } }

// 失败
{ "code": 0, "msg": "错误信息" }
```

### 1.2 分页响应格式

```json
{
  "page": 1,
  "size": 10,
  "total": 100,
  "records": [ ... ]
}
```

### 1.3 认证方式

使用 **Bearer Token** 认证，在请求头中携带：

```
Authorization: Bearer {token}
```

**流程**:
1. 调用 `POST /auth/password-login` 登录，获取 token
2. 后续所有接口（除 `/auth/**` 外）均需在请求头携带 token
3. Token 在 Redis 中存储，连续操作会自动续期

### 1.4 字段类型约定

| 字段 | 类型 | 含义 |
|------|------|------|
| `isAnonymous` | Integer | 0=不匿名, 1=匿名 |
| `allowMultiSubmit` | Integer | 0=不允许重复提交, 1=允许 |
| `required` | Boolean | true=必填, false=选填 |
| `submitted` | Boolean | true=已提交, false=未提交 |
| `status` (问卷) | String | `DRAFT` / `PUBLISHED` / `CLOSED` |
| `type` (问卷) | String | `PUBLIC` / `ASSIGNED` |
| `type` (题目) | String | `RADIO` / `CHECKBOX` / `TEXT` / `RATING` |

### 1.5 答案值编码规则

| 题目类型 | value 格式 | 示例 |
|----------|-----------|------|
| RADIO | 选项 ID | `"3"` |
| CHECKBOX | 逗号分隔的选项 ID | `"1,3,5"` |
| TEXT | 纯文本 | `"这是我的回答"` |
| RATING | 评分数值 | `"4"` |

---

## 2. 认证模块 `/auth`

> 无需登录，所有接口均可匿名访问。

### 2.1 发送验证码

发送短信验证码到指定手机号。

```
POST /auth/sendcode?phone={phone}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号（11位） |

**响应** `data`: `null`

---

### 2.2 用户注册

```
POST /auth/register
Content-Type: application/json
```

```json
{
  "username": "用户名",
  "password": "密码",
  "phone": "手机号",
  "email": "邮箱",
  "code": "短信验证码"
}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| username | 是 | 用户名 |
| password | 是 | 密码（明文，后端用 MD5 加密） |
| phone | 是 | 手机号 |
| email | 否 | 邮箱 |
| code | 是 | 短信验证码 |

**响应** `data`: `null`，`msg: "注册成功"`

---

### 2.3 密码登录

```
POST /auth/password-login
Content-Type: application/json
```

```json
{
  "username": "用户名",
  "password": "密码"
}
```

**响应** `data`:

```json
{
  "token": "uuid-token-string",
  "expiresIn": "1440",
  "user": {
    "id": 1,
    "username": "用户名",
    "role": "USER",
    "image": "头像URL"
  }
}
```

> 登录成功后前端需保存 token，后续请求放在 `Authorization: Bearer {token}` 头中。

---

### 2.4 退出登录

```
POST /auth/logout
```

**请求头**: `Authorization: Bearer {token}`

**响应** `data`: `null`，`msg: "退出登录成功！"`

---

### 2.5 忘记密码 — 验证身份

```
POST /auth/forget-password/checkInfo?phone={phone}&username={username}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号 |
| username | String | 是 | 用户名 |

验证通过时 `msg: "验证成功，请重置密码"`

---

### 2.6 忘记密码 — 验证码校验

```
POST /auth/forget-password/checkcode?phone={phone}&code={code}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号 |
| code | String | 是 | 短信验证码 |

**响应** `data`: `null`

---

### 2.7 重置密码

```
PUT /auth/reset-password?phone={phone}&new_password={newPassword}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号 |
| new_password | String | 是 | 新密码 |

**响应** `data`: `null`，`msg: "密码重置成功"`

---

## 3. 文件上传 `/common`

> 需要登录。用于头像上传。

### 3.1 上传文件

上传文件到阿里云 OSS，返回 URL。

```
POST /common/upload
Content-Type: multipart/form-data
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 上传的文件 |

**响应** `data`（直接返回文件 URL 字符串）:

```
"https://osap-moseery.oss-cn-beijing.aliyuncs.com/xxx.jpg"
```

> **头像上传流程**: 上传文件 → 获取返回的 URL → 调用修改资料接口将 URL 写入 `image` 字段。

---

## 4. 用户端 — 用户信息 `/user/user`

> 需要登录。

### 4.1 查询个人信息

```
POST /user/user/profile
```

**响应** `data`:

```json
{
  "id": 1,
  "username": "用户名",
  "phone": "手机号",
  "email": "邮箱",
  "image": "头像URL",
  "role": "USER",
  "status": 0,
  "surveyCount": 5,
  "responseCount": 10,
  "createAt": "2026-01-01T00:00:00",
  "updateAt": "2026-06-01T00:00:00"
}
```

---

### 4.2 修改个人资料

```
PUT /user/user/profile
Content-Type: application/json
```

```json
{
  "username": "新用户名",
  "email": "新邮箱",
  "image": "头像URL（从文件上传接口获取）"
}
```

所有字段均为可选。

---

### 4.3 修改密码

```
POST /user/user/update-password
Content-Type: application/json
```

```json
{
  "oldPassword": "原密码",
  "newPassword": "新密码"
}
```

---

### 4.4 修改手机号

> 先调用 `POST /auth/sendcode?phone={newPhone}` 获取验证码。

```
PUT /user/user/update-phone
Content-Type: application/json
```

```json
{
  "oldPhone": "旧手机号",
  "newPhone": "新手机号",
  "code": "短信验证码"
}
```

---

## 5. 用户端 — 问卷管理 `/user/survey`

> 需要登录。当前登录用户即创建者。

### 5.1 创建问卷

```
POST /user/survey/surveys
Content-Type: application/json
```

```json
{
  "title": "问卷标题",
  "description": "问卷描述",
  "type": "PUBLIC",
  "targetPhones": ["138xxxx", "139xxxx"],
  "startTime": "2026-06-01T00:00:00",
  "endTime": "2026-07-01T00:00:00",
  "isAnonymous": 0,
  "allowMultiSubmit": 0,
  "theme": "主题样式"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | 是 | 问卷标题 |
| type | String | 否 | `PUBLIC`(公开) / `ASSIGNED`(指定用户)，默认 `PUBLIC` |
| targetPhones | String[] | 否 | type=ASSIGNED 时必填 |
| isAnonymous | Integer | 否 | 0=不匿名 / 1=匿名，默认 0 |
| allowMultiSubmit | Integer | 否 | 0=不允许重复提交 / 1=允许，默认 0 |
| startTime | String | 否 | 开始时间 |
| endTime | String | 否 | 结束时间 |

**响应** `data`:

```json
{
  "id": 1,
  "title": "问卷标题",
  "description": "描述",
  "type": "PUBLIC",
  "status": "DRAFT",
  "isAnonymous": 0,
  "allowMultiSubmit": 0,
  "questionCount": 0,
  "responseCount": 0,
  "creator": { "id": 1, "username": "创建者" },
  "createAt": "2026-06-24T00:00:00",
  "updateAt": "2026-06-24T00:00:00"
}
```

---

### 5.2 分页查询问卷列表

```
GET /user/survey/surveys?page=1&size=10&status=DRAFT&keyword=标题&sortBy=createAt&sortOrder=desc
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认 1 |
| size | Integer | 否 | 每页条数，默认 10 |
| status | String | 否 | 筛选：`DRAFT` / `PUBLISHED` / `CLOSED` |
| keyword | String | 否 | 标题模糊搜索 |
| sortBy | String | 否 | 排序字段 |
| sortOrder | String | 否 | `asc` / `desc` |

**响应** `data`（分页）:

```json
{
  "page": 1,
  "size": 10,
  "total": 50,
  "records": [
    {
      "id": 1,
      "title": "问卷标题",
      "status": "DRAFT",
      "type": "PUBLIC",
      "createAt": "2026-06-24T00:00:00",
      "updateAt": "2026-06-24T00:00:00"
    }
  ]
}
```

---

### 5.3 获取问卷详情（含题目）

```
GET /user/survey/surveys/{surveyId}
```

| 参数 | 说明 |
|------|------|
| surveyId | 问卷 ID |

**响应** `data`:

```json
{
  "id": 1,
  "title": "问卷标题",
  "description": "描述",
  "type": "PUBLIC",
  "status": "DRAFT",
  "isAnonymous": 0,
  "allowMultiSubmit": 0,
  "questionCount": 3,
  "responseCount": 0,
  "creator": { "id": 1, "username": "创建者" },
  "createAt": "...",
  "updateAt": "...",
  "theme": "主题样式",
  "startTime": "...",
  "endTime": "...",
  "questionList": [
    {
      "id": 1,
      "type": "RADIO",
      "title": "题目内容",
      "required": true,
      "sortOrder": 1,
      "minRating": null,
      "maxRating": null,
      "options": [
        { "id": 1, "label": "选项A", "sortOrder": 1 }
      ]
    }
  ]
}
```

---

### 5.4 更新问卷

```
PUT /user/survey/surveys/{surveyId}
Content-Type: application/json
```

```json
{
  "title": "新标题",
  "description": "新描述",
  "startTime": "2026-06-01T00:00:00",
  "endTime": "2026-07-01T00:00:00",
  "theme": "主题样式"
}
```

所有字段均为可选。

**响应** `data`:

```json
{
  "id": 1,
  "title": "新标题",
  "updateAt": "2026-06-24T15:00:00"
}
```

---

### 5.5 删除问卷

```
DELETE /user/survey/surveys/{surveyId}
```

> 仅问卷创建者可删除。

---

### 5.6 发布问卷

将问卷从「草稿」状态变为「已发布」。

```
PUT /user/survey/surveys/{surveyId}/publish
```

**响应** `data`:

```json
{
  "id": 1,
  "status": "PUBLISHED",
  "updateAt": "2026-06-24T15:00:00"
}
```

---

### 5.7 关闭问卷

将问卷从「已发布」状态变为「已关闭」。

```
PUT /user/survey/surveys/{surveyId}/close
```

**响应** `data`:

```json
{
  "id": 1,
  "status": "CLOSED",
  "updateAt": "2026-06-24T15:00:00"
}
```

---

### 5.8 复制问卷

深度复制问卷（包含所有题目和选项）。

```
POST /user/survey/surveys/{surveyId}/copy
```

**响应** `data`:

```json
{
  "id": 2,
  "title": "原标题 - 副本",
  "status": "DRAFT",
  "questionCount": 3,
  "responseCount": 0,
  "createAt": "2026-06-24T15:00:00"
}
```

---

### 5.9 预览问卷

```
GET /user/survey/surveys/{surveyId}/preview
```

**响应** `data`:

```json
{
  "surveyId": 1,
  "title": "问卷标题",
  "description": "描述",
  "isAnonymous": 0,
  "status": "DRAFT",
  "questions": [
    {
      "id": 1,
      "type": "RADIO",
      "title": "题目内容",
      "required": true,
      "sortOrder": 1,
      "options": [
        { "id": 1, "label": "选项A", "sortOrder": 1 }
      ]
    }
  ]
}
```

---

## 6. 用户端 — 题目管理 `/user/question`

> 需要登录。只有问卷创建者可操作题目。

### 6.1 添加题目

```
POST /user/question/surveys/{surveyId}/questions
Content-Type: application/json
```

| 参数 | 说明 |
|------|------|
| surveyId | 问卷 ID |

```json
{
  "type": "RADIO",
  "title": "题目内容",
  "required": true,
  "sortOrder": 1,
  "minRating": 1,
  "maxRating": 5,
  "options": [
    { "label": "选项A", "sortOrder": 1 },
    { "label": "选项B", "sortOrder": 2 }
  ]
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | String | 是 | `RADIO` / `CHECKBOX` / `TEXT` / `RATING` |
| title | String | 是 | 题目内容 |
| required | Boolean | 否 | 是否必填 |
| sortOrder | Integer | 否 | 排序序号 |
| options | Array | RADIO/CHECKBOX 必填 | 选项列表 |
| minRating | Integer | RATING 时填 | 最低评分 |
| maxRating | Integer | RATING 时填 | 最高评分 |

**响应** `data`（创建后的题目完整信息）:

```json
{
  "id": 1,
  "type": "RADIO",
  "title": "题目内容",
  "required": true,
  "sortOrder": 1,
  "options": [
    { "id": 1, "label": "选项A", "sortOrder": 1 }
  ]
}
```

---

### 6.2 修改题目

```
PUT /user/question/surveys/{surveyId}/questions/{questionId}
Content-Type: application/json
```

**请求体**: 同添加题目。

**响应** `data`: 返回更新后的题目完整信息。

---

### 6.3 删除题目

```
DELETE /user/question/surveys/{surveyId}/questions/{questionId}
```

> 同时删除该题目的所有选项。

---

### 6.4 调整题目顺序

```
PUT /user/question/surveys/{surveyId}/questions/order
Content-Type: application/json
```

```json
{
  "questionIds": [3, 1, 2]
}
```

> 数组的顺序即为题目新的排序顺序。

---

## 7. 用户端 — 填写问卷 `/user/fill`

> 需要登录。

### 7.1 获取问卷（填写用）

```
GET /user/fill/surveys/{surveyId}/fill
```

**响应** `data`:

```json
{
  "surveyId": 1,
  "title": "问卷标题",
  "description": "描述",
  "isAnonymous": 0,
  "submitted": false,
  "questions": [
    {
      "id": 1,
      "type": "RADIO",
      "title": "题目内容",
      "required": true,
      "sortOrder": 1,
      "options": [
        { "id": 1, "label": "选项A", "sortOrder": 1 }
      ]
    }
  ]
}
```

> `submitted`: 当问卷不允许重复提交且用户已提交过时为 `true`。

---

### 7.2 提交答卷

幂等提交：同一 `idempotencyKey` 重复提交会返回原 responseId 而不创建新记录。

```
POST /user/fill/surveys/{surveyId}/responses
Content-Type: application/json
```

```json
{
  "answers": [
    { "questionId": 1, "value": "3" },
    { "questionId": 2, "value": "这是文本回答" }
  ],
  "idempotencyKey": "前端生成的UUID",
  "duration": 120
}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| answers | 是 | RADIO/CHECKBOX 的 value 填选项ID，TEXT 填文本 |
| idempotencyKey | 否 | 防重复提交，前端生成 UUID |
| duration | 否 | 填写耗时（秒） |

**响应** `data`:

```json
{
  "responseId": 1
}
```

---

### 7.3 获取答卷列表

```
GET /user/fill/surveys/{surveyId}/responses?page=1&size=10
```

**响应** `data`（分页）:

```json
{
  "page": 1,
  "size": 10,
  "total": 50,
  "records": [
    {
      "id": 1,
      "respondent": "用户名",
      "submittedAt": "2026-06-24T15:00:00",
      "duration": 120
    }
  ]
}
```

---

### 7.4 获取答卷详情

```
GET /user/fill/surveys/{surveyId}/responses/{responseId}
```

**响应** `data`:

```json
{
  "id": 1,
  "respondent": "用户名",
  "submittedAt": "2026-06-24T15:00:00",
  "duration": 120,
  "answers": [
    {
      "questionId": 1,
      "questionTitle": "你的性别是？",
      "questionType": "RADIO",
      "value": "3",
      "label": "男"
    }
  ]
}
```

---

### 7.5 获取待填问卷

返回当前用户可填写的公开问卷和被指派的问卷。

```
GET /user/fill/surveys/my-assigned?page=1&size=10
```

**响应** `data`（分页）:

```json
{
  "page": 1,
  "size": 10,
  "total": 20,
  "records": [
    {
      "id": 1,
      "title": "问卷标题",
      "description": "描述",
      "type": "PUBLIC",
      "questionCount": 5,
      "endTime": "2026-07-01T00:00:00",
      "creator": { "id": 1, "username": "创建者" },
      "createdAt": "2026-06-01T00:00:00"
    }
  ]
}
```

---

## 8. 用户端 — 数据分析 `/user/analysis`

> 需要登录。仅问卷创建者可查看。

### 8.1 问卷统计概览

```
GET /user/analysis/surveys/{surveyId}/overview
```

**响应** `data`:

```json
{
  "surveyId": 1,
  "title": "问卷标题",
  "totalResponses": 100,
  "completionRate": 85.5,
  "averageDuration": 180,
  "questionCount": 5,
  "dailyResponses": [
    { "date": "2026-06-20", "count": 15 },
    { "date": "2026-06-21", "count": 20 }
  ]
}
```

---

### 8.2 题目统计分析

```
GET /user/analysis/surveys/{surveyId}/questions
```

**响应** `data`:

```json
{
  "questions": [
    {
      "questionId": 1,
      "questionTitle": "你的性别是？",
      "questionType": "RADIO",
      "totalAnswers": 100,
      "skipCount": 2,
      "statistics": {
        "type": "RADIO",
        "options": [
          { "label": "男", "count": 60, "percentage": 60.0 },
          { "label": "女", "count": 40, "percentage": 40.0 }
        ],
        "averageScore": null,
        "maxScore": null,
        "minScore": null,
        "distribution": null,
        "wordCloud": null
      }
    },
    {
      "questionId": 2,
      "questionTitle": "服务评分",
      "questionType": "RATING",
      "totalAnswers": 80,
      "skipCount": 0,
      "statistics": {
        "type": "RATING",
        "options": [],
        "averageScore": 3.5,
        "maxScore": 5,
        "minScore": 1,
        "distribution": [
          { "score": 1, "count": 5, "percentage": 6.25 },
          { "score": 2, "count": 10, "percentage": 12.5 },
          { "score": 3, "count": 20, "percentage": 25.0 },
          { "score": 4, "count": 30, "percentage": 37.5 },
          { "score": 5, "count": 15, "percentage": 18.75 }
        ],
        "wordCloud": null
      }
    }
  ]
}
```

> 不同类型返回不同统计字段：
> - `RADIO` / `CHECKBOX`: `options`（选项分布）
> - `RATING`: `averageScore`、`maxScore`、`minScore`、`distribution`
> - `TEXT`: `wordCloud`（词云拼接的文本字符串）

---

### 8.3 词云数据

```
GET /user/analysis/surveys/{surveyId}/wordcloud
```

**响应** `data`:

```json
{
  "words": [
    { "word": "满意", "count": 42 },
    { "word": "服务", "count": 35 }
  ]
}
```

---

### 8.4 导出数据

导出答卷数据为 Excel 或 CSV 格式。

```
GET /user/analysis/surveys/{surveyId}/export?format=excel
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| format | String | 否 | `excel`(默认) 或 `csv` |

**响应**: 文件流下载。

---

## 9. 管理端 — 用户管理 `/admin/user`

> 需要登录。接口与用户端完全一致，前缀为 `/admin/user`。

| 接口 | 说明 |
|------|------|
| `POST /admin/user/profile` | 查询个人信息 |
| `PUT /admin/user/profile` | 修改个人资料 |
| `POST /admin/user/update-password` | 修改密码 |
| `PUT /admin/user/update-phone` | 修改手机号 |

请求体/响应体同用户端对应接口。

---

## 10. 管理端 — 答卷查看 `/admin/fill`

> 需要登录。查看任意用户对问卷的提交记录。

### 10.1 答卷列表

```
GET /admin/fill/surveys/{surveyId}/responses?page=1&size=10
```

**响应** `data`（分页）: 同 7.3。

### 10.2 答卷详情

```
GET /admin/fill/surveys/{surveyId}/responses/{responseId}
```

**响应** `data`: 同 7.4。

---

## 11. 管理端 — 系统管理 `/admin`

> 需要登录。管理员功能。

### 11.1 用户列表

```
GET /admin/users?page=1&size=10&keyword=搜索&status=状态
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 否 | 按用户名/手机号模糊搜索 |
| status | String | 否 | 筛选用户状态 |

**响应** `data`（分页）:

```json
{
  "page": 1,
  "size": 10,
  "total": 100,
  "records": [
    {
      "id": 1,
      "username": "用户名",
      "phone": "手机号",
      "email": "邮箱",
      "role": "USER",
      "status": 0,
      "surveyCount": 5,
      "createAt": "2026-01-01T00:00:00"
    }
  ]
}
```

---

### 11.2 修改用户状态

```
PUT /admin/users/{userId}/status
Content-Type: application/json
```

```json
{
  "status": "1"
}
```

---

### 11.3 问卷列表（管理端）

```
GET /admin/surveys?page=1&size=10&status=DRAFT
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | String | 否 | `DRAFT` / `PUBLISHED` / `CLOSED` |

**响应** `data`（分页）:

```json
{
  "page": 1,
  "size": 10,
  "total": 50,
  "records": [
    {
      "id": 1,
      "title": "问卷标题",
      "status": "DRAFT",
      "type": "PUBLIC",
      "questionCount": 5,
      "responseCount": 10,
      "creator": { "id": 1, "username": "创建者" },
      "createAt": "...",
      "updateAt": "..."
    }
  ]
}
```

---

### 11.4 强制删除问卷

```
DELETE /admin/surveys/{surveyId}
```

---

### 11.5 查询操作日志

```
GET /admin/logs?page=1&size=20&type=操作类型&startTime=&endTime=&keyword=
```

**响应** `data`（分页）:

```json
{
  "page": 1,
  "size": 20,
  "total": 200,
  "records": [
    {
      "id": 1,
      "type": "问卷",
      "operator": "管理员",
      "action": "删除问卷",
      "target": "问卷标题",
      "ip": "127.0.0.1",
      "createAt": "2026-06-24T15:00:00"
    }
  ]
}
```

---

### 11.6 创建数据备份

```
POST /admin/backup
```

**响应** `data`:

```json
{
  "id": 1,
  "fileName": "backup_20260624.json",
  "fileSize": 1024000,
  "createAt": "2026-06-24T15:00:00"
}
```

---

### 11.7 备份列表

```
GET /admin/backups?page=1&size=10
```

**响应** `data`（分页）: `records` 中每个元素同 11.6 结构。

---

### 11.8 下载备份文件

```
GET /admin/backups/{backupId}/download
```

**响应**: 文件流下载。

---

### 11.9 删除备份

```
DELETE /admin/backups/{backupId}
```

---

## 12. 管理端 — 问卷模板 `/admin/templates`

> 需要登录。

### 12.1 创建模板

```
POST /admin/templates
Content-Type: application/json
```

```json
{
  "title": "模板标题",
  "description": "模板描述",
  "category": "分类",
  "questions": [
    {
      "type": "RADIO",
      "title": "题目内容",
      "required": true,
      "sortOrder": 1,
      "options": [
        { "label": "选项A", "sortOrder": 1 }
      ]
    }
  ]
}
```

**响应** `data`:

```json
{
  "id": 1,
  "title": "模板标题",
  "description": "描述",
  "category": "分类",
  "questionCount": 5,
  "useCount": 0,
  "creator": { "id": 1, "username": "创建者" },
  "questions": [ ... ],
  "createAt": "...",
  "updateAt": "..."
}
```

---

### 12.2 模板列表

```
GET /admin/templates?page=1&size=10&category=分类
```

**响应** `data`（分页）:

```json
{
  "page": 1,
  "size": 10,
  "total": 20,
  "records": [
    {
      "id": 1,
      "title": "模板标题",
      "category": "分类",
      "questionCount": 5,
      "useCount": 3,
      "createAt": "..."
    }
  ]
}
```

---

### 12.3 模板详情

```
GET /admin/templates/{templateId}
```

**响应** `data`: 同 12.1 返回结构。

---

### 12.4 更新模板

```
PUT /admin/templates/{templateId}
Content-Type: application/json
```

参数同创建模板。

---

### 12.5 删除模板

```
DELETE /admin/templates/{templateId}
```

---

### 12.6 应用模板创建问卷

```
POST /admin/templates/{templateId}/apply?title=自定义标题&description=自定义描述
```

**响应** `data`:

```json
{
  "id": 2,
  "title": "自定义标题",
  "status": "DRAFT",
  "questionCount": 5,
  "responseCount": 0,
  "createAt": "..."
}
```

---

## 附录

### 附录 A：枚举常量汇总

| 枚举 | 可选值 |
|------|--------|
| 问卷状态 `status` | `DRAFT`, `PUBLISHED`, `CLOSED` |
| 问卷类型 `type` | `PUBLIC`, `ASSIGNED` |
| 题目类型 `questionType` | `RADIO`, `CHECKBOX`, `TEXT`, `RATING` |
| 导出格式 `format` | `excel`, `csv` |

### 附录 B：HTTP 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 401 | 未登录或 token 无效/过期 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
