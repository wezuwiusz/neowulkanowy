package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.TestSchedulersProvider
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDateTime.now

class LoginStudentSelectPresenterTest {

    @Mock
    lateinit var errorHandler: LoginErrorHandler

    @Mock
    lateinit var loginStudentSelectView: LoginStudentSelectView

    @Mock
    lateinit var studentRepository: StudentRepository

    @Mock
    lateinit var semesterRepository: SemesterRepository

    @Mock
    lateinit var analytics: FirebaseAnalyticsHelper

    private lateinit var presenter: LoginStudentSelectPresenter

    private val testStudent by lazy { Student(email = "test", password = "test123", endpoint = "https://fakelog.cf", loginType = "AUTO", symbol = "", isCurrent = false, studentId = 0, schoolName = "", schoolSymbol = "", studentName = "", registrationDate = now()) }

    private val testException by lazy { RuntimeException("Problem") }

    @Before
    fun initPresenter() {
        MockitoAnnotations.initMocks(this)
        clearInvocations(studentRepository, loginStudentSelectView)
        clearInvocations(semesterRepository, loginStudentSelectView)
        presenter = LoginStudentSelectPresenter(errorHandler, studentRepository, semesterRepository, TestSchedulersProvider(), analytics)
        presenter.onAttachView(loginStudentSelectView, null)
    }

    @Test
    fun initViewTest() {
        verify(loginStudentSelectView).initView()
    }

    @Test
    fun onSelectedStudentTest() {
        doReturn(Single.just(1L)).`when`(studentRepository).saveStudent(testStudent)
        doReturn(Single.just(emptyList<Semester>())).`when`(semesterRepository).getSemesters(testStudent, true)
        doReturn(Completable.complete()).`when`(studentRepository).switchStudent(testStudent)
        presenter.onItemSelected(LoginStudentSelectItem(testStudent))
        verify(loginStudentSelectView).showContent(false)
        verify(loginStudentSelectView).showProgress(true)
        verify(loginStudentSelectView).openMainView()
    }

    @Test
    fun onSelectedStudentErrorTest() {
        doReturn(Single.error<Student>(testException)).`when`(studentRepository).saveStudent(testStudent)
        doReturn(Single.just(emptyList<Semester>())).`when`(semesterRepository).getSemesters(testStudent, true)
        doReturn(Completable.complete()).`when`(studentRepository).logoutStudent(testStudent)
        presenter.onItemSelected(LoginStudentSelectItem(testStudent))
        verify(loginStudentSelectView).showContent(false)
        verify(loginStudentSelectView).showProgress(true)
        verify(errorHandler).dispatch(testException)
    }
}
