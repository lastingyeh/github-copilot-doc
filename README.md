# GitHub Copilot 文件與實驗室指南

> 本專案提供 GitHub Copilot 的進階使用指南、最佳實務以及完整的實戰演練框架。

## 目錄

- [1. 開發規範與指導原則](#1-開發規範與指導原則)

  - [1.1 技術棧規範](#11-技術棧規範)
  - [1.2 Spring Boot 框架規範](#12-spring-boot-框架規範)
  - [1.3 文件寫作原則](#13-文件寫作原則)
  - [1.4 Git 提交與 PR 規範](#14-git-提交與-pr-規範)

- [2. Spec Kit（規格驅動開發框架）](#2-spec-kit規格驅動開發框架)

  - [2.1 概述與設計哲學](#21-概述與設計哲學)
  - [2.2 多步驟優化流程：/specify → /plan → /tasks → /implement](#22-多步驟優化流程specify--plan--tasks--implement)
  - [2.3 Constitution（憲法）與企業治理](#23-constitution憲法與企業治理)
  - [2.4 架構與專案目錄](#24-架構與專案目錄)
  - [2.5 安裝與快速開始](#25-安裝與快速開始)
  - [2.6 品質控制與合規（CI 範本）](#26-品質控制與合規ci-範本)
  - [2.7 最佳實務與常見問題](#27-最佳實務與常見問題)

- [3. TinyURL Lab（實戰專案）](#3-tinyurl-lab實戰專案)

  - [3.1 專案背景與學習目標](#31-專案背景與學習目標)
  - [3.2 環境需求與初始化](#32-環境需求與初始化)
  - [3.3 Instructions/Prompts 設定](#33-instructionsprompts-設定)
  - [3.4 需求分析：/list-requirements](#34-需求分析list-requirements)
  - [3.5 任務拆解：/list-tasks + 章節選擇##](#35-任務拆解list-tasks--章節選擇)
  - [3.6 實作與驗證（含測試與迭代）](#36-實作與驗證含測試與迭代)
  - [3.7 進階功能與部署路線](#37-進階功能與部署路線)
  - [3.8 常見問題與最佳實務](#38-常見問題與最佳實務)

- [4. 核心工具與資源](#4-核心工具與資源)
  - [4.1 Copilot Prompts 工具集](#41-copilot-prompts-工具集)
  - [4.2 開發環境配置](#42-開發環境配置)
  - [4.3 參考文件與範例](#43-參考文件與範例)

---

## 1. 開發規範與指導原則

本專案採用嚴謹的開發規範，確保程式碼品質與專案一致性。所有開發活動都必須遵循以下核心原則：

### 1.1 技術棧規範

**必選技術棧**：Java 17 LTS + Spring Boot 3.x + PostgreSQL + Redis + Docker

**詳細規範**：[.github/instructions/tech-stack.instructions.md](.github/instructions/tech-stack.instructions.md)

### 1.2 Spring Boot 框架規範

**架構模式**：Clean Architecture（分層：domain / application / adapters / infrastructure）

**詳細規範**：[.github/instructions/springboot-spec.instructions.md](.github/instructions/springboot-spec.instructions.md)

### 1.3 文件寫作原則

**核心理念**：重點說明「為什麼」而非「是什麼」，避免冗長敘述，確保邏輯清晰

**詳細規範**：[.github/instructions/document.instructions.md](.github/instructions/document.instructions.md)

### 1.4 Git 提交與 PR 規範

**提交格式**：Conventional Commits（繁體中文）

**詳細規範**：[.github/instructions/git.instructions.md](.github/instructions/git.instructions.md)

---

## 2. Spec Kit（規格驅動開發框架）

Spec Kit 是一個創新的規格驅動開發框架，透過將規格文件轉化為可執行的藍圖，革命性地改變傳統軟體開發流程。

### 2.1 概述與設計哲學

規格驅動開發 (Spec-Driven Development, SDD) 的核心理念在於**讓規格本身變成可執行的**，將規格從靜態參考文件提升為開發流程的核心驅動力。

**完整指南**：[res/spec-kit/README.md](res/spec-kit/README.md)

### 2.2 多步驟優化流程：/specify → /plan → /tasks → /implement

Spec Kit 提供標準化的開發流程，透過四個核心階段確保專案的品質與一致性：

1. **specify**：定義專案規格與需求
2. **plan**：制定技術計劃與架構設計
3. **tasks**：拆解為具體開發任務
4. **implement**：依序實作與驗證

**實戰範例**：[res/spec-kit/case_podcase.md](res/spec-kit/case_podcase.md)

### 2.3 Constitution（憲法）與企業治理

Constitution 機制提供專案治理框架，確保開發決策的一致性與品質控制。

### 2.4 架構與專案目錄

基於 Clean Architecture 的標準化專案結構，支援企業級應用開發。

### 2.5 安裝與快速開始

提供完整的環境設定與快速開始指南。

### 2.6 品質控制與合規（CI 範本）

包含 CI/CD 流程範本與程式碼品質檢查工具。

### 2.7 最佳實務與常見問題

整理開發過程中的最佳實務與常見問題解決方案。

---

## 3. TinyURL Lab（實戰專案）

TinyURL Lab 是一個完整的實戰專案，展示如何使用 GitHub Copilot 進行企業級應用開發。

### 3.1 專案背景與學習目標

本專案實作一個短網址服務，包含核心的 URL 縮短與重導向功能，採用現代化的微服務架構。

**需求規格**：[docs/specs/tinyurl-requirements.md](docs/specs/tinyurl-requirements.md)

### 3.2 環境需求與初始化

**開發環境**：Java 17 + Spring Boot 3.x + PostgreSQL + Redis + Docker

**完整指南**：[res/tinyurl-lab.md/README.md](res/tinyurl-lab/README.md)

### 3.3 Instructions/Prompts 設定

本專案使用專門設計的 Copilot 指令集，包含需求分析與任務拆解工具：

- **需求分析工具**：[.github/prompts/list-requirements.prompt.md](.github/prompts/list-requirements.prompt.md)
- **任務拆解工具**：[.github/prompts/list-tasks.prompt.md](.github/prompts/list-tasks.prompt.md)

### 3.4 需求分析：/list-requirements

使用 `/list-requirements` 指令分析需求文件，產生結構化的功能需求清單。

### 3.5 任務拆解：/list-tasks + 章節選擇##

透過 `/list-tasks` 指令將專案拆解為 12 個開發任務，並使用 `##` 選擇特定任務進行實作。

### 3.6 實作與驗證（含測試與迭代）

依序實作各項任務，包含單元測試、整合測試與功能驗證。

### 3.7 進階功能與部署路線

擴展功能包含快取策略、監控指標、容器化部署等企業級需求。

### 3.8 常見問題與最佳實務

整理開發過程中的問題解決方案與最佳實務。

---

## 4. 核心工具與資源

### 4.1 Copilot Prompts 工具集

本專案提供一套完整的 Copilot Prompts 工具，用於提升開發效率：

- **結構化文件生成器**：[.github/prompts/structured-document.prompt.md](.github/prompts/structured-document.prompt.md)
- **需求分析工具**：[.github/prompts/list-requirements.prompt.md](.github/prompts/list-requirements.prompt.md)
- **任務拆解工具**：[.github/prompts/list-tasks.prompt.md](.github/prompts/list-tasks.prompt.md)

### 4.2 開發環境配置

**必要工具**：

- GitHub Copilot（建議使用 Agent 模式 + Claude Sonnet 4）
- Docker & Docker Compose
- VS Code 或 IntelliJ IDEA

### 4.3 參考文件與範例

**核心資源**：

- [Spec Kit 完整指南](res/spec-kit/README.md)
- [TinyURL Lab 實戰指南](res/tinyurl-lab/README.md)
- [Podcast 開發流程範例](res/spec-kit/case_podcase.md)

**開發規範**：

- [技術棧規範](.github/instructions/tech-stack.instructions.md)
- [Spring Boot 框架規範](.github/instructions/springboot-spec.instructions.md)
- [文件寫作原則](.github/instructions/document.instructions.md)
- [Git 提交與 PR 規範](.github/instructions/git.instructions.md)

---

## 開始使用

1. **閱讀開發規範**：熟悉專案的技術標準與程式碼風格
2. **設定 Copilot 環境**：啟用 Agent 模式並選擇 Claude Sonnet 4 模型
3. **導入 Instructions**：將 `.github/instructions/` 下的規範檔案加入 Copilot 設定
4. **選擇學習路徑**：
   - **理論學習**：從 [Spec Kit 指南](res/spec-kit/README.md) 開始
   - **實戰演練**：直接進入 [TinyURL Lab](res/tinyurl-lab/README.md)

## 貢獻指南

歡迎提交 Issue 或 Pull Request 來改善本專案。請確保：

1. 遵循專案的開發規範與程式碼風格
2. 提交前通過所有測試與程式碼檢查
3. 使用繁體中文撰寫提交訊息與文件
4. 更新相關文件以反映程式碼變更

## 授權

本專案採用 MIT 授權條款。
