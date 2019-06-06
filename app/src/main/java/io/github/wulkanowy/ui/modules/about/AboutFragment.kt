package io.github.wulkanowy.ui.modules.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsFragmentCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.openInternetBrowser
import io.github.wulkanowy.utils.withOnExtraListener
import javax.inject.Inject

class AboutFragment : BaseFragment(), AboutView, MainView.TitledView {

    @Inject
    lateinit var presenter: AboutPresenter

    @Inject
    lateinit var fragmentCompat: LibsFragmentCompat

    @Inject
    lateinit var appInfo: AppInfo

    companion object {
        fun newInstance() = AboutFragment()
    }

    override val titleStringId: Int
        get() = R.string.about_title

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.onAttachView(this)
        return Bundle().apply {
            putSerializable("data", LibsBuilder()
                .withAboutAppName(getString(R.string.app_name))
                .withAboutVersionShown(true)
                .withAboutIconShown(true)
                .withLicenseShown(true)
                .withAboutSpecial1(getString(R.string.about_discord_invite))
                .withAboutSpecial2(getString(R.string.about_homepage))
                .withAboutSpecial3(getString(R.string.about_feedback))
                .withFields(R.string::class.java.fields)
                .withCheckCachedDetection(false)
                .withExcludedLibraries("fastadapter", "AndroidIconics", "Jsoup", "Retrofit", "okio",
                    "Butterknife", "CircleImageView")
                .withOnExtraListener { presenter.onExtraSelect(it) })
        }.let {
            fragmentCompat.onCreateView(inflater.context, inflater, container, savedInstanceState, it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentCompat.onViewCreated(view, savedInstanceState)
    }

    override fun openDiscordInviteView() {
        context?.openInternetBrowser("https://discord.gg/vccAQBr", ::showMessage)
    }

    override fun openHomepageWebView() {
        context?.openInternetBrowser("https://wulkanowy.github.io/", ::showMessage)
    }

    override fun openEmailClientView() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, Array(1) { "wulkanowyinc@gmail.com" })
            putExtra(Intent.EXTRA_SUBJECT, "Zgłoszenie błędu")
            putExtra(Intent.EXTRA_TEXT, "Tu umieść treść zgłoszenia\n\n" + "-".repeat(40) + "\n" + """
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

    override fun onDestroyView() {
        fragmentCompat.onDestroyView()
        presenter.onDetachView()
        super.onDestroyView()
    }
}
