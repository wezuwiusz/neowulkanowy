package io.github.wulkanowy.utils

import android.app.Activity
import android.view.View
import javax.inject.Inject

@Suppress("UNUSED_PARAMETER")
class UpdateHelper @Inject constructor() {

    lateinit var messageContainer: View

    fun checkAndInstallUpdates(activity: Activity) {}

    fun onActivityResult(requestCode: Int, resultCode: Int) {}

    fun onResume(activity: Activity) {}
}
