package io.github.wulkanowy.ui.modules.login.options

import io.github.wulkanowy.TestSchedulersProvider
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
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

class LoginOptionsPresenterTest {

    @Mock
    lateinit var errorHandler: LoginErrorHandler

    @Mock
    lateinit var loginOptionsView: LoginOptionsView

    @Mock
    lateinit var studentRepository: StudentRepository

    @Mock
    lateinit var semesterRepository: SemesterRepository

    @Mock
    lateinit var analytics: FirebaseAnalyticsHelper

    private lateinit var presenter: LoginOptionsPresenter

    private val testStudent by lazy { Student(email = "test", password = "test123", endpoint = "https://fakelog.cf", loginType = "AUTO") }

    private val testException by lazy { RuntimeException("Problem") }

    @Before
    fun initPresenter() {
        MockitoAnnotations.initMocks(this)
        clearInvocations(studentRepository, loginOptionsView)
        clearInvocations(semesterRepository, loginOptionsView)
        presenter = LoginOptionsPresenter(errorHandler, studentRepository, semesterRepository, TestSchedulersProvider(), analytics)
        presenter.onAttachView(loginOptionsView)
    }

    @Test
    fun initViewTest() {
        verify(loginOptionsView).initView()
    }

    @Test
    fun refreshDataTest() {
        doReturn(Single.just(listOf(testStudent))).`when`(studentRepository).cachedStudents
        presenter.onParentViewLoadData()
        verify(loginOptionsView).showActionBar(true)
        verify(loginOptionsView).updateData(listOf(LoginOptionsItem(testStudent)))
    }

    @Test
    fun refreshDataErrorTest() {
        doReturn(Single.error<List<Student>>(testException)).`when`(studentRepository).cachedStudents
        presenter.onParentViewLoadData()
        verify(loginOptionsView).showActionBar(true)
        verify(errorHandler).dispatch(testException)
    }

    @Test
    fun onSelectedStudentTest() {
        doReturn(Single.just(1L)).`when`(studentRepository).saveStudent(testStudent)
        doReturn(Single.just(emptyList<Semester>())).`when`(semesterRepository).getSemesters(testStudent, true)
        doReturn(Completable.complete()).`when`(studentRepository).switchStudent(testStudent)
        presenter.onItemSelected(LoginOptionsItem(testStudent))
        verify(loginOptionsView).showContent(false)
        verify(loginOptionsView).showProgress(true)
        verify(loginOptionsView).openMainView()
    }

    @Test
    fun onSelectedStudentErrorTest() {
        doReturn(Single.error<Student>(testException)).`when`(studentRepository).saveStudent(testStudent)
        doReturn(Single.just(emptyList<Semester>())).`when`(semesterRepository).getSemesters(testStudent, true)
        doReturn(Completable.complete()).`when`(studentRepository).logoutStudent(testStudent)
        presenter.onItemSelected(LoginOptionsItem(testStudent))
        verify(loginOptionsView).showContent(false)
        verify(loginOptionsView).showProgress(true)
        verify(errorHandler).dispatch(testException)
    }
}
