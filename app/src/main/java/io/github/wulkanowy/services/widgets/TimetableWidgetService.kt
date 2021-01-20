package io.github.wulkanowy.services.widgets

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetFactory
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TimetableWidgetService : RemoteViewsService() {

    @Inject
    lateinit var timetableRepo: TimetableRepository

    @Inject
    lateinit var studentRepo: StudentRepository

    @Inject
    lateinit var semesterRepo: SemesterRepository

    @Inject
    lateinit var prefRepository: PreferencesRepository

    @Inject
    lateinit var sharedPref: SharedPrefProvider

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        Timber.d("TimetableWidgetFactory created")
        return TimetableWidgetFactory(timetableRepo, studentRepo, semesterRepo, prefRepository, sharedPref, applicationContext, intent)
    }
}
