package io.github.wulkanowy.ui.login.form

import io.github.wulkanowy.TestSchedulers
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.login.LoginErrorHandler
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class LoginFormPresenterTest {

    @Mock
    lateinit var loginFormView: LoginFormView

    @Mock
    lateinit var repository: SessionRepository

    @Mock
    lateinit var errorHandler: LoginErrorHandler

    private lateinit var presenter: LoginFormPresenter

    @Before
    fun initPresenter() {
        MockitoAnnotations.initMocks(this)
        clearInvocations(repository, loginFormView)
        presenter = LoginFormPresenter(TestSchedulers(), errorHandler, repository)
        presenter.attachView(loginFormView)
    }

    @Test
    fun initViewTest() {
        verify(loginFormView).initInputs()
    }

    @Test
    fun emptyEmailLoginTest() {
        presenter.attemptLogin("", "test123", "test")

        verify(loginFormView).setErrorEmailRequired()
        verify(loginFormView, never()).setErrorPassRequired(false)
        verify(loginFormView, never()).setErrorSymbolRequire()
        verify(loginFormView, never()).setErrorEmailInvalid()
        verify(loginFormView, never()).setErrorPassInvalid(false)
    }

    @Test
    fun invalidEmailLoginTest() {
        presenter.attemptLogin("test", "test123", "test")

        verify(loginFormView, never()).setErrorEmailRequired()
        verify(loginFormView, never()).setErrorPassRequired(false)
        verify(loginFormView, never()).setErrorSymbolRequire()
        verify(loginFormView).setErrorEmailInvalid()
        verify(loginFormView, never()).setErrorPassInvalid(false)
    }

    @Test
    fun emptyPassLoginTest() {
        presenter.attemptLogin("@", "", "test")

        verify(loginFormView, never()).setErrorEmailRequired()
        verify(loginFormView).setErrorPassRequired(true)
        verify(loginFormView, never()).setErrorSymbolRequire()
        verify(loginFormView, never()).setErrorEmailInvalid()
        verify(loginFormView, never()).setErrorPassInvalid(false)
    }

    @Test
    fun invalidPassLoginTest() {
        presenter.attemptLogin("@", "123", "test")

        verify(loginFormView, never()).setErrorEmailRequired()
        verify(loginFormView, never()).setErrorPassRequired(true)
        verify(loginFormView, never()).setErrorSymbolRequire()
        verify(loginFormView, never()).setErrorEmailInvalid()
        verify(loginFormView).setErrorPassInvalid(true)
    }

    @Test
    fun emptySymbolLoginTest() {
        doReturn(Single.just(emptyList<Student>()))
                .`when`(repository).getConnectedStudents(anyString(), anyString(), anyString())
        presenter.attemptLogin("@", "12345", "")
        presenter.attemptLogin("@", "12345", "")

        verify(loginFormView).setErrorSymbolRequire()
    }

    @Test
    fun loginTest() {
        val studentTest = Student(email = "test@", password = "123")
        doReturn(Single.just(listOf(studentTest)))
                .`when`(repository).getConnectedStudents(anyString(), anyString(), anyString())
        presenter.attemptLogin("@", "12345", "test")

        verify(loginFormView).hideSoftKeyboard()
        verify(loginFormView).showLoginProgress(true)
        verify(repository).clearCache()
        verify(loginFormView).showLoginProgress(false)
        verify(loginFormView).switchNextView()
    }

    @Test
    fun loginEmptyTest() {
        doReturn(Single.just(emptyList<Student>()))
                .`when`(repository).getConnectedStudents(anyString(), anyString(), anyString())
        presenter.attemptLogin("@", "12345", "test")

        verify(loginFormView).hideSoftKeyboard()
        verify(loginFormView).showLoginProgress(true)
        verify(repository).clearCache()
        verify(loginFormView).showLoginProgress(false)
        verify(loginFormView).showSymbolInput()
    }

    @Test
    fun loginEmptyTwiceTest() {
        doReturn(Single.just(emptyList<Student>()))
                .`when`(repository).getConnectedStudents(anyString(), anyString(), anyString())
        presenter.attemptLogin("@", "12345", "")
        presenter.attemptLogin("@", "12345", "test")

        verify(loginFormView, times(2)).hideSoftKeyboard()
        verify(loginFormView, times(2)).showLoginProgress(true)
        verify(repository, times(2)).clearCache()
        verify(loginFormView, times(2)).showLoginProgress(false)
        verify(loginFormView, times(2)).showSymbolInput()
        verify(loginFormView).setErrorSymbolIncorrect()

    }

    @Test
    fun loginErrorTest() {
        val testException = RuntimeException()
        doReturn(Single.error<List<Student>>(testException))
                .`when`(repository).getConnectedStudents(anyString(), anyString(), anyString())
        presenter.attemptLogin("@", "12345", "test")

        verify(loginFormView).hideSoftKeyboard()
        verify(loginFormView).showLoginProgress(true)
        verify(repository).clearCache()
        verify(loginFormView).showLoginProgress(false)
        verify(errorHandler).proceed(testException)
    }
}

