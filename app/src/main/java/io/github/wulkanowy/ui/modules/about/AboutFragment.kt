package io.github.wulkanowy.ui.modules.about

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsFragmentCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.withOnExtraListener
import javax.inject.Inject

class AboutFragment : BaseFragment(), AboutView, MainView.TitledView {

    @Inject
    lateinit var presenter: AboutPresenter

    @Inject
    lateinit var fragmentCompat: LibsFragmentCompat

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
                .withAboutSpecial1(getString(R.string.about_source_code))
                .withAboutSpecial2(getString(R.string.about_feedback))
                .withFields(R.string::class.java.fields)
                .withCheckCachedDetection(false)
                .withExcludedLibraries("fastadapter", "AndroidIconics", "gson",
                    "Jsoup", "Retrofit", "okio", "OkHttp")
                .withOnExtraListener { presenter.onExtraSelect(it) })
        }.let {
            fragmentCompat.onCreateView(inflater.context, inflater, container, savedInstanceState, it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentCompat.onViewCreated(view, savedInstanceState)
    }

    override fun openSourceWebView() {
        startActivity(Intent.parseUri("https://github.com/wulkanowy/wulkanowy", 0))
    }

    override fun openIssuesWebView() {
        startActivity(Intent.parseUri("https://github.com/wulkanowy/wulkanowy/issues", 0))
    }

    override fun onDestroyView() {
        fragmentCompat.onDestroyView()
        presenter.onDetachView()
        super.onDestroyView()
    }
}
