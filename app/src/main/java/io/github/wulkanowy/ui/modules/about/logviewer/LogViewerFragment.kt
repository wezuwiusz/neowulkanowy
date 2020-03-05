package io.github.wulkanowy.ui.modules.about.logviewer

import android.content.Intent
import android.content.Intent.EXTRA_EMAIL
import android.content.Intent.EXTRA_STREAM
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wulkanowy.BuildConfig.APPLICATION_ID
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import kotlinx.android.synthetic.main.fragment_logviewer.*
import java.io.File
import javax.inject.Inject

class LogViewerFragment : BaseFragment(), LogViewerView, MainView.TitledView {

    @Inject
    lateinit var presenter: LogViewerPresenter

    private val logAdapter = LogViewerAdapter()

    override val titleStringId: Int
        get() = R.string.logviewer_title

    companion object {
        fun newInstance() = LogViewerFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_logviewer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = logViewerRecycler
        presenter.onAttachView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu_logviewer, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.logViewerMenuShare) presenter.onShareLogsSelected()
        else false
    }

    override fun initView() {
        with(logViewerRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = logAdapter
        }

        logViewRefreshButton.setOnClickListener { presenter.onRefreshClick() }
    }

    override fun setLines(lines: List<String>) {
        logAdapter.lines = lines
        logAdapter.notifyDataSetChanged()
        logViewerRecycler.scrollToPosition(lines.size - 1)
    }

    override fun shareLogs(files: List<File>) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "text/plain"
            putExtra(EXTRA_EMAIL, arrayOf("wulkanowyinc@gmail.com"))
            addFlags(FLAG_GRANT_READ_URI_PERMISSION)
            putParcelableArrayListExtra(EXTRA_STREAM, ArrayList(files.map {
                if (SDK_INT < LOLLIPOP) Uri.fromFile(it)
                else FileProvider.getUriForFile(requireContext(), "$APPLICATION_ID.fileprovider", it)
            }))
        }

        startActivity(Intent.createChooser(intent, getString(R.string.logviewer_share)))
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
