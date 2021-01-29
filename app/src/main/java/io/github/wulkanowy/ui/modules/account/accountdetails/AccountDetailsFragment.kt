package io.github.wulkanowy.ui.modules.account.accountdetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.FragmentAccountDetailsBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.studentinfo.StudentInfoFragment
import io.github.wulkanowy.ui.modules.studentinfo.StudentInfoView
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
        arguments?.let {
            presenter.studentWithSemesters =
                it.getSerializable(ARGUMENT_KEY) as StudentWithSemesters
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountDetailsBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        binding.accountDetailsLogout.setOnClickListener { presenter.onRemoveSelected() }
        binding.accountDetailsSelect.setOnClickListener { presenter.onStudentSelect() }
        binding.accountDetailsSelect.isEnabled = !presenter.studentWithSemesters.student.isCurrent

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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.accountDetailsMenuEdit) {
            showAccountEditDetailsDialog()
            return true
        } else false
    }

    override fun showAccountData(studentWithSemesters: StudentWithSemesters) {
        with(binding) {
            accountDetailsName.text = studentWithSemesters.student.studentName
            accountDetailsSchool.text = studentWithSemesters.student.schoolName
        }
    }

    override fun showAccountEditDetailsDialog() {
        (requireActivity() as MainActivity).showDialogFragment(AccountEditDetailsDialog.newInstance())
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

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
