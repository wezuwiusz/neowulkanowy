package io.github.wulkanowy.ui.modules.account.accountquick

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.databinding.DialogAccountQuickBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.ui.modules.account.AccountAdapter
import io.github.wulkanowy.ui.modules.account.AccountFragment
import io.github.wulkanowy.ui.modules.account.AccountItem
import io.github.wulkanowy.ui.modules.main.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class AccountQuickDialog : BaseDialogFragment<DialogAccountQuickBinding>(), AccountQuickView {

    @Inject
    lateinit var accountAdapter: AccountAdapter

    @Inject
    lateinit var presenter: AccountQuickPresenter

    companion object {
        fun newInstance() = AccountQuickDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogAccountQuickBinding.inflate(inflater).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.onAttachView(this)
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
