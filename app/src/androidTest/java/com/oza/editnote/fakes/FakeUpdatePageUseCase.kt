package com.oza.editnote.fakes

import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.usecase.IUpdatePageUseCase

class FakeUpdatePageUseCase : IUpdatePageUseCase {
    override suspend fun invoke(page: Page): Result<Unit> {
        return Result.success(Unit)
    }
}