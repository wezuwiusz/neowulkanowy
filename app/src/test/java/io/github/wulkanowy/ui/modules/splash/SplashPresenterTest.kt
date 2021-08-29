package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.MainCoroutineRule
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.ErrorHandler
import io.mockk.MockKAnnotations
import io.mockk.coEvery
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

    @MockK(relaxed = true)
    lateinit var errorHandler: ErrorHandler

    private lateinit var presenter: SplashPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        presenter = SplashPresenter(errorHandler, studentRepository)
    }

    @Test
    fun testOpenLoginView() {
        coEvery { studentRepository.isCurrentStudentSet() } returns false

        presenter.onAttachView(splashView, null)
        verify { splashView.openLoginView() }
    }

    @Test
    fun testMainMainView() {
        coEvery { studentRepository.isCurrentStudentSet() } returns true

        presenter.onAttachView(splashView, null)
        verify { splashView.openMainView() }
    }
}
