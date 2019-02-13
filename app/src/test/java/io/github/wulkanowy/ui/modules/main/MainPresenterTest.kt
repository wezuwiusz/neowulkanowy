package io.github.wulkanowy.ui.modules.main

import io.github.wulkanowy.TestSchedulersProvider
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.job.ServiceHelper
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class MainPresenterTest {

    @Mock
    lateinit var errorHandler: ErrorHandler

    @Mock
    lateinit var studentRepository: StudentRepository

    @Mock
    lateinit var prefRepository: PreferencesRepository

    @Mock
    lateinit var serviceHelper: ServiceHelper

    @Mock
    lateinit var mainView: MainView

    @Mock
    lateinit var analytics: FirebaseAnalyticsHelper

    private lateinit var presenter: MainPresenter

    @Before
    fun initPresenter() {
        MockitoAnnotations.initMocks(this)
        clearInvocations(mainView)

        presenter = MainPresenter(errorHandler, studentRepository, prefRepository, TestSchedulersProvider(), serviceHelper, analytics)
        presenter.onAttachView(mainView, -1)
    }

    @Test
    fun initMenuTest() {
        verify(mainView).initView()
    }

    @Test
    fun onTabSelectedTest() {
        presenter.onTabSelected(1, false)
        verify(mainView).switchMenuView(1)
    }
}

