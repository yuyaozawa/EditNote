import java.io.ByteArrayOutputStream
import java.io.File
import org.gradle.api.tasks.Exec

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.oza.editnote"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.oza.editnote"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

// USB 実機／エミュレータどちらでも localhost:3000 を開発マシンに向ける
tasks.register<Exec>("adbReverse") {
    group = "development"
    description = "実機デバッグ用に adb reverse tcp:3000 を自動設定"

    // configuration 時に adb 実行ファイルを解決
    val sdkRoot = System.getenv("ANDROID_SDK_ROOT") ?: System.getenv("ANDROID_HOME")
    val candidates = listOfNotNull(
        sdkRoot?.let { "$it/platform-tools/adb" },
        "adb"  // PATH にあれば
    )
    val adbExe = candidates.firstOrNull { File(it).exists() }
    if (adbExe == null) {
        // adb が見つからないなら最初からタスクを無効化
        enabled = false
        return@register
    }

    // 実機接続がないときはスキップ
    onlyIf {
        val out = ByteArrayOutputStream()
        project.exec {
            commandLine(adbExe, "devices")
            isIgnoreExitValue = true
            standardOutput = out
        }
        out.toString().contains("\tdevice")
    }

    // adb reverse の失敗でビルドを止めない
    isIgnoreExitValue = true

    // 最後にコマンドをセット
    commandLine(adbExe, "reverse", "tcp:3000", "tcp:3000")
}

// installDebug／assembleDebug の前に必ず adbReverse を走らせる
tasks.matching {
    it.name == "installDebug" || it.name == "assembleDebug"
}.configureEach {
    dependsOn("adbReverse")
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(project(":app"))
    androidTestImplementation(project(":app"))
    androidTestImplementation(project(":app"))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Retrofit 本体：HTTP 通信を行うためのライブラリ（APIクライアント）
    implementation(libs.retrofit)
    // Moshi コンバータ：Retrofit で JSON を Kotlin オブジェクトに変換するためのアダプター
    implementation(libs.converter.moshi)
    // Moshi 本体：軽量で高速な JSON パーサー（Gsonの代替）
    implementation(libs.moshi)
    // Moshi Kotlin 拡張：Kotlin特有の構文（data classなど）を扱うための補助ライブラリ
    implementation(libs.moshi.kotlin)
    // Moshi アダプター拡張：日時や列挙型などの変換を便利に行うためのアダプター集
    implementation(libs.moshi.adapters)
    // Jetpack Hilt の Navigation Compose 対応：@HiltViewModel などを Compose Navigation と連携させるため
    implementation(libs.androidx.hilt.navigation.compose)
    // Hilt 本体：依存性注入 (DI) を簡単に導入するためのDaggerラッパー
    implementation(libs.hilt.android)
    // Hilt のコード生成に必要なアノテーションプロセッサ（Dagger2 互換）
    kapt(libs.hilt.android.compiler)
    // AndroidX版Hiltの ViewModel 拡張やナビゲーションの連携処理を生成するコンパイラ
    kapt(libs.androidx.hilt.compiler)

    // --- Unit Test ライブラリ ---
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.junit)
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest           (libs.hilt.compiler)
    // Google Truth をユニットテストで使えるように
    testImplementation(libs.truth)
    // AndroidInstrumentationTest でも Truth を使うなら
    androidTestImplementation(libs.truth)
}