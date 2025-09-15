# GitHub Copilot 指令：Spring Boot 開發實驗室

## 專案概述
這是一個教學導向的實驗室專案，專注於使用 AI 助手進行結構化的 Spring Boot 開發流程。採用**需求分析 → 任務規劃 → 程式開發**的三階段開發方法論。

## 核心開發流程

### 1. 需求分析階段 (`/list-requirements`)
- 從 `docs/specs/` 中的需求文件出發，產生功能導向的需求清單
- 輸出：`docs/requirements/` 中的功能文件（`01-user-registration.md` 格式）
- 每個功能必須包含：功能描述、驗收標準，使用 Mermaid sequence-diagram 描述流程

### 2. 任務規劃階段 (`/list-tasks 拆成 12 項子任務`)
- 分析 `docs/specs/` + `docs/requirements/` 內容，產生開發任務清單
- 輸出：`docs/tasks/` 中的任務文件（`01-setup-docker.md` 格式）
- **關鍵順序**：環境建置 → 專案初始化 → 核心功能開發
- 每個業務功能必須包含：實作 + 單元測試 + OpenAPI 註解（同步完成，無需獨立任務）

### 3. 程式開發階段 (`##` 選擇任務)
- 依照任務清單順序，使用 `##` 選擇特定任務進行開發
- 遵循既有的 Instructions 規範進行實作

## 架構與技術規範

### Clean Architecture 目錄結構
```
src/main/java/com/example/orders/
├── domain/          # 純領域模型（無框架依賴）
├── application/     # Use Cases 與 Ports
├── adapters/        # 介面層（web, messaging, scheduler）
└── infrastructure/  # 技術細節（persistence, cache, config）
```

### 開發環境模式設定
- **必須使用**：`Agent` + `Claude Sonnet 4` 模式
- 原因：程式開發任務需要 Agent 模式的多步驟推理能力

### Docker 優先的環境建置
- 第一個任務必須是建立 `docker-compose.yml`
- 容器保持運行狀態，支援後續開發與測試
- `README.md` 必須包含 Docker 環境啟動/停止指令

## 特殊約定

### 檔案命名與組織
- Instructions 檔案：`.github/instructions/*.instructions.md`
- Prompts 檔案：`.github/prompts/*.prompt.md`
- 需求文件：`docs/requirements/XX-feature-name.md`（數字編號開頭）
- 任務文件：`docs/tasks/XX-task-name.md`（按開發順序編號）

### 繁體中文輸出要求
- **所有**程式碼註解、提交訊息、PR、文件均使用繁體中文
- commit 格式：`<type>: <subject>` (繁體中文 subject)

### 測試與文件同步完成
- 每個業務功能開發時，同步完成：
  - 功能實作
  - 單元測試（JUnit 5 + Mockito）
  - OpenAPI 註解（`@Tag`, `@Operation`, `@Schema`）
- 避免為測試或文件建立獨立任務

## 專案範例場景
以 TinyURL 服務為範例：
- **短網址生成**：接收長網址，產生唯一短網址並儲存映射關係
- **重導向服務**：根據短網址查找長網址並執行 HTTP 重導向

## 關鍵品質檢查點
1. **架構符合性**：確保 Clean Architecture 分層正確
2. **測試覆蓋**：每個業務功能都有對應測試
3. **API 文件**：SpringDoc 自動產生可存取的 Swagger UI
4. **Docker 就緒**：可透過 docker-compose 完整啟動
5. **繁體中文一致性**：所有輸出內容語言統一