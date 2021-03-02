package io.github.wulkanowy.ui.modules.account.accountedit

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.ItemAccountEditColorBinding
import javax.inject.Inject

class AccountEditColorAdapter @Inject constructor() :
    RecyclerView.Adapter<AccountEditColorAdapter.ViewHolder>() {

    var items = listOf<Int>()

    var selectedColor = 0

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemAccountEditColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @SuppressLint("RestrictedApi", "NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            accountEditItemColor.setImageDrawable(GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(item)
            })

            accountEditItemColorContainer.foreground = item.createForegroundDrawable()
            accountEditCheck.isVisible = selectedColor == item

            root.setOnClickListener {
                val oldSelectedPosition = items.indexOf(selectedColor)
                selectedColor = item

                notifyItemChanged(oldSelectedPosition)
                notifyItemChanged(position)
            }
        }
    }

    private fun Int.createForegroundDrawable(): Drawable =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val mask = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.BLACK)
            }
            RippleDrawable(ColorStateList.valueOf(this.rippleColor), null, mask)
        } else {
            val foreground = StateListDrawable().apply {
                alpha = 80
                setEnterFadeDuration(250)
                setExitFadeDuration(250)
            }

            val mask = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(this@createForegroundDrawable.rippleColor)
            }

            foreground.apply {
                addState(intArrayOf(android.R.attr.state_pressed), mask)
                addState(intArrayOf(), ColorDrawable(Color.TRANSPARENT))
            }
        }

    private inline val Int.rippleColor: Int
        get() {
            val hsv = FloatArray(3)
            Color.colorToHSV(this, hsv)
            hsv[2] = hsv[2] * 0.5f
            return Color.HSVToColor(hsv)
        }

    class ViewHolder(val binding: ItemAccountEditColorBinding) :
        RecyclerView.ViewHolder(binding.root)
}
