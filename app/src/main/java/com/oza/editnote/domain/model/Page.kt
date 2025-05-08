package com.oza.editnote.domain.model


data class Page(
    val id: Int? = null,
    val title: String,
    val body: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)