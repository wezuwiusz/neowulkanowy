package io.github.wulkanowy.ui.modules.login

import io.github.wulkanowy.TestSchedulersProvider
import io.github.wulkanowy.data.repositories.student.StudentRepository
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class LoginPresenterTest {

    @Mock
    lateinit var loginView: LoginView

    @Mock
    lateinit var errorHandler: LoginErrorHandler

    @Mock
    lateinit var studentRepository: StudentRepository

    private lateinit var presenter: LoginPresenter

    @Before
    fun initPresenter() {
        MockitoAnnotations.initMocks(this)
        clearInvocations(loginView)

        presenter = LoginPresenter(TestSchedulersProvider(), errorHandler, studentRepository)
        presenter.onAttachView(loginView)
    }

    @Test
    fun initViewTest() {
        verify(loginView).initAdapter()
        verify(loginView).showActionBar(false)
    }

    @Test
    fun onBackPressedTest() {
        clearInvocations(loginView)
        doReturn(1).`when`(loginView).currentViewIndex
        presenter.onBackPressed { }
        verify(loginView).switchView(0)
    }

    @Test
    fun onBackPressedDefaultTest() {
        var i = 0
        doReturn(0).`when`(loginView).currentViewIndex
        presenter.onBackPressed { i++ }
        assertNotEquals(0, i)
    }
}

