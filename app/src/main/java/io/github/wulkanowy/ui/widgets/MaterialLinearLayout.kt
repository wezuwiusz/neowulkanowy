package io.github.wulkanowy.ui.widgets

import android.content.Context
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import com.google.android.material.shape.MaterialShapeDrawable

class MaterialLinearLayout : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    init {
        val drawable = MaterialShapeDrawable.createWithElevationOverlay(context, ViewCompat.getElevation(this))
        ViewCompat.setBackground(this, drawable)
    }

    @RequiresApi(LOLLIPOP)
    override fun setElevation(elevation: Float) {
        super.setElevation(elevation)
        if (background is MaterialShapeDrawable) {
            (background as MaterialShapeDrawable).elevation = elevation
        }
    }
}
