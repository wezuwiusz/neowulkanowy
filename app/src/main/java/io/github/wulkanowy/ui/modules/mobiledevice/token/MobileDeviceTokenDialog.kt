package io.github.wulkanowy.ui.modules.mobiledevice.token

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.getSystemService
import dagger.android.support.DaggerDialogFragment
import io.github.wulkanowy.R
import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.github.wulkanowy.data.repositories.mobiledevice.MobileDeviceRemote
import io.github.wulkanowy.ui.base.BaseActivity
import kotlinx.android.synthetic.main.dialog_mobile_device.*
import javax.inject.Inject

class MobileDeviceTokenDialog : DaggerDialogFragment(), MobileDeviceTokenVIew {

    @Inject
    lateinit var presenter: MobileDeviceTokenPresenter

    companion object {
        fun newInstance(): MobileDeviceTokenDialog = MobileDeviceTokenDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_mobile_device, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        mobileDeviceDialogClose.setOnClickListener { dismiss() }
    }

    override fun updateData(token: MobileDeviceToken) {
        with(mobileDeviceDialogToken) {
            text = token.token
            setOnClickListener { clickCopy(token.token) }
        }
        with(mobileDeviceDialogSymbol) {
            text = token.symbol
            setOnClickListener { clickCopy(token.symbol) }
        }
        with(mobileDeviceDialogPin) {
            text = token.pin
            setOnClickListener { clickCopy(token.pin) }
        }

        mobileDeviceQr.setImageBitmap(Base64.decode(token.qr, Base64.DEFAULT).let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        })
    }

    override fun hideLoading() {
        mobileDeviceDialogProgress.visibility = GONE
    }

    override fun showContent() {
        mobileDeviceDialogContent.visibility = VISIBLE
    }

    override fun closeDialog() {
        dismiss()
    }

    override fun showError(text: String, error: Throwable) {
        showMessage(text)
    }

    override fun showMessage(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    override fun showExpiredDialog() {
        (activity as? BaseActivity<*>)?.showExpiredDialog()
    }

    override fun openClearLoginView() {
        (activity as? BaseActivity<*>)?.openClearLoginView()
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }

    fun clickCopy(text: String) {
        val clip = ClipData.newPlainText("wulkanowy", text)
        activity?.getSystemService<ClipboardManager>()?.setPrimaryClip(clip)
        Toast.makeText(context, R.string.all_copied, Toast.LENGTH_LONG).show()
    }
}
