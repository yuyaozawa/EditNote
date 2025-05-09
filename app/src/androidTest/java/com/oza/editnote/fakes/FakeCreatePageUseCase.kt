package com.oza.editnote.fakes

import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.usecase.ICreatePageUseCase

class FakeCreatePageUseCase : ICreatePageUseCase {
    override suspend fun invoke(page: Page): Page =
        page.copy(id = 1)
}