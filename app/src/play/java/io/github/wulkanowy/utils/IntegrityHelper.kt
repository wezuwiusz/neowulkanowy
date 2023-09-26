package io.github.wulkanowy.utils

import android.content.Context
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntegrityHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    suspend fun getIntegrityToken(nonce: String): String? {
        val integrityManager = IntegrityManagerFactory.create(context)

        val integrityTokenResponse = integrityManager.requestIntegrityToken(
            IntegrityTokenRequest.builder()
                .setNonce(nonce)
                .build()
        )
        return integrityTokenResponse.await().token()
    }
}
