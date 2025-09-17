# Podcast 網站開發演練流程圖 (Mermaid)

## 開發流程圖

```mermaid
graph TD
    subgraph "階段 0：專案初始化"
        A[開發者] -->|"specify init pod-site"| B{Spec Kit CLI}
        B -->|"選擇 AI 代理與腳本"| C[建立專案資料夾]
        C -->|"產生模板檔案"| D[初始化完成]
    end

    subgraph "階段 1：建立原則（Constitution）"
        E[開發者] -->|"提示 AI 填寫憲法"| F{AI 代理 GPT-4.5}
        F -->|"讀取 constitution-template.md"| G[生成 constitution.md 草稿]
        G -->|"定義技術原則"| H[開發者審核與修改]
    end

    subgraph "階段 2：定義規格（Specify）"
        I[開發者] -->|"/specify 描述 Podcast 需求"| J{AI 代理}
        J -->|"create-new-feature.sh"| K[建立 Git 分支 001-i-am-building...]
        J -->|"讀取 spec-template.md"| L[生成 spec.md 規格文件]
        L -->|"使用者故事與功能需求"| M[開發者與 AI 互動澄清]
    end

    subgraph "階段 3：規劃技術（Plan）"
        N[開發者] -->|"/plan use next.js..."| O{AI 代理}
        O -->|"讀取憲法、規格、計畫模板"| P[生成 plan.md 與技術文件]
        P -->|"技術選型與執行流程"| Q[開發者審核計畫]
    end

    subgraph "階段 4：拆解任務（Tasks）"
        R[開發者] -->|"/tasks break this down"| S{AI 代理}
        S -->|"讀取計畫與任務模板"| T[生成 tasks.md 任務清單]
        T -->|"Setup/Test-First/Core Implementation"| U[開發者審核任務]
    end

    subgraph "階段 5：實作與預覽（Implement & Preview）"
        V[開發者] -->|"implement the tasks..."| W{AI 代理 Claude Sonnet 4}
        W -->|"依序執行開發任務"| X[生成網站程式碼]
        X -->|"更新任務狀態"| Y[開發者]
        Y -->|"npm run build & dev"| Z[瀏覽器預覽成果]
    end

    %% 流程串接
    D --> E
    H --> I
    M --> N
    Q --> R
    U --> V
```

## 流程圖解說

這個流程圖展示了 Spec Kit 的核心哲學：**多步驟優化 (multi-step refinement)，而非一次性生成**。

### 階段性開發流程

1. **初始化 (階段 0)**：透過 `specify init` 指令建立包含所有必要模板和腳本的專案環境
2. **建立憲法 (階段 1)**：在開發前與 AI 協作建立專案技術原則，對企業級專案尤其重要
3. **定義規格 (階段 2)**：開發者專注定義「What」與「Why」，AI 轉化為結構化的 `spec.md` 文件，在獨立 Git 分支中進行
4. **規劃技術 (階段 3)**：開發者指定「How」技術棧，AI 在遵循「憲法」前提下生成詳細執行計畫 `plan.md`
5. **拆解任務 (階段 4)**：使用 `/tasks` 指令將計畫分解為可管理的任務清單 `tasks.md`
6. **實作與預覽 (階段 5)**：AI 根據任務清單撰寫程式碼，開發者始終扮演「**人機迴圈 (human-in-the-loop)**」監督角色

## Podcast 時序圖

```mermaid
sequenceDiagram
    autonumber

    participant Dev as 開發者
    participant AI as AI 代理
    participant FS as 檔案系統/Git/文件
    participant Browser as 瀏覽器

    %% 階段 0：專案初始化
    Dev->>AI: 執行 specify init pod-site
    AI->>FS: 建立專案資料夾與模板檔案
    FS-->>Dev: 初始化完成

    %% 階段 1：建立原則
    Dev->>AI: 提示填寫憲法
    AI->>FS: 讀取 constitution-template.md
    AI-->>Dev: 生成 constitution.md 草稿
    Dev-->>AI: 審核與修改

    %% 階段 2：定義規格
    Dev->>AI: 輸入 /specify Podcast 需求
    AI->>FS: 建立新 Git 分支與規格文件
    AI-->>Dev: 生成 spec.md（故事/需求/待澄清）
    Dev-->>AI: 互動澄清需求

    %% 階段 3：規劃技術
    Dev->>AI: 輸入 /plan use next.js...
    AI->>FS: 生成 plan.md 與技術文件
    AI-->>Dev: 計畫建議（含 Next.js SSG）
    Dev-->>AI: 審核計畫

    %% 階段 4：拆解任務
    Dev->>AI: 輸入 /tasks break this down
    AI->>FS: 生成 tasks.md 任務清單
    AI-->>Dev: 提供任務拆解
    Dev-->>AI: 審核任務

    %% 階段 5：實作與預覽
    Dev->>AI: implement the tasks...
    AI->>FS: 依序執行任務並產生程式碼
    AI-->>Dev: 完成開發並更新 tasks.md
    Dev->>FS: 執行 npm run build 與 npm run dev
    FS-->>Browser: 提供網站預覽
```