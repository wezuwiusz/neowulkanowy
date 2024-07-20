package io.github.wulkanowy.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import com.google.android.material.shape.MaterialShapeDrawable

class MaterialLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    init {
        val drawable =
            MaterialShapeDrawable.createWithElevationOverlay(context, ViewCompat.getElevation(this))
        this.background = drawable
    }

    override fun setElevation(elevation: Float) {
        super.setElevation(elevation)
        if (background is MaterialShapeDrawable) {
            (background as MaterialShapeDrawable).elevation = elevation
        }
    }
}
