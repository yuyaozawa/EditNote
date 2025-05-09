package com.oza.editnote.di

import android.os.Build
import com.oza.editnote.data.api.PageApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * installDebug の前に必ず adbReverse タスクが走ることを前提に、
     * AVD・Genymotionエミュレータはそれぞれのホストループバック
     * (10.0.2.2／10.0.3.2)、物理実機は localhost:3000 を返す。
     */
    @Provides
    @Singleton
    fun provideBaseUrl(): String = when {
        // Android 標準エミュレータ (AVD)
        Build.FINGERPRINT.startsWith("generic") ||
                Build.MODEL.contains("Emulator") ||
                Build.MANUFACTURER.contains("Google") ->
            "http://10.0.2.2:3000"

        // Genymotion エミュレータ
        Build.MANUFACTURER.contains("Genymotion") ->
            "http://10.0.3.2:3000"

        // 物理デバイス（adb reverse 併用想定）
        else ->
            "http://localhost:3000"
    }

    /**
     * [provideBaseUrl] で得た URL を使って
     * - MoshiConverterFactory: JSON ↔ Kotlin オブジェクト変換
     * を組み込んだ Singleton Retrofit インスタンスを提供
     */
    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Retrofit.create() による動的実装を通じて
     * /content エンドポイントを呼び出す [PageApi] を提供
     */
    @Provides
    @Singleton
    fun providePageApi(retrofit: Retrofit): PageApi =
        retrofit.create(PageApi::class.java)
}