package com.oza.editnote.domain.usecase

import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.repository.PageRepository

/**
 * ページ更新用のユースケース。
 *
 * @param repository ページデータを更新するためのリポジトリ
 */
class UpdatePageUseCase(
    private val repository: PageRepository
) {
    suspend operator fun invoke(page: Page): Page {
        return repository.updatePage(page)
    }
}