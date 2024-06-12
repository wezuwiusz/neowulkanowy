package io.github.wulkanowy.ui.modules.end

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.activity.addCallback
import androidx.core.text.HtmlCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentEndBinding
import io.github.wulkanowy.ui.base.BaseFragment

@AndroidEntryPoint
class EndFragment : BaseFragment<FragmentEndBinding>(R.layout.fragment_end), EndView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEndBinding.bind(view)

        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finishAffinity()
        }

        binding.endClose.setOnClickListener { requireActivity().finishAffinity() }

        val message = getString(R.string.end_message)
        binding.endDescription.movementMethod = LinkMovementMethod.getInstance()
        binding.endDescription.text =
            HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }
}
