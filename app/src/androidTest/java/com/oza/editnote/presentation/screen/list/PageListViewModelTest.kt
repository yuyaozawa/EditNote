package com.oza.editnote.presentation.screen.list

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@SmallTest
@RunWith(AndroidJUnit4::class)
class PageListViewModelTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var viewModel: PageListViewModel

    @Before fun setup() {
        hiltRule.inject()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadPagesEmitsSuccessWithFakeData() = runTest {
        // FakeUseCaseModule を通して注入された FakeGetAllPagesUseCase が返すデータでテスト
        viewModel.loadPages()
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(PageListUiState.Success::class.java)
        val pages = (state as PageListUiState.Success).pages
        assertThat(pages).hasSize(1)
        assertThat(pages[0].title).isEqualTo("Test Page")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deletePageEmitsSuccessEvent() = runTest {
        // まずダイアログ対象をセット
        viewModel.showDeleteDialog(1)
        viewModel.deletePage()
        // successEvent の内容を検証
        val msg = viewModel.successEvent.replayCache.firstOrNull()
        assertThat(msg).isEqualTo("ページを削除しました")
    }
}