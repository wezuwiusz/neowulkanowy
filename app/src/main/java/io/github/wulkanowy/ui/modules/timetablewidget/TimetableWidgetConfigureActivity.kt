package io.github.wulkanowy.ui.modules.timetablewidget

import android.appwidget.AppWidgetManager.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.ActivityWidgetConfigureBinding
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.WidgetConfigureAdapter
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.EXTRA_FROM_CONFIGURE
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.EXTRA_FROM_PROVIDER
import io.github.wulkanowy.utils.AppInfo
import javax.inject.Inject

@AndroidEntryPoint
class TimetableWidgetConfigureActivity :
    BaseActivity<TimetableWidgetConfigurePresenter, ActivityWidgetConfigureBinding>(),
    TimetableWidgetConfigureView {

    @Inject
    lateinit var configureAdapter: WidgetConfigureAdapter

    @Inject
    override lateinit var presenter: TimetableWidgetConfigurePresenter

    @Inject
    lateinit var appInfo: AppInfo

    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(
            ActivityWidgetConfigureBinding.inflate(layoutInflater).apply { binding = this }.root
        )

        intent.extras.let {
            presenter.onAttachView(
                this,
                it?.getInt(EXTRA_APPWIDGET_ID),
                it?.getBoolean(EXTRA_FROM_PROVIDER)
            )
        }
    }

    override fun initView() {
        with(binding.widgetConfigureRecycler) {
            adapter = configureAdapter
            layoutManager = LinearLayoutManager(context)
        }

        configureAdapter.onClickListener = presenter::onItemSelect
    }

    override fun showThemeDialog() {
        var items = arrayOf(
            getString(R.string.widget_timetable_theme_light),
            getString(R.string.widget_timetable_theme_dark)
        )

        if (appInfo.systemVersion >= Build.VERSION_CODES.Q) items += getString(R.string.widget_timetable_theme_system)

        dialog = AlertDialog.Builder(this, R.style.WulkanowyTheme_WidgetAccountSwitcher)
            .setTitle(R.string.widget_timetable_theme_title)
            .setOnDismissListener { presenter.onDismissThemeView() }
            .setSingleChoiceItems(items, -1) { _, which ->
                presenter.onThemeSelect(which)
            }
            .show()
    }

    override fun updateData(data: List<StudentWithSemesters>, selectedStudentId: Long) {
        with(configureAdapter) {
            selectedId = selectedStudentId
            items = data
            notifyDataSetChanged()
        }
    }

    override fun updateTimetableWidget(widgetId: Int) {
        sendBroadcast(Intent(this, TimetableWidgetProvider::class.java)
            .apply {
                action = ACTION_APPWIDGET_UPDATE
                putExtra(EXTRA_APPWIDGET_IDS, intArrayOf(widgetId))
                putExtra(EXTRA_FROM_CONFIGURE, true)
            })
    }

    override fun setSuccessResult(widgetId: Int) {
        setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_APPWIDGET_ID, widgetId) })
    }

    override fun showError(text: String, error: Throwable) {
        Toast.makeText(this, text, LENGTH_LONG).show()
    }

    override fun finishView() {
        finish()
    }

    override fun openLoginView() {
        startActivity(LoginActivity.getStartIntent(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }
}
