package io.github.wulkanowy.ui.modules.mobiledevice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.mobiledevice.token.MobileDeviceTokenDialog
import kotlinx.android.synthetic.main.fragment_mobile_device.*
import javax.inject.Inject

class MobileDeviceFragment : BaseFragment(), MobileDeviceView, MainView.TitledView {

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mobile_device, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = mobileDevicesRecycler
        presenter.onAttachView(this)
    }

    override fun initView() {
        devicesAdapter.onDeviceUnregisterListener = presenter::onUnregisterDevice

        with(mobileDevicesRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = devicesAdapter
            addItemDecoration(DividerItemDecoration(context))
        }

        mobileDevicesSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
        mobileDevicesErrorRetry.setOnClickListener { presenter.onRetry() }
        mobileDevicesErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        mobileDeviceAddButton.setOnClickListener { presenter.onRegisterDevice() }
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

        Snackbar.make(mobileDevicesRecycler, getString(R.string.mobile_device_removed), 3000)
            .setAction(R.string.all_undo) {
                confirmed = false
                presenter.onUnregisterCancelled(device, position)
            }.show()

        view?.postDelayed(3000) {
            if (confirmed) presenter.onUnregisterConfirmed(device)
        }
    }

    override fun hideRefresh() {
        mobileDevicesSwipe.isRefreshing = false
    }

    override fun showProgress(show: Boolean) {
        mobileDevicesProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showEmpty(show: Boolean) {
        mobileDevicesEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        mobileDevicesError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        mobileDevicesErrorMessage.text = message
    }

    override fun enableSwipe(enable: Boolean) {
        mobileDevicesSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        mobileDevicesRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showTokenDialog() {
        (activity as? MainActivity)?.showDialogFragment(MobileDeviceTokenDialog.newInstance())
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
