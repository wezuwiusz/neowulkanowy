package io.github.wulkanowy.data.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.utils.DispatchersProvider
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class LoggerRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider
) {

    suspend fun getLastLogLines() = getLastModified().readText().split("\n")

    suspend fun getLogFiles() = withContext(dispatchers.io) {
        File(context.filesDir.absolutePath).listFiles(File::isFile)
            ?.filter { it.name.endsWith(".log") }!!
    }

    private suspend fun getLastModified() = withContext(dispatchers.io) {
        var lastModifiedTime = Long.MIN_VALUE
        var chosenFile: File? = null

        File(context.filesDir.absolutePath).listFiles(File::isFile)
            ?.forEach { file ->
                if (file.lastModified() > lastModifiedTime) {
                    lastModifiedTime = file.lastModified()
                    chosenFile = file
                }
            }

        chosenFile ?: throw FileNotFoundException("Log file not found")
    }
}
