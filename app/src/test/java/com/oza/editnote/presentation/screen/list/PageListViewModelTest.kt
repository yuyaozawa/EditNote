package com.oza.editnote.presentation.screen.list

import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.usecase.CreatePageUseCase
import com.oza.editnote.domain.usecase.DeletePageUseCase
import com.oza.editnote.domain.usecase.GetPagesUseCase
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
    // --- モックを用意 ---
    private val mockGetPagesUseCase    = mock<GetPagesUseCase>()
    private val mockCreatePageUseCase  = mock<CreatePageUseCase>()
    private val mockDeletePageUseCase  = mock<DeletePageUseCase>()
    private val mockUpdatePageUseCase  = mock<UpdatePageUseCase>()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Main dispatcher をテスト用に差し替え
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPages_success updates Success state`() = runTest {
        // Arrange
        val expectedPages = listOf(Page(1, "A", "B"))
        whenever(mockGetPagesUseCase()).thenReturn(expectedPages)

        // VM を生成すると init で loadPages が呼ばれる
        val viewModel = PageListViewModel(
            getPagesUseCase   = mockGetPagesUseCase,
            createPageUseCase = mockCreatePageUseCase,
            deletePageUseCase = mockDeletePageUseCase,
            updatePageUseCase = mockUpdatePageUseCase
        )

        // Act: コルーチンを進める
        advanceUntilIdle()

        // Assert
        assertEquals(
            PageListUiState.Success(expectedPages),
            viewModel.uiState.value
        )
    }

    @Test
    fun `loadPages_failure updates Error state`() = runTest {
        // Arrange
        whenever(mockGetPagesUseCase()).thenThrow(RuntimeException("fail"))

        val viewModel = PageListViewModel(
            getPagesUseCase   = mockGetPagesUseCase,
            createPageUseCase = mockCreatePageUseCase,
            deletePageUseCase = mockDeletePageUseCase,
            updatePageUseCase = mockUpdatePageUseCase
        )

        // Act
        advanceUntilIdle()

        // Assert
        assertEquals(
            PageListUiState.Error("fail"),
            viewModel.uiState.value
        )
    }
}