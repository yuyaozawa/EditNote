package com.oza.editnote.di

import com.oza.editnote.domain.usecase.ICreatePageUseCase
import com.oza.editnote.domain.usecase.IDeletePageUseCase
import com.oza.editnote.domain.usecase.IGetPagesUseCase
import com.oza.editnote.domain.usecase.IUpdatePageUseCase
import com.oza.editnote.fakes.FakeCreatePageUseCase
import com.oza.editnote.fakes.FakeDeletePageUseCase
import com.oza.editnote.fakes.FakeGetAllPagesUseCase
import com.oza.editnote.fakes.FakeUpdatePageUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces   = [UseCaseModule::class]
)
abstract class FakeUseCaseModule {

    @Binds
    @Singleton
    abstract fun bindGetPagesUseCase(
        impl: FakeGetAllPagesUseCase
    ): IGetPagesUseCase

    @Binds
    @Singleton
    abstract fun bindCreatePageUseCase(
        impl: FakeCreatePageUseCase
    ): ICreatePageUseCase

    @Binds
    @Singleton
    abstract fun bindDeletePageUseCase(
        impl: FakeDeletePageUseCase
    ): IDeletePageUseCase

    @Binds
    @Singleton
    abstract fun bindUpdatePageUseCase(
        impl: FakeUpdatePageUseCase
    ): IUpdatePageUseCase
}