package com.oza.editnote.domain.usecase

import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.repository.PageRepository

interface ICreatePageUseCase {
    suspend operator fun invoke(page: Page): Page
}
/**
 * 新しいページを作成するユースケース。
 *
 * @param repository ページ作成ロジックを持つリポジトリ
 *
 */
class CreatePageUseCase(
    private val repository: PageRepository
) : ICreatePageUseCase {
    override suspend fun invoke(page: Page): Page =
        repository.createPage(page)
}