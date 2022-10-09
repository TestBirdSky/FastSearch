package tool.browser.fast.utils

import android.graphics.Bitmap
import android.graphics.Outline
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.DimenRes
import tool.browser.fast.R

fun View.show() {
    if (visibility == View.VISIBLE) return
    visibility = View.VISIBLE
}

fun View.gone() {
    if (visibility == View.GONE) return
    visibility = View.GONE
}

fun View.removeFromParent() {
    (parent as? ViewGroup)?.removeView(this)
}

fun View.setCorner(corner: ViewOutlineProvider) {
    outlineProvider = corner
    clipToOutline = true
}

fun View.captureByDrawCache(): Bitmap? {
    if (!isDrawingCacheEnabled) {
        isDrawingCacheEnabled = true
    }
    return getDrawingCache(true)
}

object CornerIns {
    val dp_4 by lazy {
        createOutlineProvider(R.dimen.corner_4dp)
    }
    val dp_8 by lazy {
        createOutlineProvider(R.dimen.corner_8dp)
    }
    val dp_12 by lazy {
        createOutlineProvider(R.dimen.corner_12dp)
    }
    val dp_16 by lazy {
        createOutlineProvider(R.dimen.corner_16dp)
    }

    private fun createOutlineProvider(@DimenRes dimensionResId: Int): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(p0: View?, p1: Outline?) {
                p0 ?: return
                p1 ?: return
                p1.setRoundRect(
                    0, 0, p0.width, p0.height,
                    p0.context.resources.getDimensionPixelSize(dimensionResId).toFloat()
                )
            }
        }
    }
}