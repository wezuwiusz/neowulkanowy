package io.github.wulkanowy.ui.modules.about.license

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentLicenseBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

@AndroidEntryPoint
class LicenseFragment : BaseFragment<FragmentLicenseBinding>(R.layout.fragment_license),
    LicenseView, MainView.TitledView {

    @Inject
    lateinit var presenter: LicensePresenter

    @Inject
    lateinit var licenseAdapter: LicenseAdapter

    private val libs by lazy { Libs(requireContext()) }

    override val titleStringId get() = R.string.license_title

    override val appLibraries: ArrayList<Library>?
        get() = context?.let {
            libs.prepareLibraries(it, emptyArray(), emptyArray(), autoDetect = true, checkCachedDetection = true, sort = true)
        }

    companion object {
        fun newInstance() = LicenseFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLicenseBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        licenseAdapter.onClickListener = presenter::onItemSelected

        with(binding.licenseRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = licenseAdapter
        }
    }

    override fun updateData(data: List<Library>) {
        with(licenseAdapter) {
            items = data
            notifyDataSetChanged()
        }
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
        binding.licenseProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
