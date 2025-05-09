package com.oza.editnote.presentation.screen.list

import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.usecase.CreatePageUseCase
import com.oza.editnote.domain.usecase.DeletePageUseCase
import com.oza.editnote.domain.usecase.GetPagesUseCase
import com.oza.editnote.domain.usecase.ICreatePageUseCase
import com.oza.editnote.domain.usecase.IDeletePageUseCase
import com.oza.editnote.domain.usecase.IGetPagesUseCase
import com.oza.editnote.domain.usecase.IUpdatePageUseCase
import com.oza.editnote.domain.usecase.UpdatePageUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PageListViewModelTest {
    // モック化したユースケース
    private val mockGetPagesUseCase   = mock<IGetPagesUseCase>()
    private val mockCreatePageUseCase = mock<ICreatePageUseCase>()
    private val mockDeletePageUseCase = mock<IDeletePageUseCase>()
    private val mockUpdatePageUseCase = mock<IUpdatePageUseCase>()

    // テスト用ディスパッチャ
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Dispatchers.Main をテスト用に差し替える
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        // 差し替えた Main を元に戻す
        Dispatchers.resetMain()
    }

    /**
     * loadPages() が正常終了したとき、
     * ViewModel の uiState が PageListUiState.Success になり、
     * 取得したページ一覧が格納されていることを検証
     */
    @Test
    fun `loadPages_success updates Success state`() = runTest {
        // Arrange: モックが正常にページリストを返すようにする
        val expected = listOf(Page(1, "A", "B"))
        whenever(mockGetPagesUseCase()).thenReturn(expected)

        // VM を生成（init ブロックで loadPages() が実行される）
        val vm = PageListViewModel(
            getPagesUseCase   = mockGetPagesUseCase,
            createPageUseCase = mockCreatePageUseCase,
            deletePageUseCase = mockDeletePageUseCase,
            updatePageUseCase = mockUpdatePageUseCase
        )

        // Act: コルーチンを進める
        advanceUntilIdle()

        // Assert: uiState が Success(expected) になっていること
        assertEquals(PageListUiState.Success(expected), vm.uiState.value)
    }

    /**
     * loadPages() が例外をスローしたとき、
     * ViewModel の uiState が PageListUiState.Error になり、
     * 例外メッセージが格納されていることを検証
     */
    @Test
    fun `loadPages_failure updates Error state`() = runTest {
        // Arrange: モックが例外をスローするようにする
        whenever(mockGetPagesUseCase()).thenThrow(RuntimeException("fail"))

        // VM を生成
        val vm = PageListViewModel(
            getPagesUseCase   = mockGetPagesUseCase,
            createPageUseCase = mockCreatePageUseCase,
            deletePageUseCase = mockDeletePageUseCase,
            updatePageUseCase = mockUpdatePageUseCase
        )

        // Act: コルーチンを進める
        advanceUntilIdle()

        // Assert: uiState が Error("fail") になっていること
        assertEquals(PageListUiState.Error("fail"), vm.uiState.value)
    }
}