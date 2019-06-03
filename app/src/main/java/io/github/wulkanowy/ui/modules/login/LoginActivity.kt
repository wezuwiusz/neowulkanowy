package io.github.wulkanowy.ui.modules.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.login.form.LoginFormFragment
import io.github.wulkanowy.ui.modules.login.studentselect.LoginStudentSelectFragment
import io.github.wulkanowy.ui.modules.login.symbol.LoginSymbolFragment
import io.github.wulkanowy.utils.setOnSelectPageListener
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity<LoginPresenter>(), LoginView {

    @Inject
    override lateinit var presenter: LoginPresenter

    @Inject
    lateinit var loginAdapter: BaseFragmentPagerAdapter

    companion object {
        fun getStartIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override val currentViewIndex: Int
        get() = loginViewpager.currentItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        messageContainer = loginContainer

        presenter.onAttachView(this)
    }

    override fun initAdapter() {
        loginAdapter.apply {
            containerId = loginViewpager.id
            addFragments(listOf(
                LoginFormFragment.newInstance(),
                LoginSymbolFragment.newInstance(),
                LoginStudentSelectFragment.newInstance()
            ))
        }

        loginViewpager.run {
            offscreenPageLimit = 2
            adapter = loginAdapter
            setOnSelectPageListener { presenter.onViewSelected(it) }
        }
    }

    override fun switchView(index: Int) {
        loginViewpager.setCurrentItem(index, false)
    }

    override fun showActionBar(show: Boolean) {
        supportActionBar?.apply { if (show) show() else hide() }
    }

    override fun onBackPressed() {
        presenter.onBackPressed { super.onBackPressed() }
    }

    override fun notifyInitSymbolFragment(loginData: Triple<String, String, String>) {
        (loginAdapter.getFragmentInstance(1) as? LoginSymbolFragment)?.onParentInitSymbolFragment(loginData)
    }

    override fun notifyInitStudentSelectFragment(students: List<Student>) {
        (loginAdapter.getFragmentInstance(2) as? LoginStudentSelectFragment)?.onParentInitStudentSelectFragment(students)
    }

    fun onFormFragmentAccountLogged(students: List<Student>, loginData: Triple<String, String, String>) {
        presenter.onFormViewAccountLogged(students, loginData)
    }

    fun onSymbolFragmentAccountLogged(students: List<Student>) {
        presenter.onSymbolViewAccountLogged(students)
    }
}
