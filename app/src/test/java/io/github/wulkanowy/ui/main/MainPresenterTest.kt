package io.github.wulkanowy.ui.main

import io.github.wulkanowy.data.ErrorHandler
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class MainPresenterTest {

    @Mock
    lateinit var errorHandler: ErrorHandler

    @Mock
    lateinit var mainView: MainView

    private lateinit var presenter: MainPresenter

    @Before
    fun initPresenter() {
        MockitoAnnotations.initMocks(this)
        clearInvocations(mainView)

        presenter = MainPresenter(errorHandler)
        presenter.onAttachView(mainView)
    }

    @Test
    fun initMenuTest() {
        verify(mainView).initView()
    }

    @Test
    fun onTabSelectedTest() {
        presenter.onTabSelected(1, false)
        verify(mainView).switchMenuView(1)
    }

    @Test
    fun onMenuFragmentChangeTest() {
        doReturn("Test").`when`(mainView).viewTitle(1)
        presenter.onMenuViewChange(1)
        verify(mainView).setViewTitle("Test")
    }
}

