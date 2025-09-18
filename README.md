# GitHub Copilot 文件與實驗室指南

> 本專案以透過 GitHub Copilot 實現的進階使用指南、最佳實務為目標：
> 1. 以 [Github Spec Kit](https://github.com/github/spec-kit) (目前以整合教學內容為主，後續會陸續釋出實作細節與應用)
> 2. 以 Tiny URL 系統設計為例，內部自行設計以 **Spec driven** 的實戰框架做為核心實現
>
> 展示如何在企業級專案中有效運用 GitHub Copilot 進行規格驅動開發（Spec-Driven Development, SDD）。

## 目錄

- [GitHub Copilot 文件與實驗室指南](#github-copilot-文件與實驗室指南)
  - [目錄](#目錄)
  - [第一部分：Github Spec Kit（規格驅動開發框架）](#第一部分github-spec-kit規格驅動開發框架)
    - [1.1 框架介紹](#11-框架介紹)
    - [1.2 核心流程與用法](#12-核心流程與用法)
    - [1.3 快速開始](#13-快速開始)
    - [1.4 核心資源](#14-核心資源)
  - [第二部分：TinyURL Lab（自行設計 + 實戰範例專案）](#第二部分tinyurl-lab自行設計--實戰範例專案)
    - [2.1 專案介紹](#21-專案介紹)
    - [2.2 Copilot 驅動開發流程](#22-copilot-驅動開發流程)
    - [2.3 環境設定與實作](#23-環境設定與實作)
    - [2.4 核心資源](#24-核心資源)
  - [附錄：開發規範與指導原則](#附錄開發規範與指導原則)
    - [A.1 技術棧規範](#a1-技術棧規範)
    - [A.2 Spring Boot 框架規範](#a2-spring-boot-框架規範)
    - [A.3 文件寫作原則](#a3-文件寫作原則)
    - [A.4 Git 提交與 PR 規範](#a4-git-提交與-pr-規範)
  - [貢獻指南](#貢獻指南)
  - [授權](#授權)

---

## 第一部分：Github Spec Kit（規格驅動開發框架）

Spec Kit 是一個由 GitHub 官方推出的創新規格驅動開發（Spec-Driven Development, SDD）框架。它將傳統的靜態規格文件轉化為可執行的開發藍圖，讓規格成為驅動開發流程的核心。

### 1.1 框架介紹

- **設計哲學**：規格驅動開發（SDD）的核心理念是**讓規格本身變得可執行**。透過結構化的方式定義需求、計畫與任務，Copilot Agent 能夠理解並依循規格完成開發，確保最終產出與初始設計高度一致。
- **企業治理**：內建的 `Constitution`（憲法）機制提供了一套專案治理框架，用於定義開發規範、技術選型與品質標準，確保團隊決策的一致性。

### 1.2 核心流程與用法

Spec Kit 提供標準化的四階段開發流程，引導開發者從需求到實作的每一步：

1.  **`/specify`**：定義專案的頂層目標、使用者故事與功能規格。此階段專注於「做什麼」。
2.  **`/plan`**：根據規格制定技術方案、系統架構與資料模型。此階段專注於「如何做」。
3.  **`/tasks`**：將技術計畫拆解為具體、可執行的開發任務清單。
4.  **`/implement`**：Copilot Agent 根據任務清單，依序完成程式碼實作、測試與驗證。

### 1.3 快速開始

- **安裝與設定**：提供完整的環境設定指南與 Copilot Agent 整合教學。
- **專案架構**：框架預設採用 Clean Architecture，提供標準化的專案目錄結構，適合企業級應用開發。
- **品質控制**：包含 CI/CD 流程範本與程式碼品質檢查工具，確保專案合規性。

### 1.4 核心資源

- **官方文件**：[[Github] spec-kit](https://github.com/github/spec-kit)
- **學習參考整理**：[res/spec-kit/README.md](res/spec-kit/README.md)
- **實戰範例（Podcast）**：[res/spec-kit/case_podcase.md](res/spec-kit/case_podcase.md)

---

## 第二部分：TinyURL Lab（自行設計 + 實戰範例專案）

TinyURL Lab 是一個從零到一的完整實戰專案，旨在展示如何運用 GitHub Copilot 進行企業級後端服務開發。此專案不僅是技術演練，更是一套結合 **Copilot Prompts** 與**開發規範**的系統化方法論。

### 2.1 專案介紹

- **專案背景**：實作一個功能完整的短網址服務（TinyURL），包含 URL 縮短、重導向、自訂網址與快取機制。
- **學習目標**：
  - 學習使用 Copilot 進行需求分析與任務拆解。
  - 掌握在 Clean Architecture 下的測試驅動開發（TDD）。
  - 體驗 Copilot Agent 驅動的自動化開發流程。
- **技術棧**：Java 17, Spring Boot 3.x, PostgreSQL, Redis, Docker。

### 2.2 Copilot 驅動開發流程

本專案的核心是展示如何透過自訂的 Copilot Prompts 工具集，將開發流程自動化：

1.  **需求分析 (`/list-requirements`)**：使用此指令分析[需求規格文件](docs/specs/tinyurl-requirements.md)，自動生成結構化的功能清單。
2.  **任務拆解 (`/list-tasks`)**：將功能清單拆解為自定義多個標準化的開發任務，涵蓋從環境建置到功能實作的完整過程。
3.  **選擇性實作 (`##`)**：開發者可透過 `##` 語法選擇特定任務，讓 Copilot Agent 專注於單一目標的實作與驗證。

### 2.3 環境設定與實作

- **環境需求**：專案所需的開發工具與版本，包含 Docker, JDK, 與 IDE 設定。
- **初始化指南**：提供詳細的步驟，引導使用者完成專案初始化與資料庫設定。
- **實作與驗證**：遵循 TDD 原則，依序實作各項任務，並包含完整的單元測試與整合測試。

### 2.4 核心資源

- **實戰指南**：[res/tinyurl-lab/README.md](res/tinyurl-lab/README.md)
- **需求規格**：[docs/specs/tinyurl-requirements.md](docs/specs/tinyurl-requirements.md)
- **Copilot Prompts 工具集**：
  - **需求分析工具**：[.github/prompts/list-requirements.prompt.md](.github/prompts/list-requirements.prompt.md)
  - **任務拆解工具**：[.github/prompts/list-tasks.prompt.md](.github/prompts/list-tasks.prompt.md)

---

## 附錄：開發規範與指導原則

本專案採用嚴謹的開發規範，以確保程式碼品質與專案一致性。所有開發活動都必須導入 `.github/instructions/` 下的規範檔案至 Copilot Agent。

### A.1 技術棧規範

- **必選技術棧**：Java 17 LTS + Spring Boot 3.x + PostgreSQL + Redis + Docker
- **詳細規範**：[.github/instructions/tech-stack.instructions.md](.github/instructions/tech-stack.instructions.md)

### A.2 Spring Boot 框架規範

- **架構模式**：Clean Architecture（分層：domain / application / adapters / infrastructure）
- **詳細規範**：[.github/instructions/springboot-spec.instructions.md](.github/instructions/springboot-spec.instructions.md)

### A.3 文件寫作原則

- **核心理念**：重點說明「為什麼」而非「是什麼」，避免冗長敘述，確保邏輯清晰。
- **詳細規範**：[.github/instructions/document.instructions.md](.github/instructions/document.instructions.md)

### A.4 Git 提交與 PR 規範

- **提交格式**：Conventional Commits（繁體中文）
- **詳細規範**：[.github/instructions/git.instructions.md](.github/instructions/git.instructions.md)

---

## 貢獻指南

歡迎提交 Issue 或 Pull Request 來改善本專案。請確保：

1.  遵循專案的開發規範與程式碼風格。
2.  提交前通過所有測試與程式碼檢查。
3.  使用繁體中文撰寫提交訊息與文件。
4.  更新相關文件以反映程式碼變更。

## 授權

本專案採用 MIT 授權條款。
