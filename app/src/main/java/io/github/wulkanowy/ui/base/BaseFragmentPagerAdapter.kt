package io.github.wulkanowy.ui.base

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BaseFragmentPagerAdapter(private val fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val pages = mutableMapOf<Fragment, String?>()

    private var containerId = 0

    fun getFragmentInstance(position: Int): Fragment? {
        return fragmentManager.findFragmentByTag("android:switcher:$containerId:$position")
    }

    fun addFragments(fragments: List<Fragment>) {
        fragments.forEach { pages[it] = null }
    }

    fun addFragmentsWithTitle(pages: Map<Fragment, String>) {
        this.pages.putAll(pages)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        containerId = container.id
        return super.instantiateItem(container, position)
    }

    override fun getItem(position: Int) = pages.keys.elementAt(position)

    override fun getCount() = pages.size

    override fun getPageTitle(position: Int) = pages.values.elementAt(position)
}
