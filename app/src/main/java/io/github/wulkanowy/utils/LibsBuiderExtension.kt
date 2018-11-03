package io.github.wulkanowy.utils

import android.view.View
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration

inline fun LibsBuilder.withOnExtraListener(crossinline listener: (Libs.SpecialButton?) -> Unit): LibsBuilder {
    withListener(object : LibsConfiguration.LibsListenerImpl() {
        override fun onExtraClicked(v: View?, specialButton: Libs.SpecialButton?): Boolean {
            listener(specialButton)
            return true
        }
    })
    return this
}
