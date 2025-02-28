package com.example.gridvideoplaya.ui.fragments

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gridvideoplaya.data.model.VideoItemData
import com.example.gridvideoplaya.viewModel.VideoViewModel
import kotlinx.coroutines.delay
import java.util.UUID


@Composable
fun VideoDetailScreen(videoId: Int?, navController: NavController, viewModel: VideoViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity

    val savedVideoId = rememberSaveable { mutableStateOf(videoId) }
    var videoUrl by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(true) }

    var playbackPosition by rememberSaveable { mutableStateOf(0L) }
    var playWhenReady by rememberSaveable { mutableStateOf(true) }

    val randomDescriptions = listOf(
        "This incredible footage showcases nature at its finest, with breathtaking views and stunning details."
    )

    var videoDescription by rememberSaveable {
        mutableStateOf(randomDescriptions.random())
    }

    var recomposeToggle by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (savedVideoId.value != null && videoUrl.isEmpty()) {
            val fetchedVideo = viewModel.getVideoById(savedVideoId.value!!)
            if (fetchedVideo != null) {
                videoUrl = fetchedVideo.video_files.firstOrNull()?.link ?: ""
            }
            isLoading = false
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var isFullscreen by rememberSaveable { mutableStateOf(isLandscape) }

    LaunchedEffect(configuration.orientation) {
        isFullscreen = isLandscape
    }

    val onExitFullscreen: () -> Unit = {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        isFullscreen = false
    }

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    DisposableEffect(onBackPressedDispatcher, isFullscreen) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFullscreen) {
                    onExitFullscreen()
                } else {
                    this.remove()
                    navController.popBackStack()
                }
            }
        }

        onBackPressedDispatcher?.addCallback(callback)

        onDispose {
            callback.remove()
        }
    }

    val updatePlaybackPosition: (Long) -> Unit = { position ->
        playbackPosition = position
    }

    val updatePlayWhenReady: (Boolean) -> Unit = { ready ->
        playWhenReady = ready
    }

    val playerKey = remember(videoUrl, recomposeToggle) { UUID.randomUUID() }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {

        if (videoUrl.isNotEmpty()) {
            key(playerKey) {
                ExoPlayerView(
                    videoUrl = videoUrl,
                    onExitFullscreen = onExitFullscreen,
                    savedPlaybackPosition = playbackPosition,
                    savedPlayWhenReady = playWhenReady,
                    updatePlaybackPosition = updatePlaybackPosition,
                    updatePlayWhenReady = updatePlayWhenReady
                )
            }

            if (!isFullscreen) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x99000000)  // Semi-transparent black
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Video Description",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = videoDescription,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }
            }
        } else if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Text("Video not found", modifier = Modifier.align(Alignment.Center), color = Color.White)
        }
    }
}
