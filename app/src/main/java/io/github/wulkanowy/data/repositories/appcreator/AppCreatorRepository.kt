package io.github.wulkanowy.data.repositories.appcreator

import android.content.res.AssetManager
import com.google.gson.Gson
import io.github.wulkanowy.data.pojos.AppCreator
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCreatorRepository @Inject constructor(private val assets: AssetManager) {
    fun getAppCreators(): Single<List<AppCreator>> {
        return Single.fromCallable<List<AppCreator>> {
            Gson().fromJson(
                assets.open("contributors.json").bufferedReader().use { it.readText() },
                Array<AppCreator>::class.java
            ).toList()
        }
    }
}
