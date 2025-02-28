package com.example.gridvideoplaya

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.gridvideoplaya.data.repository.VideoRepository
import com.example.gridvideoplaya.navigation.AppNavHost
import com.example.gridvideoplaya.ui.fragments.SplashScreen
import com.example.gridvideoplaya.viewModel.VideoViewModel
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val apiService = RetrofitInstance.api
        val repository = VideoRepository(apiService)
        val viewModel = VideoViewModel(repository)

        setContent {

            var showSplash by remember { mutableStateOf(true) }
            val navController = rememberNavController()

            if (showSplash) {
                SplashScreen {
                    showSplash = false
                }
            } else {
                AppNavHost(navController, viewModel)
            }

        }

    }
}
