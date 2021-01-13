package io.github.wulkanowy.ui.modules.note

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.databinding.FragmentNoteBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import javax.inject.Inject

@AndroidEntryPoint
class NoteFragment : BaseFragment<FragmentNoteBinding>(R.layout.fragment_note), NoteView,
    MainView.TitledView {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNoteBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        noteAdapter.onClickListener = presenter::onNoteItemSelected

        with(binding.noteRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = noteAdapter
            addItemDecoration(DividerItemDecoration(context))
        }
        with(binding) {
            noteSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
            noteErrorRetry.setOnClickListener { presenter.onRetry() }
            noteErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }
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
        binding.noteEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        binding.noteError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.noteErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.noteProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.noteSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.noteRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showRefresh(show: Boolean) {
        binding.noteSwipe.isRefreshing = show
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
