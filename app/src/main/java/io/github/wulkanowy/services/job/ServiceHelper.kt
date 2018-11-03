package io.github.wulkanowy.services.job

import com.firebase.jobdispatcher.Constraint.ON_ANY_NETWORK
import com.firebase.jobdispatcher.Constraint.ON_UNMETERED_NETWORK
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.Lifetime.FOREVER
import com.firebase.jobdispatcher.RetryStrategy.DEFAULT_EXPONENTIAL
import com.firebase.jobdispatcher.Trigger.executionWindow
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.utils.isHolidays
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceHelper @Inject constructor(
    private val prefRepository: PreferencesRepository,
    private val dispatcher: FirebaseJobDispatcher
) {

    fun reloadFullSyncService() {
        startFullSyncService(true)
    }

    fun startFullSyncService(replaceCurrent: Boolean = false) {
        if (LocalDate.now().isHolidays || !prefRepository.serviceEnabled) {
            Timber.d("Services disabled or it's holidays")
            return
        }

        dispatcher.mustSchedule(
            dispatcher.newJobBuilder()
                .setLifetime(FOREVER)
                .setRecurring(true)
                .setService(SyncWorker::class.java)
                .setTag(SyncWorker.WORK_TAG)
                .setTrigger(
                    executionWindow(
                        prefRepository.servicesInterval * 60,
                        (prefRepository.servicesInterval + 10) * 60
                    )
                )
                .setConstraints(if (prefRepository.servicesOnlyWifi) ON_UNMETERED_NETWORK else ON_ANY_NETWORK)
                .setReplaceCurrent(replaceCurrent)
                .setRetryStrategy(DEFAULT_EXPONENTIAL)
                .build()
        )

        Timber.d("Services started")
    }

    fun stopFullSyncService() {
        dispatcher.cancel(SyncWorker.WORK_TAG)
        Timber.d("Services stopped")
    }
}
