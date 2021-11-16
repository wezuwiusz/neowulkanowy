package io.github.wulkanowy.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BaseFragmentPagerAdapter(
    private val fragmentManager: FragmentManager,
    private val pagesCount: Int,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle), TabLayoutMediator.TabConfigurationStrategy {

    lateinit var itemFactory: (position: Int) -> Fragment

    var titleFactory: (position: Int) -> String? = { "" }

    var containerId = 0

    fun getFragmentInstance(position: Int): Fragment? {
        require(containerId != 0) { "Container id is 0" }
        return fragmentManager.findFragmentByTag("f$position")
    }

    override fun createFragment(position: Int): Fragment = itemFactory(position)

    override fun getItemCount() = pagesCount

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.text = titleFactory(position)
    }
}
