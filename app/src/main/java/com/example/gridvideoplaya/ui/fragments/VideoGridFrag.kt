package com.example.gridvideoplaya.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gridvideoplaya.ui.components.VideoItem
import com.example.gridvideoplaya.viewModel.VideoViewModel

@Composable
fun VideoGridScreen(viewModel: VideoViewModel, navController: NavController) {
    val videos by viewModel.videos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listState = rememberLazyGridState()

    var initialLoadComplete by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (videos.isEmpty() && !isLoading) {
            viewModel.loadVideos()
        }
    }

    LaunchedEffect(videos.isNotEmpty()) {
        if (videos.isNotEmpty()) {
            initialLoadComplete = true
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (initialLoadComplete &&
            listState.layoutInfo.visibleItemsInfo.isNotEmpty() &&
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == videos.size - 1 &&
            !isLoading) {
            viewModel.loadMoreVideos()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {

        if (isLoading && videos.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = listState,
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(videos) { index, video ->
                    VideoItem(video) {
                        navController.navigate("videoDetail/${video.id}")
                    }
                }

                if (isLoading && videos.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                }
            }
        }
    }
}