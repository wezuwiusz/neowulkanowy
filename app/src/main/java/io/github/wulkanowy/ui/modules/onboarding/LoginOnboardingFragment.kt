package io.github.wulkanowy.ui.modules.login.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentOnboardingBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity

class LoginOnboardingFragment : BaseFragment<FragmentOnboardingBinding>(R.layout.fragment_onboarding), LoginOnboardingView {
    companion object {
        fun newInstance() = LoginOnboardingFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as LoginActivity).showActionBar(show = false)
        binding = FragmentOnboardingBinding.bind(view)

        binding.onboardingCloseBtn.setOnClickListener {
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            prefs.edit().putBoolean("completedOnboarding", true).apply()

            (requireActivity() as LoginActivity).navigateToLoginForm()
        }
    }

    override fun initView() {
        (requireActivity() as LoginActivity)
    }

}
