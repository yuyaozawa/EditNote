package com.oza.editnote.fakes

import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.usecase.IGetPagesUseCase

class FakeGetAllPagesUseCase : IGetPagesUseCase {
    override suspend fun invoke(): List<Page> {
        return listOf(Page(id = 1, title = "Test Page", body = "Test body"))
    }
}