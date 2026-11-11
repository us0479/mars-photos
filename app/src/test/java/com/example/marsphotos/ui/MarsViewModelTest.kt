package com.example.marsphotos.ui

import com.example.marsphotos.data.MarsPhoto
import com.example.marsphotos.data.MarsPhotosRepository
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarsViewModelTest {
    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val photos = listOf(MarsPhoto("42", "https://example.invalid/mars.jpg"))

    @Test
    fun loadPhotos_waitsInLoading_thenShowsSuccess() = runTest {
        val response = CompletableDeferred<List<MarsPhoto>>()
        val repository = MarsPhotosRepository { response.await() }
        val viewModel = MarsViewModel(repository)

        runCurrent()
        assertEquals(MarsUiState.Loading, viewModel.uiState.value)

        response.complete(photos)
        advanceUntilIdle()
        assertEquals(MarsUiState.Success(photos), viewModel.uiState.value)
    }

    @Test
    fun loadPhotos_whenOffline_showsError() = runTest {
        val repository = MarsPhotosRepository { throw IOException("offline") }
        val viewModel = MarsViewModel(repository)

        advanceUntilIdle()
        assertEquals(MarsUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun retry_afterError_loadsPhotosAgain() = runTest {
        var attempts = 0
        val repository = MarsPhotosRepository {
            attempts++
            if (attempts == 1) throw IOException("offline")
            photos
        }
        val viewModel = MarsViewModel(repository)

        advanceUntilIdle()
        assertEquals(MarsUiState.Error, viewModel.uiState.value)

        viewModel.loadPhotos()
        assertEquals(MarsUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()

        assertEquals(2, attempts)
        assertEquals(MarsUiState.Success(photos), viewModel.uiState.value)
    }
}