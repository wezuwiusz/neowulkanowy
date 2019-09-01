package io.github.wulkanowy.ui.modules.about.license

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.text.parseAsHtml
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import dagger.Lazy
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_license.*
import javax.inject.Inject

class LicenseFragment : BaseFragment(), LicenseView, MainView.TitledView {

    @Inject
    lateinit var presenter: LicensePresenter

    @Inject
    lateinit var licenseAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    @Inject
    lateinit var libs: Lazy<Libs>

    override val titleStringId get() = R.string.license_title

    override val appLibraries: ArrayList<Library>?
        get() = context?.let {
            libs.get().prepareLibraries(it, emptyArray(), emptyArray(), autoDetect = true, checkCachedDetection = true, sort = true)
        }

    companion object {
        fun newInstance() = LicenseFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_license, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(licenseRecycler) {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = licenseAdapter
        }
        licenseAdapter.setOnItemClickListener(presenter::onItemSelected)
    }

    override fun updateData(data: List<LicenseItem>) {
        licenseAdapter.updateDataSet(data)
    }

    override fun openLicense(licenseHtml: String) {
        context?.let {
            AlertDialog.Builder(it).apply {
                setTitle(R.string.license_dialog_title)
                setMessage(licenseHtml.parseAsHtml())
                setPositiveButton(android.R.string.ok) { _, _ -> }
                show()
            }
        }
    }

    override fun showProgress(show: Boolean) {
        licenseProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
