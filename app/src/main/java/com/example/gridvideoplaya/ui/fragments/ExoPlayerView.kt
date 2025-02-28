package com.example.gridvideoplaya.ui.fragments

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView


@Composable
fun ExoPlayerView(
    videoUrl: String,
    onExitFullscreen: () -> Unit,
    savedPlaybackPosition: Long = 0L,
    savedPlayWhenReady: Boolean = true,
    updatePlaybackPosition: (Long) -> Unit = {},
    updatePlayWhenReady: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val mainHandler = remember { Handler(Looper.getMainLooper()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            seekTo(savedPlaybackPosition)
            playWhenReady = savedPlayWhenReady
        }
    }

    var isFullscreen by remember { mutableStateOf(isLandscape) }

    DisposableEffect(videoUrl) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.seekTo(savedPlaybackPosition)
        exoPlayer.playWhenReady = savedPlayWhenReady
        exoPlayer.prepare()

        val updateRunnable = object : Runnable {
            override fun run() {
                if (exoPlayer.isPlaying) {
                    updatePlaybackPosition(exoPlayer.currentPosition)
                }
                updatePlayWhenReady(exoPlayer.playWhenReady)
                mainHandler.postDelayed(this, 1000)
            }
        }

        mainHandler.post(updateRunnable)

        onDispose {
            updatePlaybackPosition(exoPlayer.currentPosition)
            updatePlayWhenReady(exoPlayer.playWhenReady)
            mainHandler.removeCallbacksAndMessages(null)
            exoPlayer.release()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    updatePlaybackPosition(exoPlayer.currentPosition)
                    updatePlayWhenReady(exoPlayer.playWhenReady)
                }
                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer.seekTo(savedPlaybackPosition)
                    exoPlayer.playWhenReady = savedPlayWhenReady
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(isLandscape) {
        isFullscreen = isLandscape
        activity?.requestedOrientation = if (isFullscreen) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = if (isFullscreen) {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            },
            update = { playerView ->
                playerView.player = exoPlayer
                playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

                playerView.showController()
                playerView.useController = true
            }
        )

        IconButton(
            onClick = {
                isFullscreen = !isFullscreen
                if (isFullscreen) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    onExitFullscreen()
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
        ) {
            Icon(
                imageVector = if (isFullscreen) Icons.Filled.Close else Icons.Filled.KeyboardArrowUp,
                contentDescription = "Toggle Fullscreen",
                tint = Color.White
            )
        }
    }
}
