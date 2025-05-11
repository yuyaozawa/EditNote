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

**必須ツール（Prerequisites）**  
- **Node.js** 
- **Java JDK** (11 以上)  
- **Android Studio**  
  - Android プロジェクトのインポート／ビルドに必須  
- **Android SDK Platform-Tools**（adb 含む）  

---

1. バックエンド起動　ローカル API: http://localhost:3000
- Nest.js サーバーをローカルで起動
   ```bash
   git clone https://github.com/ncdcdev/recruit-frontend.git
   cd recruit-frontend
   npm install
   npm run migration:run
   npm run start
   ```

2. Android クライアント（EditNote）の準備

   ```bash
   git clone https://github.com/yuyaozawa/EditNote.git
   cd EditNote
   ```
     - Android Studio を起動

     - File > Open で EditNote フォルダを選択

     - File > Sync Project with Gradle Files を実行

3. Homebrew のインストール（macOS 用パッケージマネージャ）
- ターミナルで以下を実行し、Homebrew をインストールします。

   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```
- インストール後、以下をコピー＆ペーストしてパスを通します：

   ```bash
   echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
   eval "$(/opt/homebrew/bin/brew shellenv)"
   ```

4. Java 17 の導入
- Android Gradle Plugin が Java 17 を必要とするため、以下を実行します：

    ```bash
    brew install openjdk@17
    sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk \
            /Library/Java/JavaVirtualMachines/openjdk-17.jdk
    echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
    echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
    source ~/.zshrc
    ```

   
5. Android SDK Platform-Tools の導入
- macOS + Homebrew
     ```bash
     brew install --cask android-platform-tools
    ```

6. adb コマンド PATH 設定 (macOS + zsh)

 ```bash
  # ~/.zshrc に追記
  echo 'export ANDROID_SDK_ROOT=$HOME/Library/Android/sdk' >> ~/.zshrc
  echo 'export PATH="$ANDROID_SDK_ROOT/platform-tools:$PATH"' >> ~/.zshrc
  # 設定を反映
  source ~/.zshrc
  ```

7. エミュレータでの実行
- Android Studio の ▶︎Run ボタンを押すだけ
- (内部で `./gradlew installDebug` → `adbReverse` タスク実行 → reverse 設定されます)

8. 実機での実行
- USB ケーブル で実機を開発マシンに接続

- 実機の 開発者オプション > USBデバッグ を ON

- ターミナル（または PowerShell）で以下をそのままコピー＆ペースト
  ```bash 
  # 1) 接続済みデバイス一覧を確認
  adb devices
  # → List of devices attached
  #    HQ632H00AB    device
  #    emulator-5554 device

  # 2) 実機（シリアル：例 HQ632H00AB）にリバース設定
  adb -s HQ632H00AB reverse tcp:3000 tcp:3000
  # → 戻り値に “3000” と出れば成功

  # 3) アプリをビルド＆インストール
  ./gradlew installDebug

  ```

9.動作確認済みデバイス

- Android 標準エミュレータ (AVD)

- Galaxy S23

- Sony Xperia SO-52C

10.補足

 - Android Studio が必須です。

 - Windows でも同様の手順で動作しますが、ターミナルの代わりに PowerShell／コマンドプロンプトを使用し、./gradlew は gradlew.bat を使ってください。



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
