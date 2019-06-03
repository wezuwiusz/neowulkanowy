package io.github.wulkanowy.ui.modules.timetablewidget

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.EXTRA_FROM_PROVIDER
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.activity_widget_configure.*
import javax.inject.Inject

class TimetableWidgetConfigureActivity : BaseActivity<TimetableWidgetConfigurePresenter>(),
    TimetableWidgetConfigureView {

    @Inject
    lateinit var configureAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    @Inject
    override lateinit var presenter: TimetableWidgetConfigurePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(R.layout.activity_widget_configure)

        intent.extras.let {
            presenter.onAttachView(this, it?.getInt(EXTRA_APPWIDGET_ID), it?.getBoolean(EXTRA_FROM_PROVIDER))
        }
    }

    override fun initView() {
        widgetConfigureRecycler.apply {
            adapter = configureAdapter
            layoutManager = SmoothScrollLinearLayoutManager(context)
        }
        configureAdapter.setOnItemClickListener { presenter.onItemSelect(it) }
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
}
