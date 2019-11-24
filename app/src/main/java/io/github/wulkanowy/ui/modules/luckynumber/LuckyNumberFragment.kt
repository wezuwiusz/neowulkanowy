package io.github.wulkanowy.ui.modules.luckynumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import kotlinx.android.synthetic.main.fragment_lucky_number.*
import javax.inject.Inject

class LuckyNumberFragment : BaseFragment(), LuckyNumberView, MainView.TitledView {

    @Inject
    lateinit var presenter: LuckyNumberPresenter

    companion object {
        fun newInstance() = LuckyNumberFragment()
    }

    override val titleStringId: Int
        get() = R.string.lucky_number_title

    override val isViewEmpty get() = luckyNumberText.text.isBlank()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lucky_number, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = luckyNumberSwipe
        presenter.onAttachView(this)
    }

    override fun initView() {
        luckyNumberSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
        luckyNumberErrorRetry.setOnClickListener { presenter.onRetry() }
        luckyNumberErrorDetails.setOnClickListener { presenter.onDetailsClick() }
    }

    override fun updateData(data: LuckyNumber) {
        luckyNumberText.text = data.luckyNumber.toString()
    }

    override fun hideRefresh() {
        luckyNumberSwipe.isRefreshing = false
    }

    override fun showEmpty(show: Boolean) {
        luckyNumberEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        luckyNumberError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        luckyNumberErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        luckyNumberProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        luckyNumberSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        luckyNumberContent.visibility = if (show) VISIBLE else GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
