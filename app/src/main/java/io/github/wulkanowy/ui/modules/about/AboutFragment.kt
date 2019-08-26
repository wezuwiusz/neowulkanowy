package io.github.wulkanowy.ui.modules.about

import android.content.Intent
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.EXTRA_EMAIL
import android.content.Intent.EXTRA_SUBJECT
import android.content.Intent.EXTRA_TEXT
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.about.license.LicenseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.getCompatDrawable
import io.github.wulkanowy.utils.openInternetBrowser
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_about.*
import javax.inject.Inject

class AboutFragment : BaseFragment(), AboutView, MainView.TitledView {

    @Inject
    lateinit var presenter: AboutPresenter

    @Inject
    lateinit var aboutAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    @Inject
    lateinit var appInfo: AppInfo

    override val versionRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_version), "${appInfo.versionName} (${appInfo.versionCode})", getCompatDrawable(R.drawable.ic_all_about))
        }

    override val feedbackRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_feedback), getString(R.string.about_feedback_summary), getCompatDrawable(R.drawable.ic_about_feedback))
        }

    override val discordRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_discord), getString(R.string.about_discord_summary), getCompatDrawable(R.drawable.ic_about_discord))
        }

    override val homepageRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_homepage), getString(R.string.about_homepage_summary), getCompatDrawable(R.drawable.ic_about_homepage))
        }

    override val licensesRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_licenses), getString(R.string.about_licenses_summary), getCompatDrawable(R.drawable.ic_about_licenses))
        }

    override val privacyRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_privacy), getString(R.string.about_privacy_summary), getCompatDrawable(R.drawable.ic_about_privacy))
        }

    override val titleStringId get() = R.string.about_title

    companion object {
        fun newInstance() = AboutFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        aboutAdapter.setOnItemClickListener(presenter::onItemSelected)

        with(aboutRecycler) {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = aboutAdapter
        }
    }

    override fun updateData(header: AboutScrollableHeader, items: List<AboutItem>) {
        with(aboutAdapter) {
            removeAllScrollableHeaders()
            addScrollableHeader(header)
            updateDataSet(items)
        }
    }

    override fun openDiscordInvite() {
        context?.openInternetBrowser("https://discord.gg/vccAQBr", ::showMessage)
    }

    override fun openHomepage() {
        context?.openInternetBrowser("https://wulkanowy.github.io/", ::showMessage)
    }

    override fun openEmailClient() {
        val intent = Intent(ACTION_SENDTO)
            .apply {
                data = Uri.parse("mailto:")
                putExtra(EXTRA_EMAIL, arrayOf("wulkanowyinc@gmail.com"))
                putExtra(EXTRA_SUBJECT, "Zgłoszenie błędu")
                putExtra(EXTRA_TEXT, "Tu umieść treść zgłoszenia\n\n${"-".repeat(40)}\n " +
                    """
                        Build: ${appInfo.versionCode}
                        SDK: ${appInfo.systemVersion}
                        Device: ${appInfo.systemManufacturer} ${appInfo.systemModel}
                    """.trimIndent())
            }

        context?.let {
            if (intent.resolveActivity(it.packageManager) != null) {
                startActivity(Intent.createChooser(intent, getString(R.string.about_feedback)))
            } else {
                it.openInternetBrowser("https://github.com/wulkanowy/wulkanowy/issues", ::showMessage)
            }
        }
    }

    override fun openLicenses() {
        (activity as? MainActivity)?.pushView(LicenseFragment.newInstance())
    }

    override fun openPrivacyPolicy() {
        context?.openInternetBrowser("https://wulkanowy.github.io/polityka-prywatnosci.html", ::showMessage)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
