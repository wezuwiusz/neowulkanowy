package io.github.wulkanowy.ui.modules.account.accountdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.databinding.DialogAccountEditDetailsBinding
import io.github.wulkanowy.utils.lifecycleAwareVariable

class AccountEditDetailsDialog : DialogFragment() {

    private var binding: DialogAccountEditDetailsBinding by lifecycleAwareVariable()

    companion object {

        fun newInstance() = AccountEditDetailsDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DialogAccountEditDetailsBinding.inflate(inflater).apply { binding = this }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.accountEditDetailsCancel.setOnClickListener { dismiss() }
    }
}
