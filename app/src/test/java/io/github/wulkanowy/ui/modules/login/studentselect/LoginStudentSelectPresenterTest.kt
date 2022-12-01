package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.MainCoroutineRule
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant

class LoginStudentSelectPresenterTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @MockK(relaxed = true)
    lateinit var errorHandler: LoginErrorHandler

    @MockK(relaxed = true)
    lateinit var loginStudentSelectView: LoginStudentSelectView

    @MockK
    lateinit var studentRepository: StudentRepository

    @MockK(relaxed = true)
    lateinit var analytics: AnalyticsHelper

    @MockK(relaxed = true)
    lateinit var syncManager: SyncManager

    private lateinit var presenter: LoginStudentSelectPresenter

    private val testStudent by lazy {
        Student(
            email = "test",
            password = "test123",
            scrapperBaseUrl = "https://fakelog.cf",
            loginType = "AUTO",
            symbol = "",
            isCurrent = false,
            studentId = 0,
            schoolName = "",
            schoolSymbol = "",
            classId = 1,
            studentName = "",
            registrationDate = Instant.now(),
            className = "",
            loginMode = "",
            certificateKey = "",
            privateKey = "",
            mobileBaseUrl = "",
            schoolShortName = "",
            userLoginId = 1,
            isParent = false,
            userName = ""
        )
    }

    private val testException by lazy { RuntimeException("Problem") }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        clearMocks(studentRepository, loginStudentSelectView)
        every { loginStudentSelectView.initView() } just Runs
        every { loginStudentSelectView.showContact(any()) } just Runs
        every { loginStudentSelectView.enableSignIn(any()) } just Runs
        every { loginStudentSelectView.showProgress(any()) } just Runs
        every { loginStudentSelectView.showContent(any()) } just Runs

        presenter = LoginStudentSelectPresenter(studentRepository, errorHandler, syncManager, analytics)
        presenter.onAttachView(loginStudentSelectView, emptyList())
    }

    @Test
    fun initViewTest() {
        verify { loginStudentSelectView.initView() }
    }

    @Test
    fun onSelectedStudentTest() {
        coEvery {
            studentRepository.saveStudents(listOf(StudentWithSemesters(testStudent, emptyList())))
        } just Runs

        every { loginStudentSelectView.navigateToNext() } just Runs

        presenter.onItemSelected(StudentWithSemesters(testStudent, emptyList()), false)
        presenter.onSignIn()

        verify { loginStudentSelectView.showContent(false) }
        verify { loginStudentSelectView.showProgress(true) }
        verify { loginStudentSelectView.navigateToNext() }
    }

    @Test
    fun onSelectedStudentErrorTest() {
        coEvery {
            studentRepository.saveStudents(listOf(StudentWithSemesters(testStudent, emptyList())))
        } throws testException

        coEvery { studentRepository.logoutStudent(testStudent) } just Runs

        presenter.onItemSelected(StudentWithSemesters(testStudent, emptyList()), false)
        presenter.onSignIn()

        verify { loginStudentSelectView.showContent(false) }
        verify { loginStudentSelectView.showProgress(true) }
        verify { errorHandler.dispatch(match { testException.message == it.message }) }
    }
}
