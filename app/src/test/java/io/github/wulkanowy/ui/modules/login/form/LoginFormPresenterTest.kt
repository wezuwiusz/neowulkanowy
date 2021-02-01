package io.github.wulkanowy.ui.modules.login.form

import io.github.wulkanowy.MainCoroutineRule
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.time.LocalDateTime.now

class LoginFormPresenterTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @MockK(relaxed = true)
    lateinit var loginFormView: LoginFormView

    @MockK
    lateinit var repository: StudentRepository

    @MockK(relaxed = true)
    lateinit var errorHandler: LoginErrorHandler

    @MockK(relaxed = true)
    lateinit var analytics: AnalyticsHelper

    private lateinit var presenter: LoginFormPresenter

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { loginFormView.initView() } just Runs
        every { loginFormView.showContact(any()) } just Runs
        every { loginFormView.showVersion() } just Runs
        every { loginFormView.showProgress(any()) } just Runs
        every { loginFormView.showContent(any()) } just Runs
        every { loginFormView.formHostSymbol } returns "Default"
        every { loginFormView.setErrorPassInvalid(any()) } just Runs
        every { loginFormView.setErrorPassRequired(any()) } just Runs
        every { loginFormView.setErrorUsernameRequired() } just Runs

        presenter = LoginFormPresenter(repository, errorHandler, analytics)
        presenter.onAttachView(loginFormView)
    }

    @Test
    fun initViewTest() {
        verify { loginFormView.initView() }
    }

    @Test
    fun emptyNicknameLoginTest() {
        every { loginFormView.formUsernameValue } returns ""
        every { loginFormView.formPassValue } returns "test123"
        every { loginFormView.formHostValue } returns "https://fakelog.cf/?standard"
        presenter.onSignInClick()

        verify { loginFormView.setErrorUsernameRequired() }
        verify(exactly = 0) { loginFormView.setErrorPassRequired(true) }
        verify(exactly = 0) { loginFormView.setErrorPassInvalid(false) }
    }

    @Test
    fun emptyPassLoginTest() {
        every { loginFormView.formUsernameValue } returns "@"
        every { loginFormView.formPassValue } returns ""
        every { loginFormView.formHostValue } returns "https://fakelog.cf/?standard"
        presenter.onSignInClick()

        verify(exactly = 0) { loginFormView.setErrorUsernameRequired() }
        verify { loginFormView.setErrorPassRequired(true) }
        verify(exactly = 0) { loginFormView.setErrorPassInvalid(false) }
    }

    @Test
    fun invalidPassLoginTest() {
        every { loginFormView.formUsernameValue } returns "@"
        every { loginFormView.formPassValue } returns "123"
        every { loginFormView.formHostValue } returns "https://fakelog.cf/?standard"
        presenter.onSignInClick()

        verify(exactly = 0) { loginFormView.setErrorUsernameRequired() }
        verify(exactly = 0) { loginFormView.setErrorPassRequired(true) }
        verify { loginFormView.setErrorPassInvalid(true) }
    }

    @Test
    fun loginTest() {
        val studentTest = Student(
            email = "test@",
            password = "123",
            scrapperBaseUrl = "https://fakelog.cf/",
            loginType = "AUTO",
            studentName = "",
            schoolSymbol = "",
            schoolName = "",
            studentId = 0,
            classId = 1,
            isCurrent = false,
            symbol = "",
            registrationDate = now(),
            className = "",
            mobileBaseUrl = "",
            privateKey = "",
            certificateKey = "",
            loginMode = "",
            userLoginId = 0,
            schoolShortName = "",
            isParent = false,
            userName = ""
        )
        coEvery { repository.getStudentsScrapper(any(), any(), any(), any()) } returns listOf(
            StudentWithSemesters(studentTest, emptyList())
        )

        every { loginFormView.formUsernameValue } returns "@"
        every { loginFormView.formPassValue } returns "123456"
        every { loginFormView.formHostValue } returns "https://fakelog.cf/?standard"
        every { loginFormView.formHostSymbol } returns "Default"
        presenter.onSignInClick()

        verify { loginFormView.hideSoftKeyboard() }
        verify { loginFormView.showProgress(true) }
        verify { loginFormView.showProgress(false) }
        verify { loginFormView.showContent(false) }
        verify { loginFormView.showContent(true) }
    }

    @Test
    fun loginEmptyTest() {
        coEvery { repository.getStudentsScrapper(any(), any(), any(), any()) } returns listOf()
        every { loginFormView.formUsernameValue } returns "@"
        every { loginFormView.formPassValue } returns "123456"
        every { loginFormView.formHostValue } returns "https://fakelog.cf/?standard"
        every { loginFormView.formHostSymbol } returns "Default"
        presenter.onSignInClick()

        verify { loginFormView.showContent(false) }
        verify { loginFormView.showProgress(true) }
        verify { loginFormView.showProgress(false) }
        verify { loginFormView.showContent(false) }
        verify { loginFormView.showContent(true) }
    }

    @Test
    fun loginEmptyTwiceTest() {
        coEvery { repository.getStudentsScrapper(any(), any(), any(), any()) } returns listOf()
        every { loginFormView.formUsernameValue } returns "@"
        every { loginFormView.formPassValue } returns "123456"
        every { loginFormView.formHostValue } returns "https://fakelog.cf/?standard"
        every { loginFormView.formHostSymbol } returns "Default"
        presenter.onSignInClick()
        presenter.onSignInClick()

        verify(exactly = 2) { loginFormView.hideSoftKeyboard() }
        verify(exactly = 2) { loginFormView.showProgress(true) }
        verify(exactly = 2) { loginFormView.showProgress(false) }
        verify(exactly = 2) { loginFormView.showContent(false) }
        verify(exactly = 2) { loginFormView.showContent(true) }
    }

    @Test
    fun loginErrorTest() {
        val testException = IOException("test")
        coEvery { repository.getStudentsScrapper(any(), any(), any(), any()) } throws testException
        every { loginFormView.formUsernameValue } returns "@"
        every { loginFormView.formPassValue } returns "123456"
        every { loginFormView.formHostValue } returns "https://fakelog.cf/?standard"
        every { loginFormView.formHostSymbol } returns "Default"
        every { loginFormView.showProgress(any()) } just Runs
        every { loginFormView.showProgress(any()) } just Runs
        presenter.onSignInClick()

        verify { loginFormView.hideSoftKeyboard() }
        verify { loginFormView.showProgress(false) }
        verify { loginFormView.showContent(false) }
        verify { loginFormView.showContent(true) }
        verify { errorHandler.dispatch(match { it.message == testException.message }) }
    }
}
