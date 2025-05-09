package com.oza.editnote.domain.usecase

import com.oza.editnote.domain.repository.PageRepository


interface IDeletePageUseCase {
    suspend operator fun invoke(id: Int): Result<Unit>
}
/**
 * ページを削除するユースケース。
 *
 * @param repository ページ削除ロジックを持つリポジトリ
 */
class DeletePageUseCase(
    private val repository: PageRepository
) : IDeletePageUseCase {
    override suspend fun invoke(id: Int): Result<Unit> = try {
        // リポジトリの処理を呼び出し
        repository.deletePage(id)
        // 正常終了なら Result.success を返す
        Result.success(Unit)
    } catch (e: Exception) {
        // 例外が起きたら Result.failure で包む
        Result.failure(e)
    }
}