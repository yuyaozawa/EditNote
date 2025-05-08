package com.oza.editnote.di

import com.oza.editnote.data.api.PageApi
import com.oza.editnote.data.repository.PageRepositoryImpl
import com.oza.editnote.domain.repository.PageRepository
import com.oza.editnote.domain.usecase.CreatePageUseCase
import com.oza.editnote.domain.usecase.DeletePageUseCase
import com.oza.editnote.domain.usecase.GetPagesUseCase
import com.oza.editnote.domain.usecase.UpdatePageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt DI モジュール（シングルトンコンポーネント内）
 * - PageApi → PageRepositoryImpl を提供
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providePageRepository(api: PageApi): PageRepository {
        return PageRepositoryImpl(api)
    }

    @Provides
    fun provideGetPagesUseCase(repository: PageRepository): GetPagesUseCase {
        return GetPagesUseCase(repository)
    }

    @Provides
    fun provideCreatePageUseCase(repository: PageRepository): CreatePageUseCase =
        CreatePageUseCase(repository)

    @Provides
    fun provideUpdatePageUseCase(repository: PageRepository): UpdatePageUseCase =
        UpdatePageUseCase(repository)

    @Provides
    fun provideDeletePageUseCase(repository: PageRepository): DeletePageUseCase =
        DeletePageUseCase(repository)
}