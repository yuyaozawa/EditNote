# EditNote

# 🚀 概要
* Kotlin×Jetpack Compose で開発した Androidアプリです。
アーキテクチャには MVVM と Clean Architecture を採用しています。

# 🔧 技術スタック
* アーキテクチャ：MVVM + Clean Architecture
* DI：Hilt
* 通信：Retrofit ＋ Moshi ＋ Coroutines ＋ Flow
* テスト：ViewModel の単体テスト ＋ Compose UI Test
* 対応端末：Android API 24 以上、画面幅に応じたレスポンシブデザイン

# 🛠 環境構築

1. リポジトリをクローン

   ```bash
   git clone https://github.com/yuyaozawa/EditNote.git
   cd EditNote
   ```
2. バックエンド起動　ローカル API: http://localhost:3000

　　Nest.js サーバーをローカルで起動
   ```bash
   git clone https://github.com/ncdcdev/recruit-frontend.git
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

# 📁 プロジェクト構成

```
app/
├── data/           # API通信・DTO・リポジトリ実装
├── domain/         # ドメインモデル・ビジネスロジック（UseCase定義）
├── di/             # 依存性注入設定（Hiltモジュール）
├── presentation/   # Compose UI／ViewModel／共通コンポーネント／テーマ
└── test/           # ユニットテスト／UIテスト

```


# 🎨 仕様上の判断について

* 資料では「編集に関するボタンが2つ」存在する設計でしたが、実機 Android デバイスの横幅制約を考慮し、Edit ボタンは1つに統合

# 💡 工夫したポイント

- **不要な再描画の抑制**  
  - 文字数チェックの結果をキャッシュし、毎回再計算されないよう最適化  
  - 取得済みのページ一覧をメモ化し、不要な再取得を防止
    
- **リアルタイム入力チェック**  
  - タイトル（1～50文字）・本文（10～2000文字）の文字数を即座に判定し、要件を満たさない場合は入力欄をエラー表示かつ保存操作を制限

- **メニュー内リストの自動スクロール**  
  - サイドメニュー（ドロワー）を開いたとき、または項目を切り替えたときに自動でスクロールして、選択中のページが常に見えるように調整

- **操作結果の即時通知**  
   - 成功／失敗のいずれもスナックバーで短時間表示し、ユーザーへ明確なフィードバックを提供

- **画面サイズに応じたレイアウト**  
  - 端末の横幅を参照し、ドロワー幅や余白を動的に調整。スマホからタブレットまで自然な見た目を実現

- **テストしやすい構成**  
  - ビジネスロジックをユースケースとして分離。ViewModel 単体テストや UI テストが簡潔に記述可能な設計

- **汎用コンポーネントの再利用**  
  - ボタンやダイアログなどを共通部品として切り出し、画面実装は最小限に。保守性と拡張性を向上
  
# ✅ テスト

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
