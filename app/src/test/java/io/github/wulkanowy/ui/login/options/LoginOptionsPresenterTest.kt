package io.github.wulkanowy.ui.login.options

import io.github.wulkanowy.TestSchedulers
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.SessionRepository
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class LoginOptionsPresenterTest {

    @Mock
    lateinit var errorHandler: ErrorHandler

    @Mock
    lateinit var loginOptionsView: LoginOptionsView

    @Mock
    lateinit var repository: SessionRepository

    private lateinit var presenter: LoginOptionsPresenter

    private val testStudent by lazy { Student(email = "test", password = "test123") }

    private val testException by lazy { RuntimeException("Problem") }

    @Before
    fun initPresenter() {
        MockitoAnnotations.initMocks(this)
        clearInvocations(repository, loginOptionsView)
        presenter = LoginOptionsPresenter(errorHandler, repository, TestSchedulers())
        presenter.attachView(loginOptionsView)
    }

    @Test
    fun initViewTest() {
        verify(loginOptionsView).initRecycler()
    }

    @Test
    fun refreshDataTest() {
        doReturn(Single.just(listOf(testStudent))).`when`(repository).cachedStudents
        presenter.refreshData()
        verify(loginOptionsView).showActionBar(true)
        verify(loginOptionsView).updateData(listOf(LoginOptionsItem(testStudent)))
        verify(repository).clearCache()
    }

    @Test
    fun refreshDataErrorTest() {
        doReturn(Single.error<List<Student>>(testException)).`when`(repository).cachedStudents
        presenter.refreshData()
        verify(loginOptionsView).showActionBar(true)
        verify(errorHandler).proceed(testException)
        verify(repository).clearCache()
    }

    @Test
    fun onSelectedStudentTest() {
        doReturn(Completable.complete()).`when`(repository).saveStudent(testStudent)
        presenter.onSelectStudent(testStudent)
        verify(loginOptionsView).showLoginProgress(true)
        verify(loginOptionsView).openMainView()

    }

    @Test
    fun onSelectedStudentErrorTest() {
        doReturn(Completable.error(testException)).`when`(repository).saveStudent(testStudent)
        presenter.onSelectStudent(testStudent)
        verify(loginOptionsView).showLoginProgress(true)
        verify(errorHandler).proceed(testException)
    }
}
