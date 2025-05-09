package com.oza.editnote.domain.usecase

import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.repository.PageRepository

interface IGetPagesUseCase {
    suspend operator fun invoke(): List<Page>
}
/**
 * ページ一覧取得用のユースケース
 *
 * @param repository ページデータを取得するためのリポジトリ
 */
class GetPagesUseCase(
    private val repository: PageRepository
) : IGetPagesUseCase {
    override suspend fun invoke(): List<Page> =
        repository.getPages()
}