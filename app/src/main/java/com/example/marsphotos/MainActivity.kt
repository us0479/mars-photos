package com.example.marsphotos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.data.NetworkMarsPhotosRepository
import com.example.marsphotos.network.MarsApi
import com.example.marsphotos.ui.MarsPhotosApp
import com.example.marsphotos.ui.MarsViewModel
import com.example.marsphotos.ui.theme.MarsPhotosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = viewModelFactory {
            initializer {
                MarsViewModel(
                    repository = NetworkMarsPhotosRepository(MarsApi.service),
                )
            }
        }
        val viewModel = ViewModelProvider(this, factory)[MarsViewModel::class.java]

        setContent {
            MarsPhotosTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                MarsPhotosApp(
                    uiState = uiState,
                    onRetry = viewModel::loadPhotos,
                )
            }
        }
    }
}