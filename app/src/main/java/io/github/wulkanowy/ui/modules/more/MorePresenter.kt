package io.github.wulkanowy.ui.modules.more

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import timber.log.Timber
import javax.inject.Inject

class MorePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<MoreView>(errorHandler, studentRepository) {

    override fun onAttachView(view: MoreView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("More view was initialized")
        loadData()
    }

    fun onItemSelected(title: String) {
        Timber.i("Select more item \"${title}\"")
        view?.run {
            when (title) {
                messagesRes?.first -> openMessagesView()
                homeworkRes?.first -> openHomeworkView()
                noteRes?.first -> openNoteView()
                luckyNumberRes?.first -> openLuckyNumberView()
                conferencesRes?.first -> openConferencesView()
                schoolAnnouncementRes?.first -> openSchoolAnnouncementView()
                schoolAndTeachersRes?.first -> openSchoolAndTeachersView()
                mobileDevicesRes?.first -> openMobileDevicesView()
                settingsRes?.first -> openSettingsView()
            }
        }
    }

    fun onViewReselected() {
        Timber.i("More view is reselected")
        view?.popView(2)
    }

    private fun loadData() {
        Timber.i("Load items for more view")
        view?.run {
            updateData(listOfNotNull(
                messagesRes,
                homeworkRes,
                noteRes,
                luckyNumberRes,
                conferencesRes,
                schoolAnnouncementRes,
                schoolAndTeachersRes,
                mobileDevicesRes,
                settingsRes
            ))
        }
    }
}
