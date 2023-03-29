package io.github.wulkanowy.ui.modules.account.accountquick

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.DialogAccountQuickBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.ui.modules.account.AccountAdapter
import io.github.wulkanowy.ui.modules.account.AccountFragment
import io.github.wulkanowy.ui.modules.account.AccountItem
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.serializable
import javax.inject.Inject

@AndroidEntryPoint
class AccountQuickDialog : BaseDialogFragment<DialogAccountQuickBinding>(), AccountQuickView {

    @Inject
    lateinit var accountAdapter: AccountAdapter

    @Inject
    lateinit var presenter: AccountQuickPresenter

    companion object {

        private const val STUDENTS_ARGUMENT_KEY = "students"

        fun newInstance(studentsWithSemesters: List<StudentWithSemesters>) =
            AccountQuickDialog().apply {
                arguments = bundleOf(STUDENTS_ARGUMENT_KEY to studentsWithSemesters.toTypedArray())
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setView(
                DialogAccountQuickBinding.inflate(layoutInflater)
                    .apply { binding = this }.root
            )
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studentsWithSemesters = requireArguments()
            .serializable<Array<StudentWithSemesters>>(STUDENTS_ARGUMENT_KEY).toList()

        presenter.onAttachView(this, studentsWithSemesters)
    }

    override fun initView() {
        binding.accountQuickDialogManger.setOnClickListener { presenter.onManagerSelected() }

        with(accountAdapter) {
            isAccountQuickDialogMode = true
            onClickListener = presenter::onStudentSelect
        }

        with(binding.accountQuickDialogRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = accountAdapter
        }
    }

    override fun updateData(data: List<AccountItem<*>>) {
        with(accountAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun popView() {
        dismiss()
    }

    override fun recreateMainView() {
        activity?.recreate()
    }

    override fun openAccountView() {
        (requireActivity() as MainActivity).pushView(AccountFragment.newInstance())
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
