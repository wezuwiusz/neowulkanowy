package io.github.wulkanowy.ui.modules.mobiledevice

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.databinding.FragmentMobileDeviceBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.mobiledevice.token.MobileDeviceTokenDialog
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import javax.inject.Inject

@AndroidEntryPoint
class MobileDeviceFragment :
    BaseFragment<FragmentMobileDeviceBinding>(R.layout.fragment_mobile_device), MobileDeviceView,
    MainView.TitledView {

    @Inject
    lateinit var presenter: MobileDevicePresenter

    @Inject
    lateinit var devicesAdapter: MobileDeviceAdapter

    companion object {
        fun newInstance() = MobileDeviceFragment()
    }

    override val titleStringId: Int
        get() = R.string.mobile_devices_title

    override val isViewEmpty: Boolean
        get() = devicesAdapter.items.isEmpty()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMobileDeviceBinding.bind(view)
        messageContainer = binding.mobileDevicesRecycler
        presenter.onAttachView(this)
    }

    override fun initView() {
        devicesAdapter.onDeviceUnregisterListener = presenter::onUnregisterDevice

        with(binding.mobileDevicesRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = devicesAdapter
            addItemDecoration(DividerItemDecoration(context))
        }

        with(binding) {
            mobileDevicesSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
            mobileDevicesErrorRetry.setOnClickListener { presenter.onRetry() }
            mobileDevicesErrorDetails.setOnClickListener { presenter.onDetailsClick() }
            mobileDeviceAddButton.setOnClickListener { presenter.onRegisterDevice() }
        }
    }

    override fun updateData(data: List<MobileDevice>) {
        with(devicesAdapter) {
            items = data.toMutableList()
            notifyDataSetChanged()
        }
    }

    override fun deleteItem(device: MobileDevice, position: Int) {
        with(devicesAdapter) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    override fun restoreDeleteItem(device: MobileDevice, position: Int) {
        with(devicesAdapter) {
            items.add(position, device)
            notifyItemInserted(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    override fun showUndo(device: MobileDevice, position: Int) {
        var confirmed = true

        Snackbar.make(binding.mobileDevicesRecycler, getString(R.string.mobile_device_removed), 3000)
            .setAction(R.string.all_undo) {
                confirmed = false
                presenter.onUnregisterCancelled(device, position)
            }.show()

        view?.postDelayed(3000) {
            if (confirmed) presenter.onUnregisterConfirmed(device)
        }
    }

    override fun showRefresh(show: Boolean) {
        binding.mobileDevicesSwipe.isRefreshing = show
    }

    override fun showProgress(show: Boolean) {
        binding.mobileDevicesProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showEmpty(show: Boolean) {
        binding.mobileDevicesEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        binding.mobileDevicesError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.mobileDevicesErrorMessage.text = message
    }

    override fun enableSwipe(enable: Boolean) {
        binding.mobileDevicesSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.mobileDevicesRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showTokenDialog() {
        (activity as? MainActivity)?.showDialogFragment(MobileDeviceTokenDialog.newInstance())
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
