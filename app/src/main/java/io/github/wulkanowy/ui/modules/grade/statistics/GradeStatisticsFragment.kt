package io.github.wulkanowy.ui.modules.grade.statistics

import android.graphics.Color
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
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.grade.GradeView
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.setOnItemSelectedListener
import kotlinx.android.synthetic.main.fragment_grade_statistics.*
import javax.inject.Inject

class GradeStatisticsFragment : BaseFragment(), GradeStatisticsView, GradeView.GradeChildView {

    @Inject
    lateinit var presenter: GradeStatisticsPresenter

    private lateinit var subjectsAdapter: ArrayAdapter<String>

    companion object {
        private const val SAVED_CHART_TYPE = "CURRENT_TYPE"

        fun newInstance() = GradeStatisticsFragment()
    }

    override val isPieViewEmpty get() = gradeStatisticsChart.isEmpty

    override val isBarViewEmpty get() = gradeStatisticsChartPoints.isEmpty

    private lateinit var gradeColors: List<Pair<Int, Int>>

    private val vulcanGradeColors = listOf(
        6 to R.color.grade_vulcan_six,
        5 to R.color.grade_vulcan_five,
        4 to R.color.grade_vulcan_four,
        3 to R.color.grade_vulcan_three,
        2 to R.color.grade_vulcan_two,
        1 to R.color.grade_vulcan_one
    )

    private val materialGradeColors = listOf(
        6 to R.color.grade_material_six,
        5 to R.color.grade_material_five,
        4 to R.color.grade_material_four,
        3 to R.color.grade_material_three,
        2 to R.color.grade_material_two,
        1 to R.color.grade_material_one
    )

    private val gradePointsColors = listOf(
        Color.parseColor("#37c69c"),
        Color.parseColor("#d8b12a")
    )

    private val gradeLabels = listOf(
        "6, 6-", "5, 5-, 5+", "4, 4-, 4+", "3, 3-, 3+", "2, 2-, 2+", "1, 1+"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grade_statistics, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = gradeStatisticsSwipe
        presenter.onAttachView(this, savedInstanceState?.getSerializable(SAVED_CHART_TYPE) as? ViewType)
    }

    override fun initView() {
        with(gradeStatisticsChart) {
            description.isEnabled = false
            setHoleColor(context.getThemeAttrColor(android.R.attr.windowBackground))
            setCenterTextColor(context.getThemeAttrColor(android.R.attr.textColorPrimary))
            animateXY(1000, 1000)
            minAngleForSlices = 25f
            legend.textColor = context.getThemeAttrColor(android.R.attr.textColorPrimary)
        }

        with(gradeStatisticsChartPoints) {
            description.isEnabled = false

            animateXY(1000, 1000)
            legend.textColor = context.getThemeAttrColor(android.R.attr.textColorPrimary)
        }

        subjectsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        subjectsAdapter.setDropDownViewResource(R.layout.item_attendance_summary_subject)

        with(gradeStatisticsSubjects) {
            adapter = subjectsAdapter
            setOnItemSelectedListener<TextView> { presenter.onSubjectSelected(it?.text?.toString()) }
        }

        gradeStatisticsSubjectsContainer.setElevationCompat(requireContext().dpToPx(1f))

        gradeStatisticsSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
        gradeStatisticsErrorRetry.setOnClickListener { presenter.onRetry() }
        gradeStatisticsErrorDetails.setOnClickListener { presenter.onDetailsClick() }
    }

    override fun updateSubjects(data: ArrayList<String>) {
        with(subjectsAdapter) {
            clear()
            addAll(data)
            notifyDataSetChanged()
        }
    }

    override fun updatePieData(items: List<GradeStatistics>, theme: String) {
        gradeColors = when (theme) {
            "vulcan" -> vulcanGradeColors
            else -> materialGradeColors
        }

        val dataset = PieDataSet(items.map {
            PieEntry(it.amount.toFloat(), it.grade.toString())
        }, "Legenda").apply {
            valueTextSize = 12f
            sliceSpace = 1f
            valueTextColor = WHITE
            setColors(items.map {
                gradeColors.single { color -> color.first == it.grade }.second
            }.toIntArray(), context)
        }

        with(gradeStatisticsChart) {
            data = PieData(dataset).apply {
                setTouchEnabled(false)
                setValueFormatter(object : ValueFormatter() {
                    override fun getPieLabel(value: Float, pieEntry: PieEntry): String {
                        return resources.getQuantityString(R.plurals.grade_number_item, value.toInt(), value.toInt())
                    }
                })
                centerText = items.fold(0) { acc, it -> acc + it.amount }
                    .let { resources.getQuantityString(R.plurals.grade_number_item, it, it) }
            }
            legend.apply {
                setCustom(gradeLabels.mapIndexed { i, it ->
                    LegendEntry().apply {
                        label = it
                        formColor = ContextCompat.getColor(context, gradeColors[i].second)
                        form = Legend.LegendForm.SQUARE
                    }
                })
            }
            invalidate()
        }
    }

    override fun updateBarData(item: GradePointsStatistics) {
        val dataset = BarDataSet(listOf(
            BarEntry(1f, item.others.toFloat()),
            BarEntry(2f, item.student.toFloat())
        ), "Legenda").apply {
            valueTextSize = 12f
            valueTextColor = requireContext().getThemeAttrColor(android.R.attr.textColorPrimary)
            valueFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry) = "${barEntry.y}%"
            }
            colors = gradePointsColors
        }

        with(gradeStatisticsChartPoints) {
            data = BarData(dataset).apply {
                barWidth = 0.5f
                setFitBars(true)
            }
            setTouchEnabled(false)
            xAxis.setDrawLabels(false)
            xAxis.setDrawGridLines(false)
            requireContext().getThemeAttrColor(android.R.attr.textColorPrimary).let {
                axisLeft.textColor = it
                axisRight.textColor = it
            }
            legend.setCustom(listOf(
                LegendEntry().apply {
                    label = "Średnia klasy"
                    formColor = gradePointsColors[0]
                    form = Legend.LegendForm.SQUARE
                },
                LegendEntry().apply {
                    label = "Uczeń"
                    formColor = gradePointsColors[1]
                    form = Legend.LegendForm.SQUARE
                }
            ))
            invalidate()
        }
    }

    override fun showSubjects(show: Boolean) {
        gradeStatisticsSubjectsContainer.visibility = if (show) View.VISIBLE else View.INVISIBLE
        gradeStatisticsTypeSwitch.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun clearView() {
        gradeStatisticsChart.clear()
        gradeStatisticsChartPoints.clear()
    }

    override fun showPieContent(show: Boolean) {
        gradeStatisticsChart.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showBarContent(show: Boolean) {
        gradeStatisticsChartPoints.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showEmpty(show: Boolean) {
        gradeStatisticsEmpty.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showErrorView(show: Boolean) {
        gradeStatisticsError.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setErrorDetails(message: String) {
        gradeStatisticsErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        gradeStatisticsProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun enableSwipe(enable: Boolean) {
        gradeStatisticsSwipe.isEnabled = enable
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
            presenter.onTypeChange(when (checkedId) {
                R.id.gradeStatisticsTypeSemester -> ViewType.SEMESTER
                R.id.gradeStatisticsTypePartial -> ViewType.PARTIAL
                else -> ViewType.POINTS
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SAVED_CHART_TYPE, presenter.currentType)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
