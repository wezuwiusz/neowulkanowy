package io.github.wulkanowy.services.widgets

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.android.AndroidInjection
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.ui.widgets.timetable.TimetableWidgetFactory
import javax.inject.Inject

class TimetableWidgetService : RemoteViewsService() {

    @Inject
    lateinit var timetableRepo: TimetableRepository

    @Inject
    lateinit var studentRepo: StudentRepository

    @Inject
    lateinit var semesterRepo: SemesterRepository

    @Inject
    lateinit var sharedPref: SharedPrefHelper

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        AndroidInjection.inject(this)
        return TimetableWidgetFactory(timetableRepo, studentRepo, semesterRepo, sharedPref, applicationContext, intent)
    }
}
