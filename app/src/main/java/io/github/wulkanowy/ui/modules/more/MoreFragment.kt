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
import io.github.wulkanowy.ui.modules.homework.HomeworkFragment
import io.github.wulkanowy.ui.modules.luckynumber.LuckyNumberFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.mobiledevice.MobileDeviceFragment
import io.github.wulkanowy.ui.modules.note.NoteFragment
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


    override val messagesRes: Pair<String, Drawable?>?
        get() {
            return context?.run {
                getString(R.string.message_title) to
                    ContextCompat.getDrawable(this, R.drawable.ic_more_messages_24dp)
            }
        }

    override val homeworkRes: Pair<String, Drawable?>?
        get() {
            return context?.run {
                getString(R.string.homework_title) to ContextCompat.getDrawable(this, R.drawable.ic_menu_main_homework_24dp)
            }
        }

    override val noteRes: Pair<String, Drawable?>?
        get() {
            return context?.run {
                getString(R.string.note_title) to ContextCompat.getDrawable(this, R.drawable.ic_menu_main_note_24dp)
            }
        }

    override val luckyNumberRes: Pair<String, Drawable?>?
        get() {
            return context?.run {
                getString(R.string.lucky_number_title) to
                    ContextCompat.getDrawable(this, R.drawable.ic_more_lucky_number_24dp)
            }
        }

    override val mobileDevicesRes: Pair<String, Drawable?>?
        get() {
            return context?.run {
                getString(R.string.mobile_devices_title) to
                    ContextCompat.getDrawable(this, R.drawable.ic_menu_main_mobile_devices_24dp)
            }
        }

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
                    ContextCompat.getDrawable(this, R.drawable.ic_all_about_24dp)
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
        moreAdapter.run { setOnItemClickListener { presenter.onItemSelected(it) } }

        moreRecycler.apply {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = moreAdapter
        }
    }

    override fun onFragmentReselected() {
        if (::presenter.isInitialized) presenter.onViewReselected()
    }

    override fun updateData(data: List<MoreItem>) {
        moreAdapter.updateDataSet(data)
    }

    override fun openMessagesView() {
        (activity as? MainActivity)?.pushView(MessageFragment.newInstance())
    }

    override fun openHomeworkView() {
        (activity as? MainActivity)?.pushView(HomeworkFragment.newInstance())
    }

    override fun openNoteView() {
        (activity as? MainActivity)?.pushView(NoteFragment.newInstance())
    }

    override fun openLuckyNumberView() {
        (activity as? MainActivity)?.pushView(LuckyNumberFragment.newInstance())
    }

    override fun openMobileDevicesView() {
        (activity as? MainActivity)?.pushView(MobileDeviceFragment.newInstance())
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
