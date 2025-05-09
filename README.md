# EditNote

**Android向けシンプルノートアプリ**

🚀 概要
EditNote は **Jetpack Compose × MVVM × Clean Architecture** を採用したノート管理アプリです。

* ドロワー形式のノート一覧
* タイトル入力バリデーション：1～50文字
* 本文入力バリデーション：10～2000文字
* Snackbar による操作結果通知
* Hilt（DI）／Retrofit＋Moshi＋Coroutines＋Flow
* 単体テスト（ViewModel）＋Compose UI Test
* モバイル画面最適化：デバイス幅に合わせたレイアウト調整


🛠 環境構築

1. リポジトリをクローン

   ```bash
   git clone https://github.com/<ユーザ名>/EditNote.git
   cd EditNote
   ```
2. バックエンド起動（localhost:3000）

   ```bash
   cd recruit-frontend
   npm install
   npm run migration:run
   npm run start
   ```
3. Android Studio で開く、Gradle を同期
4. 実機／エミュレータでデバッグ

   * `./gradlew installDebug` 実行前に自動で `adb reverse tcp:3000 tcp:3000` 設定
   * 手動が必要なら:

     ```bash
     adb reverse tcp:3000 tcp:3000
     ```
   * Android Studio の **Run ▶︎** をクリック

📁 プロジェクト構成

```
app/
├── data/           # PageApi, PageDto, PageRepositoryImpl など
├── domain/         # Page モデル, UseCase インタフェース/実装
├── di/             # Hilt の AppModule, UseCaseModule, NetworkModule
├── presentation/   # PageListScreen, ViewModel, UiState, 共通コンポーネント, Theme
└── test/           # PageListViewModelTest, PageListScreenTest など

```


🎨 仕様上の判断について

もともとは「編集に関するボタンが2つ」存在する設計でしたが、実機 Android デバイスの横幅制約を考慮し、以下のように設計を変更しました：

1. **Edit ボタンは1つに統合**
2. **1つのボタンで状態をトグル（編集開始 ⇄ 編集完了／保存）**


✅ テスト

* **ユニットテスト**

  ```bash
  ./gradlew testDebugUnitTest
  ```

  * PageListViewModelTest：状態遷移／エラーハンドリング検証

* **UI テスト**

  ```bash
  ./gradlew connectedAndroidTest
  ```

  * PageListScreenTest：Compose 画面表示検証
  * PageListScreenFakeTest：固定ステート検証

* **Hilt テスト**

  * `@HiltAndroidTest` で DI 置き換え後のユースケース → ViewModel を検証


---
