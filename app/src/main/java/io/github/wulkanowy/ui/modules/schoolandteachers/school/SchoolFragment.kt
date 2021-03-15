package io.github.wulkanowy.ui.modules.schoolandteachers.school

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.School
import io.github.wulkanowy.databinding.FragmentSchoolBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersChildView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersFragment
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.openDialer
import io.github.wulkanowy.utils.openNavigation
import javax.inject.Inject

@AndroidEntryPoint
class SchoolFragment : BaseFragment<FragmentSchoolBinding>(R.layout.fragment_school), SchoolView,
    MainView.TitledView, SchoolAndTeachersChildView {

    @Inject
    lateinit var presenter: SchoolPresenter

    override val titleStringId get() = R.string.school_title

    override val isViewEmpty get() = binding.schoolName.text.isBlank()

    companion object {
        fun newInstance() = SchoolFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSchoolBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(binding) {
            schoolSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            schoolSwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            schoolSwipe.setProgressBackgroundColorSchemeColor(requireContext().getThemeAttrColor(R.attr.colorSwipeRefresh))
            schoolErrorRetry.setOnClickListener { presenter.onRetry() }
            schoolErrorDetails.setOnClickListener { presenter.onDetailsClick() }

            schoolAddressButton.setOnClickListener { presenter.onAddressSelected() }
            schoolTelephoneButton.setOnClickListener { presenter.onTelephoneSelected() }
        }
    }

    override fun updateData(data: School) {
        with(binding) {
            val noDataString = getString(R.string.all_no_data)
            schoolName.text = data.name.ifBlank { noDataString }
            schoolAddress.text = data.address.ifBlank { noDataString }
            schoolAddressButton.visibility = if (data.address.isNotBlank()) VISIBLE else GONE
            schoolTelephone.text = data.contact.ifBlank { noDataString }
            schoolTelephoneButton.visibility = if (data.contact.isNotBlank()) VISIBLE else GONE
            schoolHeadmaster.text = data.headmaster.ifBlank { noDataString }
            schoolPedagogue.text = data.pedagogue.ifBlank { noDataString }
        }
    }

    override fun showEmpty(show: Boolean) {
        binding.schoolEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        binding.schoolError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.schoolErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.schoolProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.schoolSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.schoolContent.visibility = if (show) VISIBLE else GONE
    }

    override fun hideRefresh() {
        binding.schoolSwipe.isRefreshing = false
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
