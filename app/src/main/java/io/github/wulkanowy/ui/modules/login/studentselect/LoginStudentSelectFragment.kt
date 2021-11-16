package io.github.wulkanowy.ui.modules.login.studentselect

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.FragmentLoginStudentSelectBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.openEmailClient
import io.github.wulkanowy.utils.openInternetBrowser
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class LoginStudentSelectFragment :
    BaseFragment<FragmentLoginStudentSelectBinding>(R.layout.fragment_login_student_select),
    LoginStudentSelectView {

    @Inject
    lateinit var presenter: LoginStudentSelectPresenter

    @Inject
    lateinit var loginAdapter: LoginStudentSelectAdapter

    @Inject
    lateinit var appInfo: AppInfo

    companion object {
        const val SAVED_STUDENTS = "STUDENTS"

        fun newInstance() = LoginStudentSelectFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginStudentSelectBinding.bind(view)
        presenter.onAttachView(this, savedInstanceState?.getSerializable(SAVED_STUDENTS))
    }

    override fun initView() {
        loginAdapter.onClickListener = presenter::onItemSelected

        with(binding) {
            loginStudentSelectSignIn.setOnClickListener { presenter.onSignIn() }
            loginStudentSelectContactDiscord.setOnClickListener { presenter.onDiscordClick() }
            loginStudentSelectContactEmail.setOnClickListener { presenter.onEmailClick() }

            with(loginStudentSelectRecycler) {
                layoutManager = LinearLayoutManager(context)
                adapter = loginAdapter
            }
        }
    }

    override fun updateData(data: List<Pair<StudentWithSemesters, Boolean>>) {
        with(loginAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun openMainView() {
        startActivity(MainActivity.getStartIntent(requireContext()))
        requireActivity().finish()
    }

    override fun showProgress(show: Boolean) {
        binding.loginStudentSelectProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        binding.loginStudentSelectContent.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSignIn(enable: Boolean) {
        binding.loginStudentSelectSignIn.isEnabled = enable
    }

    fun onParentInitStudentSelectFragment(studentsWithSemesters: List<StudentWithSemesters>) {
        presenter.onParentInitStudentSelectView(studentsWithSemesters)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SAVED_STUDENTS, presenter.students as Serializable)
    }

    override fun showContact(show: Boolean) {
        binding.loginStudentSelectContact.visibility = if (show) VISIBLE else GONE
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }

    override fun openDiscordInvite() {
        context?.openInternetBrowser("https://discord.gg/vccAQBr", ::showMessage)
    }

    override fun openEmail(lastError: String) {
        context?.openEmailClient(
            chooserTitle = requireContext().getString(R.string.login_email_intent_title),
            email = "wulkanowyinc@gmail.com",
            subject = requireContext().getString(R.string.login_email_subject),
            body = requireContext().getString(
                R.string.login_email_text, appInfo.systemModel,
                appInfo.systemVersion.toString(),
                appInfo.versionName,
                "Select users to log in",
                lastError
            )
        )
    }
}
