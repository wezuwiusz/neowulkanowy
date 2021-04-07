package io.github.wulkanowy.ui.modules.about

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentAboutBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.about.contributor.ContributorFragment
import io.github.wulkanowy.ui.modules.about.license.LicenseFragment
import io.github.wulkanowy.ui.modules.about.logviewer.LogViewerFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.getCompatDrawable
import io.github.wulkanowy.utils.openAppInMarket
import io.github.wulkanowy.utils.openEmailClient
import io.github.wulkanowy.utils.openInternetBrowser
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.toLocalDateTime
import javax.inject.Inject
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>(R.layout.fragment_about), AboutView,
    MainView.TitledView {

    @Inject
    lateinit var presenter: AboutPresenter

    @Inject
    lateinit var aboutAdapter: AboutAdapter

    @Inject
    lateinit var appInfo: AppInfo

    override val versionRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            val buildTimestamp = appInfo.buildTimestamp.toLocalDateTime().toFormattedString("yyyy-MM-dd")
            val versionSignature = "${appInfo.versionName}-${appInfo.buildFlavor} (${appInfo.versionCode}), $buildTimestamp"
            Triple(getString(R.string.about_version), versionSignature, getCompatDrawable(R.drawable.ic_all_about))
        }

    override val creatorsRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_contributor), getString(R.string.about_contributor_summary), getCompatDrawable(R.drawable.ic_about_creator))
        }

    override val feedbackRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_feedback), getString(R.string.about_feedback_summary), getCompatDrawable(R.drawable.ic_about_feedback))
        }

    override val faqRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_faq), getString(R.string.about_faq_summary), getCompatDrawable(R.drawable.ic_about_faq))
        }

    override val discordRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_discord), getString(R.string.about_discord_summary), getCompatDrawable(R.drawable.ic_about_discord))
        }

    override val facebookRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_facebook), getString(R.string.about_facebook_summary), getCompatDrawable(R.drawable.ic_about_facebook))
        }

    override val homepageRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(
                getString(R.string.about_homepage),
                getString(R.string.about_homepage_summary),
                getCompatDrawable(R.drawable.ic_all_home)
            )
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAboutBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        aboutAdapter.onClickListener = presenter::onItemSelected

        with(binding.aboutRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = aboutAdapter
        }
    }

    override fun updateData(data: List<Triple<String, String, Drawable?>>) {
        with(aboutAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun openAppInMarket() {
        context?.openAppInMarket(::showMessage)
    }

    override fun openLogViewer() {
        (activity as? MainActivity)?.pushView(LogViewerFragment.newInstance())
    }

    override fun openDiscordInvite() {
        context?.openInternetBrowser("https://discord.gg/vccAQBr", ::showMessage)
    }

    override fun openFacebookPage() {
        context?.openInternetBrowser("https://www.facebook.com/wulkanowy", ::showMessage)
    }

    override fun openHomepage() {
        context?.openInternetBrowser("https://wulkanowy.github.io/", ::showMessage)
    }

    override fun openEmailClient() {
        requireContext().openEmailClient(
            chooserTitle = getString(R.string.about_feedback),
            email = "wulkanowyinc@gmail.com",
            subject = "Zgłoszenie błędu",
            body = getString(
                R.string.about_feedback_template,
                "${appInfo.systemManufacturer} ${appInfo.systemModel}",
                appInfo.systemVersion.toString(),
                "${appInfo.versionName}-${appInfo.buildFlavor}"
            ),
            onActivityNotFound = {
                requireContext().openInternetBrowser(
                    "https://github.com/wulkanowy/wulkanowy/issues",
                    ::showMessage
                )
            }
        )
    }

    override fun openFaqPage() {
        context?.openInternetBrowser("https://wulkanowy.github.io/czesto-zadawane-pytania", ::showMessage)
    }

    override fun openLicenses() {
        (activity as? MainActivity)?.pushView(LicenseFragment.newInstance())
    }

    override fun openCreators() {
        (activity as? MainActivity)?.pushView(ContributorFragment.newInstance())
    }

    override fun openPrivacyPolicy() {
        context?.openInternetBrowser("https://wulkanowy.github.io/polityka-prywatnosci.html", ::showMessage)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
