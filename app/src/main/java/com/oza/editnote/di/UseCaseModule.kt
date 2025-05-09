package com.oza.editnote.di

import com.oza.editnote.domain.usecase.CreatePageUseCase
import com.oza.editnote.domain.usecase.DeletePageUseCase
import com.oza.editnote.domain.usecase.GetPagesUseCase
import com.oza.editnote.domain.usecase.ICreatePageUseCase
import com.oza.editnote.domain.usecase.IDeletePageUseCase
import com.oza.editnote.domain.usecase.IGetPagesUseCase
import com.oza.editnote.domain.usecase.IUpdatePageUseCase
import com.oza.editnote.domain.usecase.UpdatePageUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindGetPagesUseCase(
        impl: GetPagesUseCase
    ): IGetPagesUseCase

    @Binds
    abstract fun bindCreatePageUseCase(
        impl: CreatePageUseCase
    ): ICreatePageUseCase

    @Binds
    abstract fun bindDeletePageUseCase(
        impl: DeletePageUseCase
    ): IDeletePageUseCase

    @Binds
    abstract fun bindUpdatePageUseCase(
        impl: UpdatePageUseCase
    ): IUpdatePageUseCase
}