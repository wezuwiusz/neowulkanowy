package io.github.wulkanowy.ui.modules.account.accountedit

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.databinding.DialogAccountEditBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.utils.serializable
import javax.inject.Inject

@AndroidEntryPoint
class AccountEditDialog : BaseDialogFragment<DialogAccountEditBinding>(), AccountEditView {

    @Inject
    lateinit var presenter: AccountEditPresenter

    @Inject
    lateinit var accountEditColorAdapter: AccountEditColorAdapter

    companion object {

        private const val ARGUMENT_KEY = "student_with_semesters"

        fun newInstance(student: Student) = AccountEditDialog().apply {
            arguments = bundleOf(ARGUMENT_KEY to student)
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setView(DialogAccountEditBinding.inflate(layoutInflater).apply { binding = this }.root)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this, requireArguments().serializable(ARGUMENT_KEY))
    }

    override fun initView() {
        with(binding) {
            accountEditDetailsCancel.setOnClickListener { dismiss() }
            accountEditDetailsSave.setOnClickListener {
                presenter.changeStudentNickAndAvatar(
                    binding.accountEditDetailsNickText.text.toString(),
                    accountEditColorAdapter.selectedColor
                )
            }

            with(binding.accountEditColors) {
                layoutManager = GridLayoutManager(context, 4)
                adapter = accountEditColorAdapter
            }
        }
    }

    override fun updateSelectedColorData(color: Int) {
        with(accountEditColorAdapter) {
            selectedColor = color
            notifyDataSetChanged()
        }
    }

    override fun updateColorsData(colors: List<Int>) {
        with(accountEditColorAdapter) {
            items = colors
            notifyDataSetChanged()
        }
    }

    override fun showCurrentNick(nick: String) {
        binding.accountEditDetailsNickText.setText(nick)
    }

    override fun popView() {
        dismiss()
    }

    override fun recreateMainView() {
        activity?.recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
