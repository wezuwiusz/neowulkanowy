package io.github.wulkanowy.ui.widgets

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.util.AttributeSet
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.tabs.TabLayout

open class MaterialTabLayout : TabLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    fun setElevationCompat(elevation: Float) {
        if (SDK_INT >= LOLLIPOP) {
            setElevation(elevation)
        } else {
            setBackgroundColor(ElevationOverlayProvider(context).compositeOverlayWithThemeSurfaceColorIfNeeded(elevation))
        }
    }
}
