package io.github.wulkanowy.data.repositories

import android.content.res.AssetManager
import io.github.wulkanowy.data.pojos.Contributor
import io.github.wulkanowy.utils.DispatchersProvider
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCreatorRepository @Inject constructor(
    private val assets: AssetManager,
    private val dispatchers: DispatchersProvider,
    private val json: Json,
) {

    @OptIn(ExperimentalSerializationApi::class)
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getAppCreators() = withContext(dispatchers.backgroundThread) {
        val inputStream = assets.open("contributors.json").buffered()
        json.decodeFromStream<List<Contributor>>(inputStream)
    }
}
