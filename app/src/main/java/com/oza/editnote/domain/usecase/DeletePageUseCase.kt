package com.oza.editnote.domain.usecase

import com.oza.editnote.domain.repository.PageRepository

/**
 * ページを削除するユースケース。
 *
 * @param repository ページ削除ロジックを持つリポジトリ
 */
class DeletePageUseCase(
    private val repository: PageRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deletePage(id)
    }
}