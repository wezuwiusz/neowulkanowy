package io.github.wulkanowy.data.repositories

import android.content.res.AssetManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.github.wulkanowy.data.pojos.Contributor
import io.github.wulkanowy.utils.DispatchersProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCreatorRepository @Inject constructor(
    private val assets: AssetManager,
    private val dispatchers: DispatchersProvider
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getAppCreators() = withContext(dispatchers.backgroundThread) {
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(List::class.java, Contributor::class.java)
        val adapter = moshi.adapter<List<Contributor>>(type)
        adapter.fromJson(assets.open("contributors.json").bufferedReader().use { it.readText() })
    }
}
