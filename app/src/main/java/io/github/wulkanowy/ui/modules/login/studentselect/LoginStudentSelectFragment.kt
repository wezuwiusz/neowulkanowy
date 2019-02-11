package io.github.wulkanowy.ui.modules.login.studentselect

import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_login_student_select.*
import java.io.Serializable
import javax.inject.Inject

class LoginStudentSelectFragment : BaseFragment(), LoginStudentSelectView {

    @Inject
    lateinit var presenter: LoginStudentSelectPresenter

    @Inject
    lateinit var loginAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        const val SAVED_STUDENTS = "STUDENTS"

        fun newInstance() = LoginStudentSelectFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_student_select, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this, savedInstanceState?.getSerializable(SAVED_STUDENTS))
    }

    override fun initView() {
        loginAdapter.apply { setOnItemClickListener { presenter.onItemSelected(it) } }

        loginOptionsRecycler.apply {
            adapter = loginAdapter
            layoutManager = SmoothScrollLinearLayoutManager(context)
        }
    }

    override fun updateData(data: List<LoginStudentSelectItem>) {
        loginAdapter.updateDataSet(data, true)
    }

    override fun openMainView() {
        activity?.let {
            startActivity(MainActivity.getStartIntent(it)
                .apply { addFlags(FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK) })
        }
    }

    override fun showProgress(show: Boolean) {
        loginOptionsProgressContainer.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        loginOptionsRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showActionBar(show: Boolean) {
        (activity as? AppCompatActivity)?.supportActionBar?.run { if (show) show() else hide() }
    }

    fun onParentInitStudentSelectFragment(students: List<Student>) {
        presenter.onParentInitStudentSelectView(students)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SAVED_STUDENTS, presenter.students as Serializable)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
