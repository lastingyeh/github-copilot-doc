# TinyURL 開發任務清單

## 專案簡介

本專案採用 Clean Architecture 架構原則，實作一個完整的 TinyURL 短網址服務。開發流程遵循「環境建置 → 專案初始化 → 核心功能開發」的順序，確保每個階段都有充分的測試覆蓋與文件支援。

整個專案採用 Spring Boot 3.x + PostgreSQL + Redis 的技術棧，並使用 Docker 容器化部署，支援高併發的短網址生成與重導向服務。

## 開發任務概覽

| 編號 | 任務類型     | 任務名稱               | 檔案名稱                                                       | 任務狀態 | 任務描述                                                |
| ---- | ------------ | ---------------------- | -------------------------------------------------------------- | -------- | ------------------------------------------------------- |
| 01   | 🏗️ 環境建置   | Docker 環境設定        | [01-setup-docker.md](./01-setup-docker.md)                     | ❌ 未完成 | 建立 Docker Compose 檔案，包含 PostgreSQL、Redis 等服務 |
| 02   | 🌱 專案初始化 | Spring Boot 專案初始化 | [02-init-project.md](./02-init-project.md)                     | ❌ 未完成 | 建立基本專案結構、pom.xml 配置、Clean Architecture 目錄 |
| 03   | 🗄️ 資料庫設計 | 資料庫設計與遷移       | [03-database-design.md](./03-database-design.md)               | ❌ 未完成 | 設計 URL 映射表結構，建立 Liquibase changelog           |
| 04   | 🏛️ 架構設計   | 領域模型設計           | [04-domain-model.md](./04-domain-model.md)                     | ❌ 未完成 | 實作 URL 領域模型、Repository 介面與 Domain Service     |
| 05   | 🎯 核心功能   | 短網址生成服務         | [05-url-shortening.md](./05-url-shortening.md)                 | ❌ 未完成 | 實作短網址生成邏輯、Use Case 與單元測試                 |
| 06   | 🎯 核心功能   | 重導向服務實作         | [06-url-redirection.md](./06-url-redirection.md)               | ❌ 未完成 | 實作 URL 重導向邏輯、Use Case 與單元測試                |
| 07   | 🗄️ 資料庫設計 | 資料存取層實作         | [07-persistence-layer.md](./07-persistence-layer.md)           | ❌ 未完成 | 實作 JPA Entity、Repository 實作與資料映射              |
| 08   | 🚀 效能優化   | Redis 快取整合         | [08-redis-cache.md](./08-redis-cache.md)                       | ❌ 未完成 | 整合 Redis 快取機制，提升查詢效能                       |
| 09   | 🌐 API 設計   | REST API 控制器        | [09-rest-controllers.md](./09-rest-controllers.md)             | ❌ 未完成 | 實作 Web 控制器、DTO 與 OpenAPI 文件註解                |
| 10   | ⚠️ 例外處理   | 全域例外處理           | [10-exception-handling.md](./10-exception-handling.md)         | ❌ 未完成 | 實作統一例外處理機制與錯誤回應格式                      |
| 11   | 📊 監控與日誌 | 監控與文件整合         | [11-monitoring-docs.md](./11-monitoring-docs.md)               | ❌ 未完成 | 配置 Actuator、SpringDoc、README 文件完善               |
| 12   | 🧩 其他       | 整合測試與部署         | [12-integration-deployment.md](./12-integration-deployment.md) | ❌ 未完成 | 建立整合測試環境與容器化部署配置                        |

## 開發順序說明

### 第一階段：基礎環境 (任務 01-03)
建立開發所需的基礎環境，包含容器化服務、專案結構與資料庫設計。這個階段確保後續開發有穩定的基礎設施支援。

### 第二階段：核心架構 (任務 04-06)
實作核心業務邏輯與領域模型，包含短網址生成與重導向的核心演算法。這個階段專注於業務規則的正確實作。

### 第三階段：技術整合 (任務 07-09)
整合資料存取、快取機制與 Web 介面，將核心業務邏輯與技術基礎設施串接。這個階段確保系統的效能與可用性。

### 第四階段：品質保證 (任務 10-12)
完善錯誤處理、監控機制與部署配置，確保系統達到生產環境的品質要求。這個階段專注於系統的穩定性與維運性。

## 技術規格摘要

- **語言版本**: Java 17 LTS
- **框架**: Spring Boot 3.x
- **建置工具**: Maven + Maven Wrapper
- **資料庫**: PostgreSQL (主要) + Redis (快取)
- **架構模式**: Clean Architecture
- **容器化**: Docker + Docker Compose
- **測試策略**: JUnit 5 + Mockito + Testcontainers
- **API 文件**: SpringDoc OpenAPI
- **監控**: Spring Boot Actuator + Micrometer

## 品質標準

每個任務完成時需要確保：
- ✅ 功能實作正確且符合需求
- ✅ 單元測試覆蓋率達標 (正向+反向測試)
- ✅ 程式碼符合 Google Java Style
- ✅ 包含完整的 JavaDoc 文件
- ✅ OpenAPI 註解完整 (API 相關任務)
- ✅ 支援環境變數配置
- ✅ 錯誤處理機制完善