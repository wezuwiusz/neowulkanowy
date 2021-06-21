package io.github.wulkanowy.ui.modules.debug.logviewer

import android.content.Intent
import android.content.Intent.EXTRA_EMAIL
import android.content.Intent.EXTRA_STREAM
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.BuildConfig.APPLICATION_ID
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentLogviewerBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class LogViewerFragment : BaseFragment<FragmentLogviewerBinding>(R.layout.fragment_logviewer),
    LogViewerView, MainView.TitledView {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLogviewerBinding.bind(view)
        messageContainer = binding.logViewerRecycler
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
        with(binding.logViewerRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = logAdapter
        }

        binding.logViewRefreshButton.setOnClickListener { presenter.onRefreshClick() }
    }

    override fun setLines(lines: List<String>) {
        logAdapter.lines = lines
        logAdapter.notifyDataSetChanged()
        binding.logViewerRecycler.scrollToPosition(lines.size - 1)
    }

    override fun shareLogs(files: List<File>) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "text/plain"
            putExtra(EXTRA_EMAIL, arrayOf("wulkanowyinc@gmail.com"))
            addFlags(FLAG_GRANT_READ_URI_PERMISSION)
            putParcelableArrayListExtra(EXTRA_STREAM, ArrayList(files.map {
                FileProvider.getUriForFile(requireContext(), "$APPLICATION_ID.fileprovider", it)
            }))
        }

        startActivity(Intent.createChooser(intent, getString(R.string.logviewer_share)))
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
