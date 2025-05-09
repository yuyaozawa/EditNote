package com.oza.editnote.fakes

import com.oza.editnote.domain.usecase.IDeletePageUseCase

class FakeDeletePageUseCase : IDeletePageUseCase {
    override suspend fun invoke(id: Int): Result<Unit> {
        return Result.success(Unit)
    }
}