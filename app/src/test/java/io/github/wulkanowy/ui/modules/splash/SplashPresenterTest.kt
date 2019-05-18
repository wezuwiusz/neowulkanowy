package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.TestSchedulersProvider
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.ErrorHandler
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class SplashPresenterTest {

    @Mock
    lateinit var splashView: SplashView

    @Mock
    lateinit var studentRepository: StudentRepository

    @Mock
    lateinit var errorHandler: ErrorHandler

    private lateinit var presenter: SplashPresenter

    @Before
    fun initPresenter() {
        MockitoAnnotations.initMocks(this)
        presenter = SplashPresenter(studentRepository, errorHandler, TestSchedulersProvider())
    }

    @Test
    fun testOpenLoginView() {
        doReturn(Single.just(false)).`when`(studentRepository).isCurrentStudentSet()
        presenter.onAttachView(splashView)
        verify(splashView).openLoginView()
    }

    @Test
    fun testMainMainView() {
        doReturn(Single.just(true)).`when`(studentRepository).isCurrentStudentSet()
        presenter.onAttachView(splashView)
        verify(splashView).openMainView()
    }
}
