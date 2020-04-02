package io.github.wulkanowy.ui.modules.timetablewidget

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.EXTRA_FROM_PROVIDER
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.activity_widget_configure.*
import javax.inject.Inject

class TimetableWidgetConfigureActivity : BaseActivity<TimetableWidgetConfigurePresenter>(),
    TimetableWidgetConfigureView {

    @Inject
    lateinit var configureAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    @Inject
    override lateinit var presenter: TimetableWidgetConfigurePresenter

    @Inject
    lateinit var appInfo: AppInfo

    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(R.layout.activity_widget_configure)

        intent.extras.let {
            presenter.onAttachView(this, it?.getInt(EXTRA_APPWIDGET_ID), it?.getBoolean(EXTRA_FROM_PROVIDER))
        }
    }

    override fun initView() {
        with(widgetConfigureRecycler) {
            adapter = configureAdapter
            layoutManager = SmoothScrollLinearLayoutManager(context)
        }

        configureAdapter.setOnItemClickListener(presenter::onItemSelect)
    }

    override fun showThemeDialog() {
        var items = arrayOf(
            getString(R.string.widget_timetable_theme_light),
            getString(R.string.widget_timetable_theme_dark)
        )
        if (appInfo.versionCode >= Build.VERSION_CODES.Q) items += getString(R.string.widget_timetable_theme_system)

        dialog = AlertDialog.Builder(this, R.style.WulkanowyTheme_WidgetAccountSwitcher)
            .setTitle(R.string.widget_timetable_theme_title)
            .setOnDismissListener { presenter.onDismissThemeView() }
            .setSingleChoiceItems(items, -1) { _, which ->
                val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
                presenter.onThemeSelect(if (isDarkMode && which == 2 || which == 1) 1 else 0)
            }
            .show()
    }

    override fun updateData(data: List<TimetableWidgetConfigureItem>) {
        configureAdapter.updateDataSet(data)
    }

    override fun updateTimetableWidget(widgetId: Int) {
        sendBroadcast(Intent(this, TimetableWidgetProvider::class.java)
            .apply {
                action = ACTION_APPWIDGET_UPDATE
                putExtra(EXTRA_APPWIDGET_IDS, intArrayOf(widgetId))
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
