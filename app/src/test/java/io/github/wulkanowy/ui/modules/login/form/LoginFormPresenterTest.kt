package io.github.wulkanowy.ui.modules.login.form

import io.github.wulkanowy.TestSchedulersProvider
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDateTime.now

class LoginFormPresenterTest {

    @Mock
    lateinit var loginFormView: LoginFormView

    @Mock
    lateinit var repository: StudentRepository

    @Mock
    lateinit var errorHandler: LoginErrorHandler

    @Mock
    lateinit var analytics: FirebaseAnalyticsHelper

    private lateinit var presenter: LoginFormPresenter

    @Before
    fun initPresenter() {
        MockitoAnnotations.initMocks(this)
        clearInvocations(repository, loginFormView)
        presenter = LoginFormPresenter(TestSchedulersProvider(), repository, errorHandler, analytics)
        presenter.onAttachView(loginFormView)
    }

    @Test
    fun initViewTest() {
        verify(loginFormView).initView()
    }

    @Test
    fun emptyNicknameLoginTest() {
        `when`(loginFormView.formUsernameValue).thenReturn("")
        `when`(loginFormView.formPassValue).thenReturn("test123")
        `when`(loginFormView.formHostValue).thenReturn("https://fakelog.cf")
        presenter.onSignInClick()

        verify(loginFormView).setErrorUsernameRequired()
        verify(loginFormView, never()).setErrorPassRequired(false)
        verify(loginFormView, never()).setErrorPassInvalid(false)
    }

    @Test
    fun emptyPassLoginTest() {
        `when`(loginFormView.formUsernameValue).thenReturn("@")
        `when`(loginFormView.formPassValue).thenReturn("")
        `when`(loginFormView.formHostValue).thenReturn("https://fakelog.cf")
        presenter.onSignInClick()

        verify(loginFormView, never()).setErrorUsernameRequired()
        verify(loginFormView).setErrorPassRequired(true)
        verify(loginFormView, never()).setErrorPassInvalid(false)
    }

    @Test
    fun invalidPassLoginTest() {
        `when`(loginFormView.formUsernameValue).thenReturn("@")
        `when`(loginFormView.formPassValue).thenReturn("123")
        `when`(loginFormView.formHostValue).thenReturn("https://fakelog.cf")
        presenter.onSignInClick()

        verify(loginFormView, never()).setErrorUsernameRequired()
        verify(loginFormView, never()).setErrorPassRequired(true)
        verify(loginFormView).setErrorPassInvalid(true)
    }

    @Test
    fun loginTest() {
        val studentTest = Student(email = "test@", password = "123", scrapperBaseUrl = "https://fakelog.cf", loginType = "AUTO", studentName = "", schoolSymbol = "", schoolName = "", studentId = 0, classId = 1, isCurrent = false, symbol = "", registrationDate = now(), className = "", mobileBaseUrl = "", privateKey = "", certificateKey = "", loginMode = "", userLoginId = 0, isParent = false)
        doReturn(Single.just(listOf(studentTest))).`when`(repository).getStudentsScrapper(anyString(), anyString(), anyString(), anyString())

        `when`(loginFormView.formUsernameValue).thenReturn("@")
        `when`(loginFormView.formPassValue).thenReturn("123456")
        `when`(loginFormView.formHostValue).thenReturn("https://fakelog.cf")
        `when`(loginFormView.formSymbolValue).thenReturn("Default")
        `when`(loginFormView.formHostSymbol).thenReturn("Default")
        presenter.onSignInClick()

        verify(loginFormView).hideSoftKeyboard()
        verify(loginFormView).showProgress(true)
        verify(loginFormView).showProgress(false)
        verify(loginFormView).showContent(false)
        verify(loginFormView).showContent(true)
    }

    @Test
    fun loginEmptyTest() {
        doReturn(Single.just(emptyList<Student>()))
            .`when`(repository).getStudentsScrapper(anyString(), anyString(), anyString(), anyString())
        `when`(loginFormView.formUsernameValue).thenReturn("@")
        `when`(loginFormView.formPassValue).thenReturn("123456")
        `when`(loginFormView.formHostValue).thenReturn("https://fakelog.cf")
        `when`(loginFormView.formSymbolValue).thenReturn("Default")
        `when`(loginFormView.formHostSymbol).thenReturn("Default")
        presenter.onSignInClick()

        verify(loginFormView).hideSoftKeyboard()
        verify(loginFormView).showProgress(true)
        verify(loginFormView).showProgress(false)
        verify(loginFormView).showContent(false)
        verify(loginFormView).showContent(true)
    }

    @Test
    fun loginEmptyTwiceTest() {
        doReturn(Single.just(emptyList<Student>()))
            .`when`(repository).getStudentsScrapper(anyString(), anyString(), anyString(), anyString())
        `when`(loginFormView.formUsernameValue).thenReturn("@")
        `when`(loginFormView.formPassValue).thenReturn("123456")
        `when`(loginFormView.formHostValue).thenReturn("https://fakelog.cf")
        `when`(loginFormView.formSymbolValue).thenReturn("Default")
        `when`(loginFormView.formHostSymbol).thenReturn("Default")
        presenter.onSignInClick()
        presenter.onSignInClick()

        verify(loginFormView, times(2)).hideSoftKeyboard()
        verify(loginFormView, times(2)).showProgress(true)
        verify(loginFormView, times(2)).showProgress(false)
        verify(loginFormView, times(2)).showContent(false)
        verify(loginFormView, times(2)).showContent(true)
    }

    @Test
    fun loginErrorTest() {
        val testException = RuntimeException("test")
        doReturn(Single.error<List<Student>>(testException)).`when`(repository).getStudentsScrapper(anyString(), anyString(), anyString(), anyString())
        `when`(loginFormView.formUsernameValue).thenReturn("@")
        `when`(loginFormView.formPassValue).thenReturn("123456")
        `when`(loginFormView.formHostValue).thenReturn("https://fakelog.cf")
        `when`(loginFormView.formSymbolValue).thenReturn("Default")
        `when`(loginFormView.formHostSymbol).thenReturn("Default")
        presenter.onSignInClick()

        verify(loginFormView).hideSoftKeyboard()
        verify(loginFormView).showProgress(true)
        verify(loginFormView).showProgress(false)
        verify(loginFormView).showContent(false)
        verify(loginFormView).showContent(true)
        verify(errorHandler).dispatch(testException)
    }
}
