package com.oza.editnote.domain.usecase

import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.repository.PageRepository

interface IUpdatePageUseCase {
    suspend operator fun invoke(page: Page): Result<Unit>
}
/**
 * ページ更新用のユースケース。
 *
 * @param repository ページデータを更新するためのリポジトリ
 */
class UpdatePageUseCase(
    private val repository: PageRepository
) : IUpdatePageUseCase {
    override suspend fun invoke(page: Page): Result<Unit> = try {
        // リポジトリの更新処理を呼び出し（戻り値は無視）
        repository.updatePage(page)
        // 正常終了なら Result.success(Unit)
        Result.success(Unit)
    } catch (e: Exception) {
        // 失敗時は Result.failure(e)
        Result.failure(e)
    }
}