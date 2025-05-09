package com.oza.editnote.presentation.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.usecase.CreatePageUseCase
import com.oza.editnote.domain.usecase.DeletePageUseCase
import com.oza.editnote.domain.usecase.GetPagesUseCase
import com.oza.editnote.domain.usecase.ICreatePageUseCase
import com.oza.editnote.domain.usecase.IDeletePageUseCase
import com.oza.editnote.domain.usecase.IGetPagesUseCase
import com.oza.editnote.domain.usecase.IUpdatePageUseCase
import com.oza.editnote.domain.usecase.UpdatePageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PageListUiState {
    // ローディング中
    object Loading : PageListUiState()
    // 成功時: ページリストを保持
    data class Success(val pages: List<Page>) : PageListUiState()
    // エラー時: メッセージを保持
    data class Error(val message: String) : PageListUiState()
}

@HiltViewModel
open class PageListViewModel @Inject constructor(
    private val getPagesUseCase: IGetPagesUseCase,
    private val createPageUseCase: ICreatePageUseCase,
    private val deletePageUseCase: IDeletePageUseCase,
    private val updatePageUseCase: IUpdatePageUseCase
) : ViewModel() {

    // UIの状態を管理するStateFlow
    private val _uiState = MutableStateFlow<PageListUiState>(PageListUiState.Loading)
    open val uiState: StateFlow<PageListUiState> = _uiState.asStateFlow()

    // 選択中のページID
    private val _selectedPageId = MutableStateFlow<Int?>(null)
    open val selectedPageId: StateFlow<Int?> = _selectedPageId.asStateFlow()

    // 編集モードかどうか
    private val _isEditMode = MutableStateFlow(false)
    open val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    // 削除ダイアログを表示する対象ID
    private val _showDeleteDialogFor = MutableStateFlow<Int?>(null)
    open val showDeleteDialogFor: StateFlow<Int?> = _showDeleteDialogFor.asStateFlow()

    // 追加ダイアログの表示フラグ
    private val _showAddDialog = MutableStateFlow(false)
    open val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    // 新規ページのタイトル
    private val _newTitle = MutableStateFlow("")
    open val newTitle: StateFlow<String> = _newTitle.asStateFlow()

    // 一時的な通知イベント（Snackbar用）
    private val _successEvent = MutableSharedFlow<String?>()
    private val _errorEvent   = MutableSharedFlow<String?>()
    val successEvent: SharedFlow<String?> = _successEvent
    val errorEvent: SharedFlow<String?>   = _errorEvent

    init {
        // 初回ロード
        loadPages()
    }

    /**
     * ページ一覧を取得してUI状態を更新
     */
    fun loadPages() {
        viewModelScope.launch {
            _uiState.value = PageListUiState.Loading
            try {
                val pages = getPagesUseCase()
                _uiState.value = PageListUiState.Success(pages)
                // 未選択なら先頭を自動選択
                if (_selectedPageId.value == null) {
                    _selectedPageId.value = pages.firstOrNull()?.id
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = PageListUiState.Error(e.message ?: "Unknown error")
                _errorEvent.emit("ページ一覧の取得に失敗しました：${e.message}")
            }
        }
    }

    /**
     * ページ削除を実行し、成功・失敗通知を発行
     */
    fun deletePage() {
        val id = _showDeleteDialogFor.value ?: return
        viewModelScope.launch {
            try {
                deletePageUseCase(id)
                _successEvent.emit("ページを削除しました")
                loadPages()
            } catch (e: Exception) {
                _errorEvent.emit("削除に失敗しました：${e.message}")
            } finally {
                _showDeleteDialogFor.value = null
            }
        }
    }

    /**
     * 新規ページを作成し、成功・失敗通知を発行
     */
    fun createPage() {
        val title = _newTitle.value.takeIf { it.isNotBlank() } ?: return
        viewModelScope.launch {
            try {
                val created = createPageUseCase(Page(id = 0, title = title, body = ""))
                _successEvent.emit("ページを追加しました")
                loadPages()
                _selectedPageId.value = created.id
                _showAddDialog.value = false
            } catch (e: Exception) {
                _errorEvent.emit("追加に失敗しました：${e.message}")
            }
        }
    }

    /**
     * ページ更新を実行し、成功・失敗通知を発行
     */
    fun updatePage(page: Page) {
        viewModelScope.launch {
            try {
                updatePageUseCase(page)
                _successEvent.emit("ページを更新しました")
                loadPages()
            } catch (e: Exception) {
                _errorEvent.emit("更新に失敗しました：${e.message}")
            }
        }
    }

    /**
     * 以下、UIの状態やダイアログ制御用のシンプルなメソッド群
     */
    open fun selectPage(id: Int) {
        _selectedPageId.value = id
    }

    open fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }

    open fun showDeleteDialog(id: Int) {
        _showDeleteDialogFor.value = id
    }

    open fun dismissDeleteDialog() {
        _showDeleteDialogFor.value = null
    }

    open fun showAddDialog() {
        _newTitle.value = ""
        _showAddDialog.value = true
    }

    open fun dismissAddDialog() {
        _showAddDialog.value = false
    }

    open fun updateNewTitle(text: String) {
        _newTitle.value = text
    }

    /**
     * Snackbar表示後にイベントをクリア
     */
    fun clearErrorEvent() {
        viewModelScope.launch { _errorEvent.emit(null) }
    }
    fun clearSuccessEvent() {
        viewModelScope.launch { _successEvent.emit(null) }
    }
}