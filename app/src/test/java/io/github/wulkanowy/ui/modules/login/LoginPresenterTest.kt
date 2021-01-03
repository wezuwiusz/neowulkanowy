package io.github.wulkanowy.ui.modules.login

import io.github.wulkanowy.data.repositories.StudentRepository
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class LoginPresenterTest {

    @MockK(relaxed = true)
    lateinit var loginView: LoginView

    @MockK(relaxed = true)
    lateinit var errorHandler: LoginErrorHandler

    @MockK
    lateinit var studentRepository: StudentRepository

    private lateinit var presenter: LoginPresenter

    @Before
    fun initPresenter() {
        MockKAnnotations.init(this)
        clearMocks(loginView)

        presenter = LoginPresenter(errorHandler, studentRepository)
        presenter.onAttachView(loginView)
    }

    @Test
    fun initViewTest() {
        verify { loginView.initView() }
        verify { loginView.showActionBar(false) }
    }

    @Test
    fun onBackPressedTest() {
        clearMocks(loginView)
        every { loginView.currentViewIndex } returns 1
        presenter.onBackPressed { }
        verify { loginView.switchView(0) }
    }

    @Test
    fun onBackPressedDefaultTest() {
        var i = 0
        every { loginView.currentViewIndex } returns 0
        presenter.onBackPressed { i++ }
        assertNotEquals(0, i)
    }
}

