package io.github.wulkanowy.ui.modules.about.license

import com.mikepenz.aboutlibraries.entity.Library
import io.github.wulkanowy.ui.base.BaseView

interface LicenseView : BaseView {

    val appLibraries: List<Library>

    fun initView()

    fun updateData(data: List<Library>)

    fun openLicense(licenseHtml: String)

    fun showProgress(show: Boolean)
}
