---
applyTo: '**'
---
# 文件與 Git 規範（含提交/PR）

> **語言**：所有文件、提交訊息與 PR 內容一律使用**繁體中文**。

## Git Commit（Conventional Commits）

### 格式

```text
<type>: <subject>

<body>
````
* **type**：
  * `feat` 新功能
  * `fix` 修錯
  * `docs` 文件
  * `style` 格式（不影響行為）
  * `refactor` 重構（不改變商業邏輯）
  * `perf` 效能
  * `test` 測試
  * `chore` 建置/工具/維運
  * `revert` 回復（如：`revert: feat(auth): support OAuth2 login`）
* **subject**：祈使句、簡短、末尾不加句點
* **body**（選填)：條列「變更內容 / 原因 / 影響」

### 範例

```text
fix: 修正員工管理的時間格式轉換

1. 調整 TimeUtils 的時區偏移計算，改為自 timestamp 扣除偏移量。
```

## 分支與 PR 流程

* **分支命名**：`feature/<短描述>`、`fix/<短描述>`、`chore/<短描述>`
* **PR 標題**：與 commit type 對齊（如 `feat: 新增 Orders 建立 API`）
* **PR Checklist（至少）**：

  * [ ] 單元測試通過（含 Positive/Negative）
  * [ ] 有必要的文件更新（README/OpenAPI/變更說明）
  * [ ] 觀測性端點可用（`/actuator/health`、`/actuator/prometheus`）
  * [ ] 無明顯 N+1 / 多餘查詢；必要索引已建立
  * [ ] 錯誤訊息清晰且一致
* **Merge 策略**：建議 **squash & merge**（保持線性歷史）
* **Changelog**：版本釋出時依 Conventional Commits 產生（建議 `CHANGELOG.md`）

## Copilot 回覆格式（文件/PR/提交訊息）

* 以**繁體中文**撰寫，必要時附上**檔名與路徑**
* 對每個決策簡述**為什麼**
* 當我要求「只要骨架」時，仍需產出**可編譯**的最小可行樣板與至少一個通過的測試
