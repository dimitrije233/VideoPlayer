package com.example.gridvideoplaya.data.network

import com.example.gridvideoplaya.data.model.VideoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("videos/search")
    suspend fun fetchVideos(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String = "nature",
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int
    ): VideoResponse
}

data class VideoRequest(
    val Video: VideoQuery
)

data class VideoQuery(
    val searchTerm: String = "video",
    val languages: List<String> = emptyList(),
    val tagsTaxonomiesIds: List<String> = emptyList()
)