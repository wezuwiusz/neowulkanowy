package io.github.wulkanowy.ui.modules.panicmode

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.databinding.FragmentPanicModeBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.WebkitCookieManagerProxy
import io.github.wulkanowy.utils.openInternetBrowser
import javax.inject.Inject

@AndroidEntryPoint
class PanicModeFragment : BaseFragment<FragmentPanicModeBinding>(R.layout.fragment_panic_mode),
    MainView.TitledView {

    @Inject
    lateinit var wulkanowySdkFactory: WulkanowySdkFactory

    @Inject
    lateinit var webkitCookieManagerProxy: WebkitCookieManagerProxy

    private var webView: WebView? = null

    override val titleStringId: Int get() = R.string.panic_mode_title

    companion object {

        private const val PANIC_URL = "panic_mode_url"
        fun newInstance(url: String?): PanicModeFragment {
            return PanicModeFragment().apply {
                arguments = bundleOf(PANIC_URL to url)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPanicModeBinding.bind(view)

        binding.panicModeRefresh.setOnClickListener {
            binding.panicModeWebview.loadUrl(
                binding.panicModeWebview.url ?: arguments?.getString(PANIC_URL).orEmpty()
            )
        }
        binding.panicModeBack.setOnClickListener { binding.panicModeWebview.goBack() }
        binding.panicModeHome.setOnClickListener {
            binding.panicModeWebview.loadUrl(
                arguments?.getString(PANIC_URL).orEmpty()
            )
        }
        binding.panicModeForward.setOnClickListener { binding.panicModeWebview.goForward() }
        binding.panicModeShare.setOnClickListener {
            requireContext().openInternetBrowser(
                binding.panicModeWebview.url.toString(),
            )
        }

        val onBackPressedCallback = requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                binding.panicModeWebview.goBack()
            }

        with(binding.panicModeWebview) {
            webView = this
            with(settings) {
                javaScriptEnabled = true
                userAgentString = wulkanowySdkFactory.createBase().userAgent
            }

            webViewClient = object : WebViewClient() {
                override fun doUpdateVisitedHistory(
                    view: WebView?,
                    url: String?,
                    isReload: Boolean
                ) {
                    binding.panicModeBack.isEnabled = binding.panicModeWebview.canGoBack()
                    binding.panicModeForward.isEnabled = binding.panicModeWebview.canGoForward()
                    onBackPressedCallback.isEnabled = binding.panicModeWebview.canGoBack()
                }
            }
            loadUrl(arguments?.getString(PANIC_URL).orEmpty())
        }
    }

    override fun onDestroy() {
        webkitCookieManagerProxy.webkitCookieManager?.flush()
        webView?.destroy()
        super.onDestroy()
    }
}
