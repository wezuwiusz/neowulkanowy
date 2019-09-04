package io.github.wulkanowy.ui.widgets

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.shape.MaterialShapeDrawable

class MaterialLinearLayout : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    init {
        val drawable = MaterialShapeDrawable.createWithElevationOverlay(context, ViewCompat.getElevation(this))
        ViewCompat.setBackground(this, drawable)
    }

    override fun setElevation(elevation: Float) {
        super.setElevation(elevation)
        if (background is MaterialShapeDrawable) {
            (background as MaterialShapeDrawable).elevation = elevation
        }
    }

    fun setElevationCompat(elevation: Float) {
        if (SDK_INT >= LOLLIPOP) {
            setElevation(elevation)
        } else {
            setBackgroundColor(ElevationOverlayProvider(context).getSurfaceColorWithOverlayIfNeeded(elevation))
        }
    }
}
