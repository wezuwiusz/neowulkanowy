package io.github.wulkanowy.utils

import android.content.Context
import android.view.View
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.ui.modules.dashboard.DashboardItem
import javax.inject.Inject

@Suppress("unused")
class AdsHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository
) {

    fun initialize() {
        preferencesRepository.isAdsEnabled = false
        preferencesRepository.isAgreeToProcessData = false
        preferencesRepository.selectedDashboardTiles -= DashboardItem.Tile.ADS
    }

    @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
    suspend fun getDashboardTileAdBanner(width: Int): AdBanner {
        throw IllegalStateException("Can't get ad banner (F-droid)")
    }
}

data class AdBanner(val view: View)
