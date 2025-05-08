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
     * installDebug の前に必ず adbReverse タスクで
     * tcp:3000→tcp:3000 が設定されることを前提に、
     * どのデバイス（エミュレータ／実機）でも
     * localhost:3000 だけを返す実装に簡素化
     */
    @Provides
    @Singleton
    fun provideBaseUrl(): String = "http://localhost:3000"

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