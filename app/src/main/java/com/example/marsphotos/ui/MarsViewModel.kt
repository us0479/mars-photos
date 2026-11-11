package com.example.marsphotos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.MarsPhoto
import com.example.marsphotos.data.MarsPhotosRepository
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

sealed interface MarsUiState {
    data object Loading : MarsUiState
    data class Success(val photos: List<MarsPhoto>) : MarsUiState
    data object Error : MarsUiState
}

class MarsViewModel(
    private val repository: MarsPhotosRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MarsUiState>(MarsUiState.Loading)
    val uiState: StateFlow<MarsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        if (loadJob?.isActive == true) return

        _uiState.value = MarsUiState.Loading

        loadJob = viewModelScope.launch {
            _uiState.value = try {
                MarsUiState.Success(repository.getPhotos())
            } catch (error: IOException) {
                MarsUiState.Error
            } catch (error: HttpException) {
                MarsUiState.Error
            } catch (error: SerializationException) {
                MarsUiState.Error
            }
        }
    }
}