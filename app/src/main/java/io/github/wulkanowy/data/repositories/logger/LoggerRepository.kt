package io.github.wulkanowy.data.repositories.logger

import android.content.Context
import io.reactivex.Single
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class LoggerRepository @Inject constructor(private val context: Context) {

    fun getLastLogLines(): Single<List<String>> {
        return getLastModified()
            .map { it.readText() }
            .map { it.split("\n") }
    }

    fun getLogFiles(): Single<List<File>> {
        return Single.fromCallable {
            File(context.filesDir.absolutePath).listFiles(File::isFile)?.filter {
                it.name.endsWith(".log")
            }
        }
    }

    private fun getLastModified(): Single<File> {
        return Single.fromCallable {
            var lastModifiedTime = Long.MIN_VALUE
            var chosenFile: File? = null
            File(context.filesDir.absolutePath).listFiles(File::isFile)?.forEach { file ->
                if (file.lastModified() > lastModifiedTime) {
                    lastModifiedTime = file.lastModified()
                    chosenFile = file
                }
            }
            if (chosenFile == null) throw FileNotFoundException("Log file not found")
            chosenFile
        }
    }
}
