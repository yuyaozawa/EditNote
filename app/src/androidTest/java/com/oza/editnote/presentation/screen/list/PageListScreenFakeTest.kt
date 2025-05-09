package com.oza.editnote.presentation.screen.list

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oza.editnote.TestActivity
import com.oza.editnote.fakes.FakeGetAllPagesUseCase
import com.oza.editnote.fakes.FakeCreatePageUseCase
import com.oza.editnote.fakes.FakeDeletePageUseCase
import com.oza.editnote.fakes.FakeUpdatePageUseCase
import dagger.hilt.android.testing.HiltAndroidTest

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PageListScreenFakeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun fakeViewModel_displaysFixedPageAndButtons() {
        // テスト用 FakeViewModel を作成
        val fakeVm = FakePageListViewModel(
            getPagesUseCase   = FakeGetAllPagesUseCase(),
            createPageUseCase = FakeCreatePageUseCase(),
            deletePageUseCase = FakeDeletePageUseCase(),
            updatePageUseCase = FakeUpdatePageUseCase()
        )

        // TestActivity の setContent で差し替え
        composeTestRule.activity.setContent {
            PageListScreen(viewModel = fakeVm)
        }

        // 固定データ（テストページ）が表示されているか？
        composeTestRule.onNodeWithText("テストページ").assertExists()
        // 編集モード ON 前提なので New Page ボタンも存在
        composeTestRule.onNodeWithText("New Page").assertExists()
        // アプリタイトルもちゃんと出ている
        composeTestRule.onNodeWithText("EditNote").assertExists()
    }
}