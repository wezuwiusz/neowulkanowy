package io.github.wulkanowy.ui.modules.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import kotlinx.android.synthetic.main.fragment_note.*
import javax.inject.Inject

class NoteFragment : BaseFragment(), NoteView, MainView.TitledView {

    @Inject
    lateinit var presenter: NotePresenter

    @Inject
    lateinit var noteAdapter: NoteAdapter

    companion object {
        fun newInstance() = NoteFragment()
    }

    override val titleStringId: Int
        get() = R.string.note_title

    override val isViewEmpty: Boolean
        get() = noteAdapter.items.isEmpty()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        noteAdapter.onClickListener = presenter::onNoteItemSelected

        noteRecycler.run {
            layoutManager = LinearLayoutManager(context)
            adapter = noteAdapter
            addItemDecoration(DividerItemDecoration(context))
        }
        noteSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
        noteErrorRetry.setOnClickListener { presenter.onRetry() }
        noteErrorDetails.setOnClickListener { presenter.onDetailsClick() }
    }

    override fun showNoteDialog(note: Note) {
        (activity as? MainActivity)?.showDialogFragment(NoteDialog.newInstance(note))
    }

    override fun updateData(data: List<Note>) {
        with(noteAdapter) {
            items = data.toMutableList()
            notifyDataSetChanged()
        }
    }

    override fun updateItem(item: Note, position: Int) {
        with(noteAdapter) {
            items[position] = item
            notifyItemChanged(position)
        }
    }

    override fun clearData() {
        with(noteAdapter) {
            items = mutableListOf()
            notifyDataSetChanged()
        }
    }

    override fun showEmpty(show: Boolean) {
        noteEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        noteError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        noteErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        noteProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        noteSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        noteRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun hideRefresh() {
        noteSwipe.isRefreshing = false
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
