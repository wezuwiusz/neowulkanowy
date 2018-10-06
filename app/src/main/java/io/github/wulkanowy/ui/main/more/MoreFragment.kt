package io.github.wulkanowy.ui.main.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment

class MoreFragment : BaseFragment() {

    companion object {
        fun newInstance() = MoreFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_attendance, container, false)
    }
}

