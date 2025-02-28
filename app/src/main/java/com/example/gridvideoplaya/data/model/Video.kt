package com.example.gridvideoplaya.data.model

data class VideoResponse(
    val videos: List<VideoItemData>
)

data class VideoItemData(
    val id: Int,
    val title: String,
    val description:String,
    val image: String,
    val video_files:  List<PexelsVideoFile>
)

data class PexelsVideoFile(
    val link: String
)