# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build entire project (skip tests)
mvn clean compile -DskipTests

# Run the server
mvn spring-boot:run -pl OSAP-server

# Run all tests
mvn test

# Run a single test class
mvn test -pl OSAP-server -Dtest=TestClassName

# Package as executable JAR
mvn clean package -DskipTests
```

## Requirements

- Java 17+ (Maven compiler target is 17)
- MySQL 8+ — database `osap` on port 3306 (configurable in `application.yaml`)
- Redis — default expects redis on `192.168.44.128:6379`
- Aliyun OSS — set env vars `OSS_ACCESS_KEY_ID` and `OSS_ACCESS_KEY_SECRET`

## Project Structure

Multi-module Maven project (Spring Boot 3.5.15, MyBatis, MySQL, Redis):

```
OSAP/                   -- parent POM (pom packaging)
├── OSAP-common/        -- shared utilities (no web dependency)
│   ├── constant/       -- RedisConstant, SystemConstant
│   ├── exception/      -- BaseException + subclasses (AccountNotFound, LoginFailed, UserNotLogin)
│   ├── properties/     -- AliOssProperties (@ConfigurationProperties)
│   ├── result/         -- Result<T>, PageResult (standardized API responses)
│   ├── UserHolder/     -- ThreadLocal holder for current logged-in user
│   └── utils/          -- RegexUtils, RegexPatterns
├── OSAP-pojo/          -- plain data objects (no Spring dependency)
│   ├── dto/            -- request DTOs (UserRegisterDTO, UserPasswordLoginDTO, etc.)
│   ├── entity/         -- entity classes (User, UserInfo)
│   └── vo/             -- view objects (UserPasswordLoginVO)
└── OSAP-server/        -- Spring Boot application (runs on port 8081)
    ├── config/         -- WebMvcConfig, OssConfiguration
    ├── controller/     -- AuthController, UserController
    ├── interceptor/    -- TokenRefreshInterceptor, LoginInterceptor
    ├── mapper/         -- UserMapper (MyBatis, annotation-based SQL)
    ├── service/        -- AuthService, UserService (interfaces + impls)
    └── OsapApplication.java
```

## Architecture & Key Patterns

- **Controller → Service (interface + impl) → Mapper (MyBatis)** — standard layered architecture
- **Token-based auth via Redis** — login generates a UUID token stored in Redis (key: `loginUser:token:{token}`, hash with user fields). Not JWT-based despite the JJWT dependency on classpath.
- **Two interceptor chain**: `TokenRefreshInterceptor` (order 0, all paths) extracts user from Redis token into `UserHolder` ThreadLocal; `LoginInterceptor` (order 1, excludes `/user/auth/**`) rejects requests with 401 if `UserHolder` is empty.
- **`UserHolder`** — `ThreadLocal<UserInfo>` holding `(id, username)` of the current request's user. Must call `removeCurrentUser()` in `afterCompletion` (handled by `TokenRefreshInterceptor`).
- **`Result<T>`** — standard API response wrapper: `code` (1=success, 0=failure), `msg`, `data`. All controller/service methods return `Result`.
- **Auth flow**: register (SMS code via Redis) → password-login (returns UUID token) → subsequent requests include `Authorization: Bearer {token}` header.
- **Password hashing**: MD5 via `DigestUtils.md5DigestAsHex` (legacy — no salt).
- **MyBatis mappers** use annotation-based SQL (`@Select`, `@Insert`, `@Update`) — no XML mapper files despite `mapper-locations` config.
