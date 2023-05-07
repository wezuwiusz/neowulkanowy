package io.github.wulkanowy.ui.modules.settings.appearance.menuorder

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentMenuOrderBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import javax.inject.Inject


@AndroidEntryPoint
class MenuOrderFragment : BaseFragment<FragmentMenuOrderBinding>(R.layout.fragment_menu_order),
    MenuOrderView, MainView.TitledView {

    @Inject
    lateinit var presenter: MenuOrderPresenter

    @Inject
    lateinit var menuOrderAdapter: MenuOrderAdapter

    override val titleStringId = R.string.menu_order_title

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMenuOrderBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        val itemTouchHelper = ItemTouchHelper(
            MenuItemMoveCallback(menuOrderAdapter, presenter::onDragAndDropEnd)
        )

        itemTouchHelper.attachToRecyclerView(binding.menuOrderRecycler)

        with(binding.menuOrderRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = menuOrderAdapter
            addItemDecoration(MenuOrderDividerItemDecoration(context))
            addItemDecoration(DividerItemDecoration(context))
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            presenter.onBackSelected()
        }

        initializeToolbar()
    }

    private fun initializeToolbar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == android.R.id.home) {
                    presenter.onBackSelected()
                    return true
                }
                return false
            }

        }, viewLifecycleOwner)
    }

    override fun updateData(data: List<MenuOrderItem>) {
        menuOrderAdapter.submitList(data)
    }

    override fun restartApp() {
        startActivity(MainActivity.getStartIntent(requireContext()))
        requireActivity().finishAffinity()
    }

    override fun popView() {
        (activity as? MainActivity?)?.popView()
    }

    override fun showRestartConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.menu_order_confirm_title)
            .setMessage(R.string.menu_order_confirm_content)
            .setPositiveButton(R.string.menu_order_confirm_restart) { _, _ -> presenter.onConfirmRestart() }
            .setNegativeButton(R.string.all_cancel) { _, _ -> presenter.onCancelRestart() }
            .show()
    }
}
