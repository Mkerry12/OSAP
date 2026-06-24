# OSAP 在线问卷调查系统 — 前端接口文档

**版本**: v1.0  
**基础地址**: `http://localhost:8081`  
**Content-Type**: `application/json`

---

## 目录

1. [通用说明](#1-通用说明)
2. [认证模块](#2-认证模块-auth)
3. [通用模块](#3-通用模块-common)
4. [问卷管理](#4-问卷管理-user-survey)
5. [题目管理](#5-题目管理-user-question)
6. [问卷填写](#6-问卷填写-user-fill)
7. [数据分析](#7-数据分析-user-analysis)
8. [个人中心](#8-个人中心-user-user)
9. [管理端-用户管理](#9-管理端-用户管理-admin-user)
10. [管理端-答卷查看](#10-管理端-答卷查看-admin-fill)

---

## 1. 通用说明

### 1.1 响应格式

所有接口返回统一的 JSON 响应体：

```json
{
  "code": 1,        // 1=成功, 0=失败
  "msg": "success",
  "data": { ... }   // 具体业务数据
}
```

### 1.2 分页响应格式

```json
{
  "page": 1,
  "size": 10,
  "total": 100,
  "records": [ ... ]  // 每页数据列表
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
3. Token 有效期为 30 分钟，连续操作会自动续期

### 1.4 问卷状态说明

| 状态值 | 说明 |
|--------|------|
| DRAFT | 草稿 |
| PUBLISHED | 已发布（可填写） |
| CLOSED | 已关闭 |

### 1.5 问卷类型说明

| 类型值 | 说明 |
|--------|------|
| PUBLIC | 公开问卷（任何人可填写） |
| ASSIGNED | 指定用户填写（仅在 targetPhones 列表中的用户可填） |

### 1.6 题目类型说明

| 类型值 | 说明 |
|--------|------|
| RADIO | 单选题 |
| CHECKBOX | 多选题 |
| TEXT | 文本题 |
| RATING | 评分题 |
| DROPDOWN | 下拉题 |

### 1.7 答案值编码规则

| 题目类型 | value 格式 | 示例 |
|----------|-----------|------|
| RADIO | 选项 ID（数字） | `"3"` |
| CHECKBOX | 逗号分隔的选项 ID | `"1,3,5"` |
| TEXT | 纯文本 | `"这是我的回答"` |
| RATING | 评分数值 | `"4"` |
| DROPDOWN | 选项 ID（数字） | `"2"` |

---

## 2. 认证模块 `/auth`

> 无需登录，所有接口均可匿名访问。

### 2.1 发送验证码

发送短信验证码到指定手机号。

```
POST /auth/sendcode
```

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | String | 是 | 手机号（11位） |

**响应** `data`: `null`

**错误码**: 手机号格式错误时返回 `code=0`, `msg="手机号格式错误！"`

---

### 2.2 用户注册

```
POST /auth/register
```

**请求体**:

```json
{
  "username": "string",     // 必填，用户名
  "password": "string",     // 必填，密码（明文，后端用 MD5 加密）
  "phone": "string",        // 必填，手机号
  "email": "string",        // 可选，邮箱
  "code": "string"          // 必填，短信验证码
}
```

**响应** `data`: `null`

---

### 2.3 密码登录

```
POST /auth/password-login
```

**请求体**:

```json
{
  "username": "string",     // 必填，用户名
  "password": "string"      // 必填，密码
}
```

**响应** `data`:

```json
{
  "token": "uuid-string",
  "expiresIn": "1800",
  "user": {
    "id": 1,
    "username": "string",
    "status": 1,
    "phone": "13800138000",
    "email": "user@example.com",
    "image": "http://...",
    "role": "string",
    "createAt": "2026-06-24T10:00:00",
    "updateAt": "2026-06-24T10:00:00",
    "surveyCount": 0,
    "responseCount": 0
  }
}
```

---

### 2.4 退出登录

```
POST /auth/logout
```

**请求头**: `Authorization: Bearer {token}`

**响应** `data`: `null`, `msg: "退出登录成功！"`

---

### 2.5 忘记密码 — 验证身份

```
POST /auth/forget-password/checkInfo
```

**请求体**:

```json
{
  "phone": "string",        // 必填
  "username": "string"      // 必填
}
```

**响应** `data`: `null`  
验证通过时 `msg: "验证成功，请重置密码"`

---

### 2.6 忘记密码 — 验证码校验

```
POST /auth/forget-password/checkcode
```

**请求体**:

```json
{
  "phone": "string",        // 必填
  "username": "string",     // 必填
  "code": "string"          // 必填，短信验证码
}
```

**响应** `data`: `null`

---

### 2.7 重置密码

```
PUT /auth/reset-password
```

**请求体**:

```json
{
  "phone": "string",        // 必填
  "username": "string",     // 必填
  "password": "string"      // 必填，新密码
}
```

**响应** `data`: `null`, `msg: "密码重置成功"`

---

## 3. 通用模块 `/common`

> 需要登录。

### 3.1 文件上传

上传文件到阿里云 OSS。

```
POST /common/upload
```

**请求格式**: `multipart/form-data`

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | 上传的文件 |

**响应** `data`:

```json
"https://osap-moseery.oss-cn-beijing.aliyuncs.com/xxx.jpg"
```

（直接返回文件 URL 字符串）

---

## 4. 问卷管理 `/user/survey`

> 需要登录。当前登录用户即创建者（owner）。

### 4.1 创建问卷

```
POST /user/survey/surveys
```

**请求体**:

```json
{
  "title": "string",              // 必填，问卷标题
  "description": "string",        // 可选，问卷描述
  "type": "PUBLIC",               // 可选，PUBLIC/ASSIGNED，默认 PUBLIC
  "targetPhones": ["138..."],     // 可选，ASSIGNED 类型时指定可填写的手机号列表
  "startTime": "2026-07-01T00:00:00",  // 可选，开始时间
  "endTime": "2026-07-31T00:00:00",    // 可选，结束时间
  "isAnonymous": false,           // 可选，是否匿名，默认 false
  "theme": "string",              // 可选，主题色
  "allowMultiSubmit": false       // 可选，是否允许重复提交，默认 false
}
```

**响应** `data`:

```json
{
  "id": 1,
  "title": "string",
  "description": "string",
  "type": "PUBLIC",
  "status": "DRAFT",
  "isAnonymous": false,
  "allowMultiSubmit": false,
  "questionCount": 0,
  "responseCount": 0,
  "creator": {
    "id": 1,
    "username": "string"
  },
  "createAt": "2026-06-24T10:00:00",
  "updateAt": "2026-06-24T10:00:00"
}
```

---

### 4.2 分页查询问卷列表

```
GET /user/survey/surveys?page=1&size=10&status=PUBLISHED&keyword=xxx&sortBy=createAt&sortOrder=desc
```

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码，默认 1 |
| size | Integer | 否 | 每页条数，默认 10 |
| status | String | 否 | 筛选状态：DRAFT/PUBLISHED/CLOSED |
| keyword | String | 否 | 按标题搜索 |
| sortBy | String | 否 | 排序字段：createAt/updateAt |
| sortOrder | String | 否 | 排序方向：asc/desc |

**响应** `data`: 分页格式

```json
{
  "page": 1,
  "size": 10,
  "total": 50,
  "records": [
    {
      "id": 1,
      "title": "string",
      "status": "PUBLISHED",
      "type": "PUBLIC",
      "createAt": "2026-06-24T10:00:00",
      "updateAt": "2026-06-24T10:00:00"
    }
  ]
}
```

---

### 4.3 获取问卷详情（含题目）

```
GET /user/survey/surveys/{surveyId}
```

**路径参数**:

| 参数名 | 说明 |
|--------|------|
| surveyId | 问卷 ID |

**响应** `data`:

```json
{
  "id": 1,
  "title": "string",
  "description": "string",
  "type": "PUBLIC",
  "status": "DRAFT",
  "isAnonymous": false,
  "allowMultiSubmit": false,
  "questionCount": 3,
  "responseCount": 0,
  "creator": {
    "id": 1,
    "username": "string"
  },
  "createAt": "2026-06-24T10:00:00",
  "updateAt": "2026-06-24T10:00:00",
  "theme": "string",
  "startTime": "2026-07-01T00:00:00",
  "endTime": "2026-07-31T00:00:00",
  "questionList": [
    {
      "id": 1,
      "type": "RADIO",
      "title": "string",
      "required": true,
      "sortOrder": 1,
      "options": [
        {
          "id": 1,
          "label": "选项A",
          "sortOrder": 0
        }
      ],
      "minRating": null,
      "maxRating": null
    }
  ]
}
```

---

### 4.4 更新问卷信息

```
PUT /user/survey/surveys/{surveyId}
```

**路径参数**:

| 参数名 | 说明 |
|--------|------|
| surveyId | 问卷 ID |

**请求体**:

```json
{
  "title": "string",              // 可选，新标题
  "description": "string",        // 可选，新描述
  "startTime": "2026-07-01T00:00:00",  // 可选
  "endTime": "2026-07-31T00:00:00",    // 可选
  "theme": "string"               // 可选
}
```

**响应** `data`:

```json
{
  "id": 1,
  "title": "string",
  "updateAt": "2026-06-24T12:00:00"
}
```

---

### 4.5 删除问卷

```
DELETE /user/survey/surveys/{surveyId}
```

**路径参数**:

| 参数名 | 说明 |
|--------|------|
| surveyId | 问卷 ID |

**响应** `data`: `null`

> 仅问卷创建者可删除。

---

### 4.6 发布问卷

将问卷从「草稿」状态变为「已发布」。

```
PUT /user/survey/surveys/{surveyId}/publish
```

**响应** `data`:

```json
{
  "id": 1,
  "status": "PUBLISHED",
  "updateAt": "2026-06-24T12:00:00"
}
```

---

### 4.7 关闭问卷

将问卷从「已发布」状态变为「已关闭」。

```
PUT /user/survey/surveys/{surveyId}/close
```

**响应** `data`:

```json
{
  "id": 1,
  "status": "CLOSED",
  "updateAt": "2026-06-24T12:00:00"
}
```

---

### 4.8 复制问卷

深度复制问卷（包含所有题目和选项，生成全新 ID）。

```
POST /user/survey/surveys/{surveyId}/copy
```

**响应** `data`:

```json
{
  "id": 2,
  "title": "string (副本)",
  "status": "DRAFT",
  "questionCount": 3,
  "responseCount": 0,
  "createAt": "2026-06-24T12:00:00"
}
```

---

### 4.9 预览问卷

获取问卷的预览信息（仅供创建者预览，不含敏感数据）。

```
GET /user/survey/surveys/{surveyId}/preview
```

**响应** `data`:

```json
{
  "surveyId": 1,
  "title": "string",
  "description": "string",
  "isAnonymous": 0,
  "status": "DRAFT",
  "questions": [
    {
      "id": 1,
      "type": "RADIO",
      "title": "string",
      "required": true,
      "sortOrder": 1,
      "options": [
        {
          "id": 1,
          "label": "选项A",
          "sortOrder": 0
        }
      ],
      "minRating": null,
      "maxRating": null
    }
  ]
}
```

---

## 5. 题目管理 `/user/question`

> 需要登录。只有问卷创建者可操作题目。

### 5.1 添加题目

```
POST /user/question/surveys/{surveyId}/questions
```

**路径参数**:

| 参数名 | 说明 |
|--------|------|
| surveyId | 问卷 ID |

**请求体**:

```json
{
  "type": "RADIO",              // 必填，RADIO/CHECKBOX/TEXT/RATING/DROPDOWN
  "title": "string",            // 必填，题目内容
  "required": true,             // 可选，是否必填，默认 false
  "sortOrder": 1,               // 可选，排序序号
  "options": [                  // 可选，选择题的选项列表（RADIO/CHECKBOX/DROPDOWN 必填）
    {
      "label": "选项A",          // 必填，选项文字
      "sortOrder": 0            // 可选，选项排序
    }
  ],
  "minRating": 1,               // 可选，RATING 类型时最小值
  "maxRating": 5                // 可选，RATING 类型时最大值
}
```

**响应** `data`: 返回创建的题目完整信息（同 4.3 中的 QuestionVO 结构）

---

### 5.2 修改题目

```
PUT /user/question/surveys/{surveyId}/questions/{questionId}
```

**路径参数**:

| 参数名 | 说明 |
|--------|------|
| surveyId | 问卷 ID |
| questionId | 题目 ID |

**请求体**: 同添加题目（见 5.1）

**响应** `data`: 返回更新后的题目完整信息

---

### 5.3 删除题目

```
DELETE /user/question/surveys/{surveyId}/questions/{questionId}
```

**响应** `data`: `null`

> 同时删除该题目的所有选项。

---

### 5.4 调整题目顺序

```
PUT /user/question/surveys/{surveyId}/questions/order
```

**路径参数**:

| 参数名 | 说明 |
|--------|------|
| surveyId | 问卷 ID |

**请求体**:

```json
{
  "questionIds": [3, 1, 2]   // 按目标顺序排列的题目 ID 列表
}
```

**响应** `data`: `null`

---

## 6. 问卷填写 `/user/fill`

> 需要登录。提交问卷的接口。

### 6.1 获取待填写的问卷

获取问卷的填写视图（包含所有题目，不含统计数据）。

```
GET /user/fill/surveys/{surveyId}/fill
```

**响应** `data`:

```json
{
  "surveyId": 1,
  "title": "string",
  "description": "string",
  "isAnonymous": false,
  "submitted": false,       // 当前用户是否已提交
  "questions": [
    {
      "id": 1,
      "type": "RADIO",
      "title": "string",
      "required": true,
      "sortOrder": 1,
      "options": [
        {
          "id": 1,
          "label": "选项A",
          "sortOrder": 0
        }
      ],
      "minRating": null,
      "maxRating": null
    }
  ]
}
```

---

### 6.2 提交答卷

幂等提交：同一 `idempotencyKey` 重复提交会返回原 responseId 而不创建新记录。

```
POST /user/fill/surveys/{surveyId}/responses
```

**路径参数**:

| 参数名 | 说明 |
|--------|------|
| surveyId | 问卷 ID |

**请求体**:

```json
{
  "answers": [
    {
      "questionId": 1,
      "value": "3"          // 答案值，编码规则见通用说明 1.7
    }
  ],
  "idempotencyKey": "uuid-string",   // 必填，前端生成 UUID，用于防重复提交
  "duration": 120           // 可选，填写耗时（秒）
}
```

**响应** `data`:

```json
{
  "responseId": 1
}
```

---

### 6.3 查看我的答卷列表

获取当前用户对某问卷的所有提交记录。

```
GET /user/fill/surveys/{surveyId}/responses
```

**响应** `data`:

```json
[
  {
    "id": 1,
    "respondent": "用户名",
    "submittedAt": "2026-06-24T10:00:00",
    "duration": 120
  }
]
```

---

### 6.4 查看答卷详情

```
GET /user/fill/surveys/{surveyId}/responses/{responseId}
```

**响应** `data`:

```json
{
  "id": 1,
  "respondent": "用户名",
  "submittedAt": "2026-06-24T10:00:00",
  "duration": 120,
  "answers": [
    {
      "questionId": 1,
      "questionTitle": "你的性别是？",
      "questionType": "RADIO",
      "value": "3",
      "label": "男"     // 选择题显示选项文字，文本题同 value
    }
  ]
}
```

---

### 6.5 获取分配给我的问卷

查询 ASSIGNED 类型且当前用户手机号在 targetPhones 中的问卷列表。

```
GET /user/fill/surveys/my-assigned
```

**响应** `data`:

```json
[
  {
    "id": 1,
    "title": "string",
    "description": "string",
    "type": "ASSIGNED",
    "questionCount": 5,
    "endTime": "2026-07-31T00:00:00",
    "creator": {
      "id": 1,
      "username": "creator_name"
    },
    "createdAt": "2026-06-24T10:00:00"
  }
]
```

---

## 7. 数据分析 `/user/analysis`

> 需要登录。仅问卷创建者可查看。

### 7.1 问卷概览

获取问卷的统计数据概览。

```
GET /user/analysis/surveys/{surveyId}/overview
```

**响应** `data`:

```json
{
  "surveyId": 1,
  "title": "string",
  "totalResponses": 100,
  "completionRate": 85.5,       // 完成率（%）
  "averageDuration": 180,       // 平均填写时长（秒）
  "questionCount": 5,
  "dailyResponses": [
    {
      "date": "2026-06-24",
      "count": 15
    }
  ]
}
```

---

### 7.2 题目分析

获取每个题目的详细统计数据。

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
          {
            "label": "男",
            "count": 60,
            "percentage": 60.0
          },
          {
            "label": "女",
            "count": 40,
            "percentage": 40.0
          }
        ],
        "averageScore": null,
        "maxScore": null,
        "minScore": null,
        "distribution": [],
        "wordCloud": null
      }
    },
    {
      "questionId": 2,
      "questionTitle": "评分题",
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
    },
    {
      "questionId": 3,
      "questionTitle": "文本题",
      "questionType": "TEXT",
      "totalAnswers": 50,
      "skipCount": 5,
      "statistics": {
        "type": "TEXT",
        "options": [],
        "averageScore": null,
        "maxScore": null,
        "minScore": null,
        "distribution": [],
        "wordCloud": "word1 word2 word3 ..."
      }
    }
  ]
}
```

---

### 7.3 词云数据

获取文本题的回答词频统计（已过滤停用词）。

```
GET /user/analysis/surveys/{surveyId}/wordcloud
```

**响应** `data`:

```json
{
  "words": [
    {
      "word": "满意",
      "count": 42
    },
    {
      "word": "服务",
      "count": 35
    }
  ]
}
```

---

### 7.4 导出数据

导出答卷数据为 Excel 或 CSV 格式。

```
GET /user/analysis/surveys/{surveyId}/export?format=excel
```

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| format | String | 否 | 导出格式：`excel`（默认）或 `csv` |

**响应**: 文件流下载（`Content-Disposition: attachment`）

---

## 8. 个人中心 `/user/user`

> 需要登录。管理当前用户的个人信息。

### 8.1 获取个人信息

```
POST /user/user/profile
```

**响应** `data`:

```json
{
  "id": 1,
  "username": "string",
  "status": 1,
  "phone": "13800138000",
  "email": "user@example.com",
  "image": "http://...",
  "role": "string",
  "createAt": "2026-06-24T10:00:00",
  "updateAt": "2026-06-24T10:00:00",
  "surveyCount": 5,
  "responseCount": 20
}
```

---

### 8.2 修改个人信息

```
PUT /user/user/profile
```

**请求体**:

```json
{
  "username": "new_name",   // 可选
  "email": "new@mail.com",  // 可选
  "image": "http://..."     // 可选，头像 URL
}
```

**响应** `data`: `null`

---

### 8.3 修改密码

```
POST /user/user/update-password
```

**请求体**:

```json
{
  "oldPassword": "旧密码",
  "newPassword": "新密码"
}
```

**响应** `data`: `null`

---

### 8.4 修改手机号

```
PUT /user/user/update-phone
```

**请求体**:

```json
{
  "oldPhone": "13800138000",     // 旧手机号
  "newPhone": "13900139000",     // 新手机号
  "code": "验证码"                 // 发送到新手机号的验证码
}
```

**响应** `data`: `null`

---

## 9. 管理端 — 用户管理 `/admin/user`

> 需要登录。功能与 `/user/user/*` 完全一致。

### 9.1 获取个人信息

```
POST /admin/user/profile
```

**响应**: 同 8.1

### 9.2 修改个人信息

```
PUT /admin/user/profile
```

**请求体**: 同 8.2

### 9.3 修改密码

```
POST /admin/user/update-password
```

**请求体**: 同 8.3

### 9.4 修改手机号

```
PUT /admin/user/update-phone
```

**请求体**: 同 8.4

---

## 10. 管理端 — 答卷查看 `/admin/fill`

> 需要登录。查看任意用户对问卷的提交记录。

### 10.1 答卷列表（分页）

```
GET /admin/fill/surveys/{surveyId}/responses?page=1&size=10
```

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码，默认 1 |
| size | Integer | 否 | 每页条数，默认 10 |

**响应** `data`: 分页格式

```json
{
  "page": 1,
  "size": 10,
  "total": 50,
  "records": [
    {
      "id": 1,
      "respondent": "用户名",
      "submittedAt": "2026-06-24T10:00:00",
      "duration": 120
    }
  ]
}
```

---

### 10.2 答卷详情

```
GET /admin/fill/surveys/{surveyId}/responses/{responseId}
```

**响应** `data`: 同 6.4

---

## 附录

### 附录 A：枚举常量汇总

| 枚举类型 | 可选值 |
|----------|--------|
| 问卷状态 `status` | `DRAFT`, `PUBLISHED`, `CLOSED` |
| 问卷类型 `type` | `PUBLIC`, `ASSIGNED` |
| 题目类型 `questionType` | `RADIO`, `CHECKBOX`, `TEXT`, `RATING`, `DROPDOWN` |
| 导出格式 `format` | `excel`, `csv` |

### 附录 B：HTTP 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 401 | 未登录或 token 无效/过期 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 附录 C：错误码

| code | 含义 |
|------|------|
| 1 | 成功 |
| 0 | 失败（具体错误见 `msg` 字段） |
