package io.github.wulkanowy.ui.modules.more

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.about.AboutFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.settings.SettingsFragment
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_more.*
import javax.inject.Inject

class MoreFragment : BaseFragment(), MoreView, MainView.TitledView, MainView.MainChildView {

    @Inject
    lateinit var presenter: MorePresenter

    @Inject
    lateinit var moreAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = MoreFragment()
    }

    override val titleStringId: Int
        get() = R.string.more_title

    override val settingsRes: Pair<String, Drawable?>?
        get() {
            return context?.run {
                getString(R.string.settings_title) to
                        ContextCompat.getDrawable(this, R.drawable.ic_more_settings_24dp)
            }
        }

    override val aboutRes: Pair<String, Drawable?>?
        get() {
            return context?.run {
                getString(R.string.about_title) to
                        ContextCompat.getDrawable(this, R.drawable.ic_more_about_24dp)
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        moreAdapter.run { setOnItemClickListener { presenter.onItemSelected(getItem(it)) } }

        moreRecycler.apply {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = moreAdapter
        }
    }

    override fun onFragmentReselected() {
        presenter.onViewReselected()
    }

    override fun updateData(data: List<MoreItem>) {
        moreAdapter.updateDataSet(data)
    }

    override fun openSettingsView() {
        (activity as? MainActivity)?.pushView(SettingsFragment.newInstance())
    }

    override fun openAboutView() {
        (activity as? MainActivity)?.pushView(AboutFragment.newInstance())
    }

    override fun popView() {
        (activity as? MainActivity)?.popView()
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
