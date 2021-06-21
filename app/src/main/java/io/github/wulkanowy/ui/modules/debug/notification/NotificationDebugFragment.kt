package io.github.wulkanowy.ui.modules.debug.notification

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentDebugNotificationsBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDebugFragment :
    BaseFragment<FragmentDebugNotificationsBinding>(R.layout.fragment_debug_notifications),
    NotificationDebugView, MainView.TitledView {

    @Inject
    lateinit var presenter: NotificationDebugPresenter

    private val notificationDebugAdapter = NotificationDebugAdapter()

    override val titleStringId: Int
        get() = R.string.notification_debug_title

    companion object {
        fun newInstance() = NotificationDebugFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDebugNotificationsBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(binding.recyclerView) {
            adapter = notificationDebugAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun setItems(notificationDebugs: List<NotificationDebugItem>) {
        with(notificationDebugAdapter) {
            items = notificationDebugs
            notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
