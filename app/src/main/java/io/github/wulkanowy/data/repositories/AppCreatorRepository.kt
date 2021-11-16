package io.github.wulkanowy.data.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
    private val json: Json,
) {

    @OptIn(ExperimentalSerializationApi::class)
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getAppCreators() = withContext(dispatchers.io) {
        val inputStream = context.assets.open("contributors.json").buffered()
        json.decodeFromStream<List<Contributor>>(inputStream)
    }
}
