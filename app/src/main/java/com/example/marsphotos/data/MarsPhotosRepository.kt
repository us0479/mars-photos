package com.example.marsphotos.data

import com.example.marsphotos.network.MarsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun interface MarsPhotosRepository {
    suspend fun getPhotos(): List<MarsPhoto>
}

class NetworkMarsPhotosRepository(
    private val api: MarsApiService,
) : MarsPhotosRepository {
    override suspend fun getPhotos(): List<MarsPhoto> = withContext(Dispatchers.IO) {
        api.getPhotos()
    }
}