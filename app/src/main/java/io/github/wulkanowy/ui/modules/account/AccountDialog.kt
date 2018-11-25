package io.github.wulkanowy.ui.modules.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import dagger.android.support.DaggerAppCompatDialogFragment
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.dialog_account.*
import javax.inject.Inject

class AccountDialog : DaggerAppCompatDialogFragment(), AccountView {

    @Inject
    lateinit var presenter: AccountPresenter

    @Inject
    lateinit var accountAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = AccountDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        accountAdapter.setOnItemClickListener { presenter.onItemSelected(it) }

        accountDialogAdd.setOnClickListener { presenter.onAddSelected() }
        accountDialogRemove.setOnClickListener { presenter.onRemoveSelected() }
        accountDialogRecycler.apply {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = accountAdapter
        }
    }

    override fun updateData(data: List<AccountItem>) {
        accountAdapter.updateDataSet(data)
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
        activity?.also {
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

    override fun recreateView() {
        activity?.also {
            startActivity(MainActivity.getStartIntent(it))
            it.finish()
        }
    }

    override fun onDestroy() {
        presenter.onDetachView()
        super.onDestroy()
    }
}

