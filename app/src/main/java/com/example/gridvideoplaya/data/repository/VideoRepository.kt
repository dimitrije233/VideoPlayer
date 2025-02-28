package com.example.gridvideoplaya.data.repository

import com.example.gridvideoplaya.data.model.VideoItemData
import com.example.gridvideoplaya.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoRepository(private val apiService: ApiService) {
    private val API_KEY = "ipqQ04gu3WzTmhLMrnC6WaEOEypCSxCAXA44aL41hUIx96fG3GnLQ1J5"  // Replace with your actual API key

    suspend fun getVideos(page: Int, perPage: Int): List<VideoItemData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.fetchVideos(API_KEY, page = page, perPage = perPage)
                response.videos.map { video ->
                    VideoItemData(
                        id = video.id,
                        title = video.title ?: "Untitled Video",
                        description = video.description ?: "No description available.",
                        image = video.image ,
                        video_files = video.video_files
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}