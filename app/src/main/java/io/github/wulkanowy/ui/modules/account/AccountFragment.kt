package io.github.wulkanowy.ui.modules.account

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.FragmentAccountBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.account.accountdetails.AccountDetailsFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>(R.layout.fragment_account),
    AccountView, MainView.TitledView {

    @Inject
    lateinit var presenter: AccountPresenter

    @Inject
    lateinit var accountAdapter: AccountAdapter

    companion object {

        fun newInstance() = AccountFragment()
    }

    override val titleStringId = R.string.account_title

    override var subtitleString = ""

    override val isViewEmpty get() = accountAdapter.items.isEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        binding.accountErrorRetry.setOnClickListener { presenter.onRetry() }
        binding.accountErrorDetails.setOnClickListener { presenter.onDetailsClick() }

        binding.accountRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = accountAdapter
        }

        accountAdapter.onClickListener = presenter::onItemSelected

        with(binding) {
            accountAdd.setOnClickListener { presenter.onAddSelected() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu[0].isVisible = false
    }

    override fun updateData(data: List<AccountItem<*>>) {
        with(accountAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun openLoginView() {
        activity?.let {
            startActivity(LoginActivity.getStartIntent(it))
        }
    }

    override fun openAccountDetailsView(studentWithSemesters: StudentWithSemesters) {
        (activity as? MainActivity)?.pushView(
            AccountDetailsFragment.newInstance(
                studentWithSemesters
            )
        )
    }

    override fun showErrorView(show: Boolean) {
        binding.accountError.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setErrorDetails(message: String) {
        binding.accountErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.accountProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        with(binding) {
            accountRecycler.visibility = if (show) View.VISIBLE else View.GONE
            accountAdd.visibility = if (show) View.VISIBLE else View.GONE
        }
    }
}
