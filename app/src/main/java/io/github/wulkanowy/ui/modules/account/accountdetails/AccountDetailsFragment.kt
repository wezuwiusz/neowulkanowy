package io.github.wulkanowy.ui.modules.account.accountdetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.FragmentAccountDetailsBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.account.accountedit.AccountEditDialog
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.studentinfo.StudentInfoFragment
import io.github.wulkanowy.ui.modules.studentinfo.StudentInfoView
import io.github.wulkanowy.utils.createNameInitialsDrawable
import io.github.wulkanowy.utils.nickOrName
import javax.inject.Inject

@AndroidEntryPoint
class AccountDetailsFragment :
    BaseFragment<FragmentAccountDetailsBinding>(R.layout.fragment_account_details),
    AccountDetailsView, MainView.TitledView {

    @Inject
    lateinit var presenter: AccountDetailsPresenter

    override val titleStringId = R.string.account_details_title

    override var subtitleString = ""

    companion object {

        private const val ARGUMENT_KEY = "Data"

        fun newInstance(studentWithSemesters: StudentWithSemesters) =
            AccountDetailsFragment().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, studentWithSemesters) }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountDetailsBinding.bind(view)
        presenter.onAttachView(this, requireArguments()[ARGUMENT_KEY] as StudentWithSemesters)
    }

    override fun initView() {
        binding.accountDetailsErrorRetry.setOnClickListener { presenter.onRetry() }
        binding.accountDetailsErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        binding.accountDetailsLogout.setOnClickListener { presenter.onRemoveSelected() }
        binding.accountDetailsSelect.setOnClickListener { presenter.onStudentSelect() }

        binding.accountDetailsPersonalData.setOnClickListener {
            presenter.onStudentInfoSelected(StudentInfoView.Type.PERSONAL)
        }
        binding.accountDetailsAddressData.setOnClickListener {
            presenter.onStudentInfoSelected(StudentInfoView.Type.ADDRESS)
        }
        binding.accountDetailsContactData.setOnClickListener {
            presenter.onStudentInfoSelected(StudentInfoView.Type.CONTACT)
        }
        binding.accountDetailsFamilyData.setOnClickListener {
            presenter.onStudentInfoSelected(StudentInfoView.Type.FAMILY)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu[0].isVisible = false
        inflater.inflate(R.menu.action_menu_account_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.accountDetailsMenuEdit) {
            presenter.onAccountEditSelected()
            true
        } else false
    }

    override fun showAccountData(student: Student) {
        with(binding) {
            accountDetailsCheck.isVisible = student.isCurrent
            accountDetailsName.text = student.nickOrName
            accountDetailsSchool.text = student.schoolName
            accountDetailsAvatar.setImageDrawable(
                requireContext().createNameInitialsDrawable(
                    student.nickOrName,
                    student.avatarColor
                )
            )
        }
    }

    override fun enableSelectStudentButton(enable: Boolean) {
        binding.accountDetailsSelect.isEnabled = enable
    }

    override fun showAccountEditDetailsDialog(student: Student) {
        (requireActivity() as MainActivity).showDialogFragment(
            AccountEditDialog.newInstance(student)
        )
    }

    override fun showLogoutConfirmDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.account_logout_student)
                .setMessage(R.string.account_confirm)
                .setPositiveButton(R.string.account_logout) { _, _ -> presenter.onLogoutConfirm() }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
        }
    }

    override fun popView() {
        (requireActivity() as MainActivity).popView(2)
    }

    override fun recreateMainView() {
        requireActivity().recreate()
    }

    override fun openStudentInfoView(
        infoType: StudentInfoView.Type,
        studentWithSemesters: StudentWithSemesters
    ) {
        (requireActivity() as MainActivity).pushView(
            StudentInfoFragment.newInstance(
                infoType,
                studentWithSemesters
            )
        )
    }

    override fun showErrorView(show: Boolean) {
        binding.accountDetailsError.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setErrorDetails(message: String) {
        binding.accountDetailsErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.accountDetailsProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        binding.accountDetailsContent.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
