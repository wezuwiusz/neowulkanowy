package io.github.wulkanowy.ui.modules.grade.statistics

import android.graphics.Color.WHITE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.grade.GradeView
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.setOnItemSelectedListener
import kotlinx.android.synthetic.main.fragment_grade_statistics.*
import javax.inject.Inject

class GradeStatisticsFragment : BaseSessionFragment(), GradeStatisticsView, GradeView.GradeChildView {

    @Inject
    lateinit var presenter: GradeStatisticsPresenter

    private lateinit var subjectsAdapter: ArrayAdapter<String>

    companion object {
        private const val SAVED_CHART_TYPE = "CURRENT_TYPE"

        fun newInstance() = GradeStatisticsFragment()
    }

    override val isViewEmpty
        get() = gradeStatisticsChart.isEmpty

    private val gradeColors = listOf(
        6 to R.color.grade_material_six,
        5 to R.color.grade_material_five,
        4 to R.color.grade_material_four,
        3 to R.color.grade_material_three,
        2 to R.color.grade_material_two,
        1 to R.color.grade_material_one
    )

    private val gradeLabels = listOf(
        "6, 6-", "5, 5-, 5+", "4, 4-, 4+", "3, 3-, 3+", "2, 2-, 2+", "1, 1+"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grade_statistics, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = gradeStatisticsChart
        presenter.onAttachView(this, savedInstanceState?.getBoolean(SAVED_CHART_TYPE))
    }

    override fun initView() {
        gradeStatisticsChart.run {
            description.isEnabled = false
            setHoleColor(context.getThemeAttrColor(android.R.attr.windowBackground))
            setCenterTextColor(context.getThemeAttrColor(android.R.attr.textColorPrimary))
            animateXY(1000, 1000)
            minAngleForSlices = 25f
            legend.apply {
                textColor = context.getThemeAttrColor(android.R.attr.textColorPrimary)
                setCustom(gradeLabels.mapIndexed { i, it ->
                    LegendEntry().apply {
                        label = it
                        formColor = ContextCompat.getColor(context, gradeColors[i].second)
                        form = Legend.LegendForm.SQUARE
                    }
                })
            }
        }

        context?.let {
            subjectsAdapter = ArrayAdapter(it, android.R.layout.simple_spinner_item, ArrayList<String>())
            subjectsAdapter.setDropDownViewResource(R.layout.item_attendance_summary_subject)
        }

        gradeStatisticsSubjects.run {
            adapter = subjectsAdapter
            setOnItemSelectedListener { presenter.onSubjectSelected((it as TextView).text.toString()) }
        }

        gradeStatisticsSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
    }

    override fun updateSubjects(data: ArrayList<String>) {
        subjectsAdapter.run {
            clear()
            addAll(data)
            notifyDataSetChanged()
        }
    }

    override fun updateData(items: List<GradeStatistics>) {
        gradeStatisticsChart.run {
            data = PieData(PieDataSet(items.map {
                PieEntry(it.amount.toFloat(), it.grade.toString())
            }, "Legenda").apply {
                valueTextSize = 12f
                sliceSpace = 1f
                valueTextColor = WHITE
                setColors(items.map {
                    gradeColors.single { color -> color.first == it.grade }.second
                }.toIntArray(), context)
            }).apply {
                setTouchEnabled(false)
                setValueFormatter(object : ValueFormatter() {
                    override fun getPieLabel(value: Float, pieEntry: PieEntry): String {
                        return resources.getQuantityString(R.plurals.grade_number_item, value.toInt(), value.toInt())
                    }
                })
                centerText = items.fold(0) { acc, it -> acc + it.amount }
                    .let { resources.getQuantityString(R.plurals.grade_number_item, it, it) }
            }
            invalidate()
        }
    }

    override fun showSubjects(show: Boolean) {
        gradeStatisticsSubjectsContainer.visibility = if (show) View.VISIBLE else View.INVISIBLE
        gradeStatisticsTypeSwitch.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun clearView() {
        gradeStatisticsChart.clear()
    }

    override fun showContent(show: Boolean) {
        gradeStatisticsChart.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showEmpty(show: Boolean) {
        gradeStatisticsEmpty.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showProgress(show: Boolean) {
        gradeStatisticsProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showRefresh(show: Boolean) {
        gradeStatisticsSwipe.isRefreshing = show
    }

    override fun onParentLoadData(semesterId: Int, forceRefresh: Boolean) {
        presenter.onParentViewLoadData(semesterId, forceRefresh)
    }

    override fun onParentReselected() {
        //
    }

    override fun onParentChangeSemester() {
        presenter.onParentViewChangeSemester()
    }

    override fun notifyParentDataLoaded(semesterId: Int) {
        (parentFragment as? GradeFragment)?.onChildFragmentLoaded(semesterId)
    }

    override fun notifyParentRefresh() {
        (parentFragment as? GradeFragment)?.onChildRefresh()
    }

    override fun onResume() {
        super.onResume()
        gradeStatisticsTypeSwitch.setOnCheckedChangeListener { _, checkedId ->
            presenter.onTypeChange(checkedId == R.id.gradeStatisticsTypeSemester)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(GradeStatisticsFragment.SAVED_CHART_TYPE, presenter.currentIsSemester)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
