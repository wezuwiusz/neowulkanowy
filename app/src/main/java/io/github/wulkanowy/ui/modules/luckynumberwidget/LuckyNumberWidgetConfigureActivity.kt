package io.github.wulkanowy.ui.modules.luckynumberwidget

import android.appwidget.AppWidgetManager.*
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.ActivityWidgetConfigureBinding
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.WidgetConfigureAdapter
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.utils.AppInfo
import javax.inject.Inject

@AndroidEntryPoint
class LuckyNumberWidgetConfigureActivity :
    BaseActivity<LuckyNumberWidgetConfigurePresenter, ActivityWidgetConfigureBinding>(),
    LuckyNumberWidgetConfigureView {

    @Inject
    lateinit var configureAdapter: WidgetConfigureAdapter

    @Inject
    override lateinit var presenter: LuckyNumberWidgetConfigurePresenter

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
            presenter.onAttachView(this, it?.getInt(EXTRA_APPWIDGET_ID))
        }
    }

    override fun initView() {
        with(binding.widgetConfigureRecycler) {
            adapter = configureAdapter
            layoutManager = LinearLayoutManager(context)
        }

        configureAdapter.onClickListener = presenter::onItemSelect
    }

    override fun updateData(data: List<StudentWithSemesters>, selectedStudentId: Long) {
        with(configureAdapter) {
            selectedId = selectedStudentId
            items = data
            notifyDataSetChanged()
        }
    }

    override fun updateLuckyNumberWidget(widgetId: Int) {
        sendBroadcast(Intent(this, LuckyNumberWidgetProvider::class.java)
            .apply {
                action = ACTION_APPWIDGET_UPDATE
                putExtra(EXTRA_APPWIDGET_IDS, intArrayOf(widgetId))
            })
    }

    override fun setSuccessResult(widgetId: Int) {
        setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_APPWIDGET_ID, widgetId) })
    }

    override fun showError(text: String, error: Throwable) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
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
