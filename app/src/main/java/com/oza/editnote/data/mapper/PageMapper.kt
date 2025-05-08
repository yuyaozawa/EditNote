package com.oza.editnote.data.mapper

import com.oza.editnote.data.model.PageDto
import com.oza.editnote.domain.model.Page

fun PageDto.toDomain(): Page {
    return Page(
        id = id,
        title = title,
        body = body,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Page.toDto(): PageDto {
    return PageDto(
        id = id,
        title = title,
        body = body,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}