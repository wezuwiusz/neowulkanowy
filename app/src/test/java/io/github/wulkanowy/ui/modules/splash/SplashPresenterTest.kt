package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.MainCoroutineRule
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AppInfo
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SplashPresenterTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @MockK(relaxed = true)
    lateinit var splashView: SplashView

    @MockK
    lateinit var studentRepository: StudentRepository

    @MockK
    lateinit var preferencesRepository: PreferencesRepository

    @MockK
    lateinit var appInfo: AppInfo

    @MockK(relaxed = true)
    lateinit var errorHandler: ErrorHandler

    private lateinit var presenter: SplashPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        presenter = SplashPresenter(errorHandler, studentRepository, preferencesRepository, appInfo)
    }

    @Test
    fun testOpenLoginView() {
        every { appInfo.systemVersion } returns 30
        every { preferencesRepository.isKitkatDialogDisabled } returns true
        coEvery { studentRepository.isCurrentStudentSet() } returns false

        presenter.onAttachView(splashView, null)
        verify { splashView.openLoginView() }
    }

    @Test
    fun testMainMainView() {
        every { appInfo.systemVersion } returns 30
        every { preferencesRepository.isKitkatDialogDisabled } returns true
        coEvery { studentRepository.isCurrentStudentSet() } returns true

        presenter.onAttachView(splashView, null)
        verify { splashView.openMainView() }
    }
}
