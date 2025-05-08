package com.oza.editnote.data.api

import com.oza.editnote.data.model.PageDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PageApi {

    @GET("/content")
    suspend fun getPages(): List<PageDto>

    @POST("/content")
    suspend fun createPage(@Body page: PageDto): PageDto

    @PUT("/content/{id}")
    suspend fun updatePage(
        @Path("id") id: Int,
        @Body page: PageDto
    ): PageDto

    @DELETE("/content/{id}")
    suspend fun deletePage(@Path("id") id: Int)
}