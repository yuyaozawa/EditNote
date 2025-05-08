package com.oza.editnote.data.mapper

import com.oza.editnote.data.model.PageDto
import junit.framework.TestCase.assertEquals
import org.junit.Test

class PageMapperTest {

    @Test
    fun `PageDtoをPageに正しく変換できる`() {
        val dto = PageDto(
            id = 1,
            title = "タイトル",
            body = "本文",
            createdAt = "2025-05-01T12:00:00Z",
            updatedAt = "2025-05-02T12:00:00Z"
        )

        val result = dto.toDomain()

        assertEquals(1, result.id)
        assertEquals("タイトル", result.title)
        assertEquals("本文", result.body)
    }
}