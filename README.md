# 建立專案資料夾
建立空資料夾，並使用 vscode 開啟

# 放入需求文件
1. 建立 `docs/specs` 資料夾
2. 在資料夾內建立需求文件 `tinyurl-requirements.md` <br/>
    ![tinyurl-requirements.png](imgs/tinyurl-requirements.png)
    - 內容參考：[tinyurl-requirements.md](templates/docs/specs/tinyurl-requirements.md)

# 基本設定
## 設定模式與模型
> 💡 如果是寫程式的話，Claude Sonnet 模型目前算是表現比較好的選擇之一

![agent-mode.png](imgs/agent-mode.png)
- 選擇 `Agent` + `Claude Sonnet 4`

## 手動建立指示 (Instructions)
> 💡 可以把團隊的開發規範加入，這樣 AI 每次回覆時都會參考

1. 點選 `設定 > 指示` <br/>
  ![instructions-1.png](imgs/instructions-1.png)  
2. 點選 `新的指示檔案...` <br/>
  ![instructions-2.png](imgs/instructions-2.png)
3. 點選 `.github/instructions` <br/>
  ![instructions-3.png](imgs/instructions-3.png)
4. 輸入檔名(例如：`development-guidelines`) 並按下 enter <br/>
  ![instructions-4.png](imgs/instructions-4.png)
5. 就會在 `.github/instructions` 資料夾下，建立 `development-guidelines.instructions.md` <br/>
  ![instructions-5.png](imgs/instructions-5.png)
6. 調整內容，並儲存 <br/>
   ![instructions-6.png](imgs/instructions-6.png) <br/>
    - 📝 LAB 時．請使用教材提供的 Instructions：
      - [document.instructions.md](.github/instructions/document.instructions.md)
      - [git.instructions.md](.github/instructions/git.instructions.md)
      - [springboot-spec.instructions.md](.github/instructions/springboot-spec.instructions.md)
      - [tech-stack.instructions.md](.github/instructions/tech-stack.instructions.md)

## 自動建立提示檔案 (instructions)
> 💡 讓 AI 讀取整個專案，自動生成 instructions
1. 點選 `設定 > 指示` <br/>
  ![auto-instructions-1.png](imgs/auto-instructions-1.png)
2. 點選 `產生提示...` <br/>
  ![auto-instructions-2.png](imgs/auto-instructions-2.png)
3. AI 會分析整個專案並生成或更新 `.github/copilot-instructions.md` <br/>
  ![auto-instructions-3.png](imgs/auto-instructions-3.png)

## 建立提示檔案 (Prompt)
> 💡 建立常用的 Prompt，就可以透過快捷鍵使用

1. 點選 `設定 > 提示檔案` <br/>
  ![prompt-file-1.png](imgs/prompt-file-1.png)
2. 點選 `新增指示檔案...` <br/>
  ![prompt-file-2.png](imgs/prompt-file-2.png)
3. 點選 `.github/prompts` <br/>
  ![prompt-file-3.png](imgs/prompt-file-3.png)
4. 輸入檔名(例如：`list-tasks`) 並按下 enter <br/>
  ![prompt-file-4.png](imgs/prompt-file-4.png)
5. 就會在 `.github/instructions` 資料夾下，建立 `list-tasks.prompts.md` <br/>
  ![prompt-file-5.png](imgs/prompt-file-5.png)
6. 調整內容，並儲存 <br/>
  ![prompt-file-6.png](imgs/prompt-file-6.png)
    - 📝 LAB 時．請使用教材提供的 Prompt：
      - [list-requirements.prompt.md](.github/prompts/list-requirements.prompt.md)
      - [list-tasks.prompt.md](.github/prompts/list-tasks.prompt.md)

# 產生工作項目
> 💡LAB 先使用 `/list-requirements` 產生需求，再使用 `/list-tasks 拆成 12 項子任務` 產生工作任務 <br/>
> (為了讓 LAB 時，大家的進度一致，所以才多加上 `拆成 12 項子任務` 的指示)

1. 在聊天視窗輸入 `/`，可以看到剛剛建立的 Prompt <br/>
  ![list-tasks-1.png](imgs/list-tasks-1.png)
2. 選擇 `/list-tasks`，並按下 enter 或傳送 <br/>
  ![list-tasks-2.png](imgs/list-tasks-2.png)
3. AI 就會跟據 Prompt 的說明，拆解工作任務並將結果寫入 `docs/tasks` 資料夾 (這步驟工作比較多，需要等比較久) <br/>
  ![list-tasks-3.png](imgs/list-tasks-3.png)


# 開發
> 💡 不建議一次處理太多任務，因為 AI 可能會抓不清重點，導致回覆品質下降 <br/>
>    當任務內容很多時，可以運用 `##` 選擇子章節，分批執行

## 建立 docker compose
1. 點開建立 docker compose 的任務 <br/>
  ![dev-docker-1.png](imgs/dev-docker-1.png)
2. 在聊天視窗輸入 `##`，可以選擇已經開啟的 markdown 檔案中的章節 <br/>
  ![dev-docker-2.png](imgs/dev-docker-2.png)
3. 選擇要執行的章節，並按下 enter 或傳送 <br/>
  ![dev-docker-3.png](imgs/dev-docker-3.png)
4. AI 會根據章節內容，產生 docker compose 的內容，並且將服務啟動 <br/>
  ![dev-docker-4.png](imgs/dev-docker-4.png)

## 建立專案
1. 點開建立專案的任務，選擇章節，並按下 enter 或傳送 <br/>
  ![init-project-1.png](imgs/init-project-1.png)
2. AI 就會建立出 Spring Boot 專案 <br/>
  ![init-project-2.png](imgs/init-project-2.png)
3. 嘗試使用 IDE 啟動服務，或是在聊天視窗輸入 `啟動服務` 並送出，確認程式可以正常運作

## Keep Going...
依照上面的步驟，一步一步將任務完成，就可以完成功能囉～
