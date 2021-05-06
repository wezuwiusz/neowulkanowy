package io.github.wulkanowy.ui.modules.studentinfo

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.view.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.StudentGuardian
import io.github.wulkanowy.data.db.entities.StudentInfo
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.enums.Gender
import io.github.wulkanowy.databinding.FragmentStudentInfoBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.capitalise
import io.github.wulkanowy.utils.getThemeAttrColor
import javax.inject.Inject

@AndroidEntryPoint
class StudentInfoFragment :
    BaseFragment<FragmentStudentInfoBinding>(R.layout.fragment_student_info), StudentInfoView,
    MainView.TitledView {

    @Inject
    lateinit var presenter: StudentInfoPresenter

    @Inject
    lateinit var studentInfoAdapter: StudentInfoAdapter

    override val titleStringId: Int
        get() = when (requireArguments().getSerializable(INFO_TYPE_ARGUMENT_KEY) as? StudentInfoView.Type) {
            StudentInfoView.Type.PERSONAL -> R.string.account_personal_data
            StudentInfoView.Type.CONTACT -> R.string.account_contact
            StudentInfoView.Type.ADDRESS -> R.string.account_address
            StudentInfoView.Type.FAMILY -> R.string.account_family
            StudentInfoView.Type.SECOND_GUARDIAN -> R.string.student_info_guardian
            StudentInfoView.Type.FIRST_GUARDIAN -> R.string.student_info_guardian
            else -> R.string.student_info_title
        }

    override val isViewEmpty get() = studentInfoAdapter.items.isEmpty()

    companion object {

        private const val INFO_TYPE_ARGUMENT_KEY = "info_type"

        private const val STUDENT_ARGUMENT_KEY = "student_with_semesters"

        fun newInstance(type: StudentInfoView.Type, studentWithSemesters: StudentWithSemesters) =
            StudentInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(INFO_TYPE_ARGUMENT_KEY, type)
                    putSerializable(STUDENT_ARGUMENT_KEY, studentWithSemesters)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStudentInfoBinding.bind(view)
        presenter.onAttachView(
            this,
            requireArguments().getSerializable(INFO_TYPE_ARGUMENT_KEY) as StudentInfoView.Type,
            requireArguments().getSerializable(STUDENT_ARGUMENT_KEY) as StudentWithSemesters
        )
    }

    override fun initView() {
        with(binding) {
            studentInfoSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            studentInfoSwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            studentInfoSwipe.setProgressBackgroundColorSchemeColor(
                requireContext().getThemeAttrColor(
                    R.attr.colorSwipeRefresh
                )
            )
            studentInfoErrorRetry.setOnClickListener { presenter.onRetry() }
            studentInfoErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }

        with(studentInfoAdapter) {
            onItemClickListener = presenter::onItemSelected
            onItemLongClickListener = presenter::onItemLongClick
        }

        with(binding.studentInfoRecycler) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            setHasFixedSize(true)
            adapter = studentInfoAdapter
        }
    }

    override fun updateData(data: List<Pair<String, String>>) {
        with(studentInfoAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu[0].isVisible = false
    }

    override fun showPersonalTypeData(studentInfo: StudentInfo) {
        updateData(
            listOf(
                getString(R.string.student_info_first_name) to studentInfo.firstName,
                getString(R.string.student_info_second_name) to studentInfo.secondName,
                getString(R.string.student_info_last_name) to studentInfo.surname,
                getString(R.string.student_info_gender) to getString(if (studentInfo.gender == Gender.MALE) R.string.student_info_male else R.string.student_info_female),
                getString(R.string.student_info_polish_citizenship) to getString(if (studentInfo.hasPolishCitizenship) R.string.all_yes else R.string.all_no),
                getString(R.string.student_info_family_name) to studentInfo.familyName,
                getString(R.string.student_info_parents_name) to studentInfo.parentsNames
            ).map {
                if (it.second.isBlank()) it.copy(second = getString(R.string.all_no_data)) else it
            }
        )
    }

    override fun showContactTypeData(studentInfo: StudentInfo) {
        updateData(
            listOf(
                getString(R.string.student_info_phone) to studentInfo.phoneNumber,
                getString(R.string.student_info_cellphone) to studentInfo.cellPhoneNumber,
                getString(R.string.student_info_email) to studentInfo.email
            ).map {
                if (it.second.isBlank()) it.copy(second = getString(R.string.all_no_data)) else it
            }
        )
    }

    @SuppressLint("DefaultLocale")
    override fun showFamilyTypeData(studentInfo: StudentInfo) {
        updateData(
            listOfNotNull(
                studentInfo.firstGuardian?.let { it.kinship.capitalise() to it.fullName },
                studentInfo.secondGuardian?.let { it.kinship.capitalise() to it.fullName },
            ).map { (title, value) ->
                val updatedValue = value.ifBlank { getString(R.string.all_no_data) }
                val updatedTitle = title.ifBlank { getString(R.string.all_no_data) }

                updatedTitle to updatedValue
            }
        )
    }

    override fun showAddressTypeData(studentInfo: StudentInfo) {
        updateData(
            listOf(
                getString(R.string.student_info_address) to studentInfo.address,
                getString(R.string.student_info_registered_address) to studentInfo.registeredAddress,
                getString(R.string.student_info_correspondence_address) to studentInfo.correspondenceAddress
            ).map {
                if (it.second.isBlank()) it.copy(second = getString(R.string.all_no_data)) else it
            }
        )
    }

    override fun showFirstGuardianTypeData(studentGuardian: StudentGuardian) {
        updateData(
            listOf(
                getString(R.string.student_info_full_name) to studentGuardian.fullName,
                getString(R.string.student_info_kinship) to studentGuardian.kinship,
                getString(R.string.student_info_guardian_address) to studentGuardian.address,
                getString(R.string.student_info_phones) to studentGuardian.phones,
                getString(R.string.student_info_email) to studentGuardian.email
            ).map {
                if (it.second.isBlank()) it.copy(second = getString(R.string.all_no_data)) else it
            }
        )
    }

    override fun showSecondGuardianTypeData(studentGuardian: StudentGuardian) {
        updateData(
            listOf(
                getString(R.string.student_info_full_name) to studentGuardian.fullName,
                getString(R.string.student_info_kinship) to studentGuardian.kinship,
                getString(R.string.student_info_guardian_address) to studentGuardian.address,
                getString(R.string.student_info_phones) to studentGuardian.phones,
                getString(R.string.student_info_email) to studentGuardian.email
            ).map {
                if (it.second.isBlank()) it.copy(second = getString(R.string.all_no_data)) else it
            }
        )
    }

    override fun openStudentInfoView(
        infoType: StudentInfoView.Type,
        studentWithSemesters: StudentWithSemesters
    ) {
        (requireActivity() as MainActivity).pushView(newInstance(infoType, studentWithSemesters))
    }

    override fun showEmpty(show: Boolean) {
        binding.studentInfoEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showErrorView(show: Boolean) {
        binding.studentInfoError.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setErrorDetails(message: String) {
        binding.studentInfoErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.studentInfoProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.studentInfoSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.studentInfoRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun hideRefresh() {
        binding.studentInfoSwipe.isRefreshing = false
    }

    override fun copyToClipboard(text: String) {
        val clipData = ClipData.newPlainText("student_info_wulkanowy", text)
        requireActivity().getSystemService<ClipboardManager>()?.setPrimaryClip(clipData)
        Toast.makeText(context, R.string.all_copied, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
