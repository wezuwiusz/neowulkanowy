package io.github.wulkanowy.ui.modules.grade.futurecalculator

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.DialogGradeFutureCalculatorBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import javax.inject.Inject

@AndroidEntryPoint
class GradeFutureCalculatorDialog : BaseDialogFragment<DialogGradeFutureCalculatorBinding>(),
    GradeFutureCalculatorView {

    @Inject
    lateinit var presenter: GradeFutureCalculatorPresenter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setView(
                DialogGradeFutureCalculatorBinding.inflate(layoutInflater)
                    .apply { binding = this }.root
            )
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView(keys: Array<String>) {
        with(binding) {
            gradeFutureCalculatorSubjectEdit.doOnTextChanged { _, _, _, _ ->
                gradeFutureCalculatorSubject.error = null
                gradeFutureCalculatorSubject.isErrorEnabled = false
                presenter.onInput(
                    gradeFutureCalculatorSubjectEdit.text.toString(),
                    gradeFutureCalculatorGradeEdit.text.toString(),
                    gradeFutureCalculatorWeightEdit.text.toString(),
                    gradeFutureCalculatorValue
                )
            }
            gradeFutureCalculatorGradeEdit.doOnTextChanged { _, _, _, _ ->
                gradeFutureCalculatorGrade.error = null
                gradeFutureCalculatorGrade.isErrorEnabled = false
                presenter.onInput(
                    gradeFutureCalculatorSubjectEdit.text.toString(),
                    gradeFutureCalculatorGradeEdit.text.toString(),
                    gradeFutureCalculatorWeightEdit.text.toString(),
                    gradeFutureCalculatorValue
                )
            }
            gradeFutureCalculatorWeightEdit.doOnTextChanged { _, _, _, _ ->
                gradeFutureCalculatorWeight.error = null
                gradeFutureCalculatorWeight.isErrorEnabled = false
                presenter.onInput(
                    gradeFutureCalculatorSubjectEdit.text.toString(),
                    gradeFutureCalculatorGradeEdit.text.toString(),
                    gradeFutureCalculatorWeightEdit.text.toString(),
                    gradeFutureCalculatorValue
                )
            }
            gradeFutureCalculatorDialogClose.setOnClickListener { dismiss() }

            (gradeFutureCalculatorSubject.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
                keys
            )
        }
    }

    override fun setErrorSubjectRequired() {
        with(binding.gradeFutureCalculatorSubject) {
            isErrorEnabled = true
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorGradeRequired() {
        with(binding.gradeFutureCalculatorGrade) {
            isErrorEnabled = true
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorWeightRequired() {
        with(binding.gradeFutureCalculatorWeight) {
            isErrorEnabled = true
            error = getString(R.string.error_field_required)
        }
    }

    override fun closeDialog() {
        dismiss()
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
