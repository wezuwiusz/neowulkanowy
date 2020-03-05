package io.github.wulkanowy.ui.modules.about.logviewer

import io.github.wulkanowy.ui.base.BaseView
import java.io.File

interface LogViewerView : BaseView {

    fun initView()

    fun setLines(lines: List<String>)

    fun shareLogs(files: List<File>)
}
