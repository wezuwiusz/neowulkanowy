package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.TestSchedulersProvider
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.ErrorHandler
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SplashPresenterTest {

    @MockK(relaxed = true)
    lateinit var splashView: SplashView

    @MockK
    lateinit var studentRepository: StudentRepository

    @MockK(relaxed = true)
    lateinit var errorHandler: ErrorHandler

    private lateinit var presenter: SplashPresenter

    @Before
    fun initPresenter() {
        MockKAnnotations.init(this)
        presenter = SplashPresenter(TestSchedulersProvider(), errorHandler, studentRepository)
    }

    @Test
    fun testOpenLoginView() {
        coEvery { studentRepository.isCurrentStudentSet() } returns false
        presenter.onAttachView(splashView)
        verify { splashView.openLoginView() }
    }

    @Test
    fun testMainMainView() {
        coEvery { studentRepository.isCurrentStudentSet() } returns true
        presenter.onAttachView(splashView)
        verify { splashView.openMainView() }
    }
}
