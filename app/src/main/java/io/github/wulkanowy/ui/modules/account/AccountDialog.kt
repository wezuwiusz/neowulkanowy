package io.github.wulkanowy.ui.modules.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.DialogAccountBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import javax.inject.Inject

class AccountDialog : BaseDialogFragment<DialogAccountBinding>(), AccountView {

    @Inject
    lateinit var presenter: AccountPresenter

    @Inject
    lateinit var accountAdapter: AccountAdapter

    companion object {
        fun newInstance() = AccountDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DialogAccountBinding.inflate(inflater).apply { binding = this }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        accountAdapter.onClickListener = presenter::onItemSelected

        with(binding) {
            accountDialogAdd.setOnClickListener { presenter.onAddSelected() }
            accountDialogRemove.setOnClickListener { presenter.onRemoveSelected() }
            accountDialogRecycler.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = accountAdapter
            }
        }
    }

    override fun updateData(data: List<AccountItem<*>>) {
        with(accountAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun showError(text: String, error: Throwable) {
        showMessage(text)
    }

    override fun showMessage(text: String) {
        Toast.makeText(context, text, LENGTH_LONG).show()
    }

    override fun dismissView() {
        dismiss()
    }

    override fun openLoginView() {
        activity?.let {
            startActivity(LoginActivity.getStartIntent(it))
        }
    }

    override fun showConfirmDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.account_logout_student)
                .setMessage(R.string.account_confirm)
                .setPositiveButton(R.string.account_logout) { _, _ -> presenter.onLogoutConfirm() }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
        }
    }

    override fun recreateMainView() {
        activity?.recreate()
    }

    override fun onDestroy() {
        presenter.onDetachView()
        super.onDestroy()
    }
}
