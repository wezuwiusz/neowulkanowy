package io.github.wulkanowy.ui.login

import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class LoginPresenterTest {

    @Mock
    lateinit var loginView: LoginView

    @Mock
    lateinit var errorHandler: LoginErrorHandler

    private lateinit var presenter: LoginPresenter

    @Before
    fun initPresenter() {
        MockitoAnnotations.initMocks(this)
        clearInvocations(loginView)
        presenter = LoginPresenter(errorHandler)
        presenter.onAttachView(loginView)
    }

    @Test
    fun initViewTest() {
        verify(loginView).initAdapter()
        verify(loginView).hideActionBar()
    }

    @Test
    fun onPageSelectedTest() {
        presenter.onPageSelected(1)
        verify(loginView).loadOptionsView(1)

        presenter.onPageSelected(0)
        verify(loginView, never()).loadOptionsView(0)
    }

    @Test
    fun onSwitchFragmentTest() {
        presenter.onSwitchFragment(4)
        verify(loginView).switchView(4)
    }

    @Test
    fun onBackPressedTest() {
        clearInvocations(loginView)
        doReturn(1).`when`(loginView).currentViewPosition()
        presenter.onBackPressed { }
        verify(loginView).switchView(0)
        verify(loginView).hideActionBar()
    }

    @Test
    fun onBackPressedDefaultTest() {
        var i = 0
        doReturn(0).`when`(loginView).currentViewPosition()
        presenter.onBackPressed { i++ }
        assertNotEquals(0, i)
    }
}

