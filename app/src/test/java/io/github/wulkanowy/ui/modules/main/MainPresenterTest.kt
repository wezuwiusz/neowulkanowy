package io.github.wulkanowy.ui.modules.main

import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AdsHelper
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.AppInfo
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.serialization.json.Json
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

    @MockK(relaxed = true)
    lateinit var appInfo: AppInfo

    @MockK(relaxed = true)
    lateinit var adsHelper: AdsHelper

    private lateinit var presenter: MainPresenter

    @Before
    fun initPresenter() {
        MockKAnnotations.init(this)
        clearMocks(mainView)

        every { mainView.initView(any(), any(), any()) } just Runs
        presenter = MainPresenter(
            errorHandler = errorHandler,
            studentRepository = studentRepository,
            preferencesRepository = prefRepository,
            syncManager = syncManager,
            analytics = analytics,
            json = Json,
            appInfo = appInfo,
            adsHelper = adsHelper
        )
        presenter.onAttachView(mainView, null)
    }

    @Test
    fun onTabSelectedTest() {
        every { mainView.notifyMenuViewChanged() } just Runs

        every { mainView.switchMenuView(1) } just Runs
        presenter.onTabSelected(1, false)
        verify { mainView.switchMenuView(1) }
    }
}
