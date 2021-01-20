package io.github.wulkanowy.ui.modules.main

import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class MainPresenterTest {

    @MockK(relaxed = true)
    lateinit var errorHandler: ErrorHandler

    @MockK
    lateinit var studentRepository: StudentRepository

    @MockK(relaxed = true)
    lateinit var prefRepository: PreferencesRepository

    @MockK(relaxed = true)
    lateinit var syncManager: SyncManager

    @MockK
    lateinit var mainView: MainView

    @MockK(relaxed = true)
    lateinit var analytics: AnalyticsHelper

    private lateinit var presenter: MainPresenter

    @Before
    fun initPresenter() {
        MockKAnnotations.init(this)
        clearMocks(mainView)

        every { mainView.startMenuIndex = any() } just Runs
        every { mainView.startMenuMoreIndex = any() } just Runs
        every { mainView.startMenuIndex } returns 1
        every { mainView.startMenuMoreIndex } returns 1
        every { mainView.initView() } just Runs
        presenter = MainPresenter(errorHandler, studentRepository, prefRepository, syncManager, analytics)
        presenter.onAttachView(mainView, null)
    }

    @Test
    fun initMenuTest() {
        verify { mainView.initView() }
    }

    @Test
    fun onTabSelectedTest() {
        every { mainView.notifyMenuViewChanged() } just Runs

        every { mainView.switchMenuView(1) } just Runs
        presenter.onTabSelected(1, false)
        verify { mainView.switchMenuView(1) }
    }
}
