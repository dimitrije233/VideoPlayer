package com.example.gridvideoplaya.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gridvideoplaya.ui.fragments.VideoDetailScreen
import com.example.gridvideoplaya.ui.fragments.VideoGridScreen
import com.example.gridvideoplaya.viewModel.VideoViewModel

@Composable
fun AppNavHost(navController: NavHostController, viewModel: VideoViewModel) {

    DisposableEffect(Unit) {
        onDispose {
        }
    }

    NavHost(navController, startDestination = "VideoGridScreen") {
        composable("VideoGridScreen") {

            DisposableEffect(Unit) {
                onDispose {
                }
            }

            VideoGridScreen(viewModel, navController)

        }

        composable("videoDetail/{videoId}") { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
            VideoDetailScreen(videoId.toInt(), navController, viewModel)
        }
    }

}