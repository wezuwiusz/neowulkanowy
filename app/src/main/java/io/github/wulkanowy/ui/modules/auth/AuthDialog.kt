package io.github.wulkanowy.ui.modules.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.DialogAuthBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import javax.inject.Inject

@AndroidEntryPoint
class AuthDialog : BaseDialogFragment<DialogAuthBinding>(), AuthView {

    @Inject
    lateinit var presenter: AuthPresenter

    companion object {
        fun newInstance() = AuthDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DialogAuthBinding.inflate(inflater).apply { binding = this }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.onAttachView(this)

        binding.authInput.doOnTextChanged { text, _, _, _ ->
            presenter.onPeselChange(text?.toString())
        }

        binding.authButton.setOnClickListener { presenter.authorize() }
        binding.authSuccessButton.setOnClickListener {
            activity?.recreate()
            dismiss()
        }
        binding.authButtonSkip.setOnClickListener { dismiss() }
    }

    override fun enableAuthButton(isEnabled: Boolean) {
        binding.authButton.isEnabled = isEnabled
    }

    override fun showProgress(show: Boolean) {
        binding.authProgress.isVisible = show
    }

    override fun showPeselError(show: Boolean) {
        binding.authInputLayout.error = getString(R.string.auth_api_error).takeIf { show }
    }

    override fun showInvalidPeselError(show: Boolean) {
        binding.authInputLayout.error = getString(R.string.auth_invalid_error).takeIf { show }
    }

    override fun showSuccess(show: Boolean) {
        binding.authSuccess.isVisible = show
    }

    override fun showContent(show: Boolean) {
        binding.authForm.isVisible = show
    }

    override fun showDescriptionWithName(name: String) {
        binding.authDescription.text = getString(R.string.auth_description, name).parseAsHtml()
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
