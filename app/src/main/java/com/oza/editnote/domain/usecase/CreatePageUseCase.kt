package com.oza.editnote.domain.usecase

import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.repository.PageRepository

/**
 * 新しいページを作成するユースケース。
 *
 * @param repository ページ作成ロジックを持つリポジトリ
 */
class CreatePageUseCase(
    private val repository: PageRepository
) {
    suspend operator fun invoke(page: Page): Page {
        return repository.createPage(page)
    }
}