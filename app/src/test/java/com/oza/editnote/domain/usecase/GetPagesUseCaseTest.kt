package com.oza.editnote.domain.usecase

import com.oza.editnote.domain.model.Page
import com.oza.editnote.domain.repository.PageRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify


class GetPagesUseCaseTest {

    private val mockRepository = mock<PageRepository>()
    private val useCase = GetPagesUseCase(mockRepository)

    @Test
    fun `Repositoryから取得したページリストをそのまま返す`() = runTest {
        val expectedPages = listOf(
            Page(id = 1, title = "タイトル1", body = "本文1"),
            Page(id = 2, title = "タイトル2", body = "本文2")
        )

        whenever(mockRepository.getPages()).thenReturn(expectedPages)

        val result = useCase()

        assertEquals(expectedPages, result)
        verify(mockRepository).getPages()
    }
}