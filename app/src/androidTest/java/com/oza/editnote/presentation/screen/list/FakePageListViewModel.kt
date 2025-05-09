package com.oza.editnote.presentation.screen.list

import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.usecase.ICreatePageUseCase
import com.oza.editnote.domain.usecase.IDeletePageUseCase
import com.oza.editnote.domain.usecase.IGetPagesUseCase
import com.oza.editnote.domain.usecase.IUpdatePageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * テスト用に UI ステートやダイアログフラグを固定し、
 * 本番ロジックを呼ばない Fake の ViewModel。
 */
class FakePageListViewModel(
    getPagesUseCase: IGetPagesUseCase,
    createPageUseCase: ICreatePageUseCase,
    deletePageUseCase: IDeletePageUseCase,
    updatePageUseCase: IUpdatePageUseCase
) : PageListViewModel(
    getPagesUseCase, createPageUseCase, deletePageUseCase, updatePageUseCase
) {
    // テスト用の「固定ページリスト」
    private val _uiState = MutableStateFlow<PageListUiState>(
        PageListUiState.Success(
            pages = listOf(
                Page(id = 1, title = "テストページ", body = "本文テスト")
            )
        )
    )
    /** テスト用 UIState */
    override val uiState: StateFlow<PageListUiState> = _uiState.asStateFlow()

    // 選択中のページIDを「1」で固定
    private val _selectedPageId = MutableStateFlow<Int?>(1)
    override val selectedPageId: StateFlow<Int?> = _selectedPageId.asStateFlow()

    // 編集モード ON
    private val _isEditMode = MutableStateFlow(true)
    override val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    // 削除ダイアログは最初は出さない
    private val _showDeleteDialogFor = MutableStateFlow<Int?>(null)
    override val showDeleteDialogFor: StateFlow<Int?> = _showDeleteDialogFor.asStateFlow()

    // 追加ダイアログは最初は出さない
    private val _showAddDialog = MutableStateFlow(false)
    override val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    // 新規タイトルは常に空文字
    private val _newTitle = MutableStateFlow("")
    override val newTitle: StateFlow<String> = _newTitle.asStateFlow()

    /** Fake: ページ選択をシミュレート */
    override fun selectPage(id: Int) {
        _selectedPageId.value = id
    }

    /** Fake: 削除ダイアログを表示 */
    override fun showDeleteDialog(id: Int) {
        _showDeleteDialogFor.value = id
    }

    /** Fake: 削除ダイアログを閉じる */
    override fun dismissDeleteDialog() {
        _showDeleteDialogFor.value = null
    }

    /** Fake: 追加ダイアログを表示 */
    override fun showAddDialog() {
        _showAddDialog.value = true
    }

    /** Fake: 追加ダイアログを閉じる */
    override fun dismissAddDialog() {
        _showAddDialog.value = false
    }

    /** Fake: 新規タイトルをセット */
    override fun updateNewTitle(text: String) {
        _newTitle.value = text
    }

    /** Fake: 編集モードのトグル */
    override fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }

    // loadPages/deletePage/createPage/updatePage などは本物のロジックを動かさないのでそのまま継承
}
