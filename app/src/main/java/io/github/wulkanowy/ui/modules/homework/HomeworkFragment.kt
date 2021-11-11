package io.github.wulkanowy.ui.modules.homework

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.databinding.FragmentHomeworkBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.homework.add.HomeworkAddDialog
import io.github.wulkanowy.ui.modules.homework.details.HomeworkDetailsDialog
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.getThemeAttrColor
import javax.inject.Inject

@AndroidEntryPoint
class HomeworkFragment : BaseFragment<FragmentHomeworkBinding>(R.layout.fragment_homework),
    HomeworkView, MainView.TitledView {

    @Inject
    lateinit var presenter: HomeworkPresenter

    @Inject
    lateinit var homeworkAdapter: HomeworkAdapter

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"

        fun newInstance() = HomeworkFragment()
    }

    override val titleStringId get() = R.string.homework_title

    override val isViewEmpty get() = homeworkAdapter.items.isEmpty()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeworkBinding.bind(view)
        messageContainer = binding.homeworkRecycler
        presenter.onAttachView(this, savedInstanceState?.getLong(SAVED_DATE_KEY))
    }

    override fun initView() {
        homeworkAdapter.onClickListener = presenter::onHomeworkItemSelected

        with(binding.homeworkRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = homeworkAdapter
            addItemDecoration(DividerItemDecoration(context))
        }

        with(binding) {
            homeworkSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            homeworkSwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            homeworkSwipe.setProgressBackgroundColorSchemeColor(requireContext().getThemeAttrColor(R.attr.colorSwipeRefresh))
            homeworkErrorRetry.setOnClickListener { presenter.onRetry() }
            homeworkErrorDetails.setOnClickListener { presenter.onDetailsClick() }

            homeworkPreviousButton.setOnClickListener { presenter.onPreviousDay() }
            homeworkNextButton.setOnClickListener { presenter.onNextDay() }

            openAddHomeworkButton.setOnClickListener { presenter.onHomeworkAddButtonClicked() }

            homeworkNavContainer.elevation = requireContext().dpToPx(8f)
        }
    }

    override fun updateData(data: List<HomeworkItem<*>>) {
        with(homeworkAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun clearData() {
        with(homeworkAdapter) {
            items = emptyList()
            notifyDataSetChanged()
        }
    }

    override fun updateNavigationWeek(date: String) {
        binding.homeworkNavDate.text = date
    }

    override fun showRefresh(show: Boolean) {
        binding.homeworkSwipe.isRefreshing = show
    }

    override fun showEmpty(show: Boolean) {
        binding.homeworkEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        binding.homeworkError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.homeworkErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.homeworkProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.homeworkSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.homeworkRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showPreButton(show: Boolean) {
        binding.homeworkPreviousButton.visibility = if (show) VISIBLE else View.INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        binding.homeworkNextButton.visibility = if (show) VISIBLE else View.INVISIBLE
    }

    override fun showHomeworkDialog(homework: Homework) {
        (activity as? MainActivity)?.showDialogFragment(HomeworkDetailsDialog.newInstance(homework))
    }

    override fun showAddHomeworkDialog() {
        (activity as? MainActivity)?.showDialogFragment(HomeworkAddDialog())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(SAVED_DATE_KEY, presenter.currentDate.toEpochDay())
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
