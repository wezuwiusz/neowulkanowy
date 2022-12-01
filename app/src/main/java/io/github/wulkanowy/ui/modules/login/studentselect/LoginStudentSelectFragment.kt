package io.github.wulkanowy.ui.modules.login.studentselect

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.databinding.FragmentLoginStudentSelectBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.openEmailClient
import io.github.wulkanowy.utils.openInternetBrowser
import io.github.wulkanowy.utils.serializable
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

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    companion object {
        const val ARG_STUDENTS = "STUDENTS"

        fun newInstance(studentsWithSemesters: List<StudentWithSemesters>) =
            LoginStudentSelectFragment().apply {
                arguments = bundleOf(ARG_STUDENTS to studentsWithSemesters)
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginStudentSelectBinding.bind(view)
        presenter.onAttachView(
            view = this,
            students = requireArguments().serializable(ARG_STUDENTS),
        )
    }

    override fun initView() {
        (requireActivity() as LoginActivity).showActionBar(true)

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

    override fun navigateToNext() {
        (requireActivity() as LoginActivity).navigateToNotifications()
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
                R.string.login_email_text,
                "${appInfo.systemManufacturer} ${appInfo.systemModel}",
                appInfo.systemVersion.toString(),
                "${appInfo.versionName}-${appInfo.buildFlavor}",
                "Select users to log in",
                preferencesRepository.installationId,
                lastError
            )
        )
    }
}
