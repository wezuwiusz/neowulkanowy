package io.github.wulkanowy.ui.modules.account.accountedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DialogAccountEditBinding.inflate(inflater).apply { binding = this }.root

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
