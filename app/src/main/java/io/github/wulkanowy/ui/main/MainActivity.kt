package io.github.wulkanowy.ui.main

import android.content.Context
import android.content.Intent
import io.github.wulkanowy.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    companion object {
        fun getStartIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}

