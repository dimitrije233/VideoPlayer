package com.example.gridvideoplaya.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gridvideoplaya.data.model.VideoItemData
import com.example.gridvideoplaya.data.repository.VideoRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VideoViewModel(private val repository: VideoRepository) : ViewModel() {
    private val _videos = MutableStateFlow<List<VideoItemData>>(emptyList())
    val videos: StateFlow<List<VideoItemData>> = _videos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var currentPage = 1
    private val perPage = 10

    init {
        loadVideos()
    }

    fun loadVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newVideos = repository.getVideos(currentPage, perPage)  // Call your API
                _videos.value = newVideos
                currentPage++
            } catch (e: Exception) {

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val moreVideos = repository.getVideos(currentPage, 6)  // Fetch 6 more items
                _videos.value = _videos.value + moreVideos
                currentPage++
            } catch (e: Exception) {

             } finally {
                _isLoading.value = false
            }
        }
    }
    fun getVideoById(videoId: Int): VideoItemData? {
        return _videos.value.find { it.id == videoId }
    }
}