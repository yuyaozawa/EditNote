package com.oza.editnote.data.repository

import com.oza.editnote.data.api.PageApi
import com.oza.editnote.data.mapper.toDomain
import com.oza.editnote.data.mapper.toDto
import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.repository.PageRepository

class PageRepositoryImpl(
    private val api: PageApi
) : PageRepository {

    override suspend fun getPages(): List<Page> {
        return api.getPages().map { it.toDomain() }
    }

    override suspend fun createPage(page: Page): Page {
        return api.createPage(page.toDto()).toDomain()
    }

    override suspend fun updatePage(page: Page): Page {
        val id = page.id ?: throw IllegalArgumentException("Page ID is required for update")
        return api.updatePage(id, page.toDto()).toDomain()
    }

    override suspend fun deletePage(id: Int) {
        api.deletePage(id)
    }
}
