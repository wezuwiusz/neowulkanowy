package io.github.wulkanowy.ui.modules.schoolandteachers.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersChildView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersFragment
import io.github.wulkanowy.utils.openDialer
import io.github.wulkanowy.utils.openNavigation
import kotlinx.android.synthetic.main.fragment_school.*
import javax.inject.Inject

class SchoolFragment : BaseFragment(), SchoolView, MainView.TitledView, SchoolAndTeachersChildView {

    @Inject
    lateinit var presenter: SchoolPresenter

    override val titleStringId get() = R.string.school_title

    companion object {
        fun newInstance() = SchoolFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_school, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        schoolSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }

        schoolAddressButton.setOnClickListener { presenter.onAddressSelected() }
        schoolTelephoneButton.setOnClickListener { presenter.onTelephoneSelected() }
    }

    override fun updateData(data: School) {
        schoolName.text = data.name
        schoolAddress.text = data.address.ifBlank { "-" }
        schoolAddressButton.visibility = if (data.address.isNotBlank()) View.VISIBLE else View.GONE
        schoolTelephone.text = data.contact.ifBlank { "-" }
        schoolTelephoneButton.visibility = if (data.contact.isNotBlank()) View.VISIBLE else View.GONE
        schoolHeadmaster.text = data.headmaster
        schoolPedagogue.text = data.pedagogue
    }


    override fun isViewEmpty(): Boolean {
        return schoolName.text.isBlank()
    }

    override fun showEmpty(show: Boolean) {
        schoolEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        schoolProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun enableSwipe(enable: Boolean) {
        schoolSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        schoolContent.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun hideRefresh() {
        schoolSwipe.isRefreshing = false
    }

    override fun notifyParentDataLoaded() {
        (parentFragment as? SchoolAndTeachersFragment)?.onChildFragmentLoaded()
    }

    override fun onParentLoadData(forceRefresh: Boolean) {
        presenter.onParentViewLoadData(forceRefresh)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }

    override fun openMapsLocation(location: String) {
        context?.openNavigation(location)
    }

    override fun dialPhone(phone: String) {
        context?.openDialer(phone)
    }
}
