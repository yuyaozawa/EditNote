package com.oza.editnote.domain.repository

import com.oza.editnote.domain.model.Page

interface PageRepository {
    suspend fun getPages(): List<Page>
    suspend fun createPage(page: Page): Page
    suspend fun updatePage(page: Page): Page
    suspend fun deletePage(id: Int)
}