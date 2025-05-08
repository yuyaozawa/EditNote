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

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
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
}