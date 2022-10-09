package tool.browser.fast.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.blankj.utilcode.util.BarUtils
import com.bumptech.glide.Glide
import tool.browser.fast.R
import tool.browser.fast.main.MainViewModel
import tool.browser.fast.server.State

object BindingAdaptUtil {
    @Suppress("MemberVisibilityCanBePrivate")
    const val STATUS_BAR_ADAPT_MODE_TOP_MARGIN = "topMargin"

    @BindingAdapter("adapt_status_bar")
    @JvmStatic
    fun adaptStatusBar(view: View, mode: String?) {
        when (mode ?: STATUS_BAR_ADAPT_MODE_TOP_MARGIN) {
            STATUS_BAR_ADAPT_MODE_TOP_MARGIN -> {
                val marginLp = view.layoutParams as? ViewGroup.MarginLayoutParams
                marginLp?.let {
                    it.topMargin += BarUtils.getStatusBarHeight()
                    view.layoutParams = it
                }
            }
        }
    }

    @BindingAdapter("country_src")
    @JvmStatic
    fun setCountrySrc(view: ImageView, country: String?) {
        view.setImageResource(
            when (country?.lowercase()?.replace(" ", "")) {
                "canada" -> R.mipmap.canada
                "unitedkingdom" -> R.mipmap.unitedkingdom
                "unitedstates" -> R.mipmap.unitedstates
                "japan" -> R.mipmap.japan
                else -> R.mipmap.ic_server
            }
        )
    }

    @BindingAdapter("connect_time_unit")
    @JvmStatic
    fun setConnectTimeUnitText(view: TextView, connectTime: Long?) {
        view.text = connectTime?.toTimeUnit()
    }

    @BindingAdapter("lottie_toggle")
    @JvmStatic
    fun toggleLottieAnimation(view: LottieAnimationView, connectState: State?) {
        val state = connectState ?: return
        when (state) {
            State.CONNECTED -> {
                view.progress = 1.0F
                view.cancelAnimation()
            }
            State.CONNECTING -> {
                view.playAnimation()
            }
            State.STOPPING -> {
                view.playAnimation()
            }
            State.STOPPED -> {
                view.cancelAnimation()
                view.progress = 0.0F
            }
        }
    }

    @BindingAdapter("connect_state_text")
    @JvmStatic
    fun setConnectStateText(view: TextView, connectState: State?) {
        view.text = when (connectState) {
            State.CONNECTED -> MainViewModel.STATE_TEXT_CONNECTED
            State.CONNECTING -> MainViewModel.STATE_TEXT_CONNECTING
            State.STOPPING -> MainViewModel.STATE_TEXT_DISCONNECTING
            else -> MainViewModel.STATE_TEXT_DISCONNECT
        }
    }

    @BindingAdapter("is_enabled")
    @JvmStatic
    fun setEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
    }

    @BindingAdapter("compat_src")
    @JvmStatic
    fun setCompatSrc(view: AppCompatImageView, src: Int) {
        view.setImageResource(src)
    }

    @BindingAdapter("str_color")
    @JvmStatic
    fun setTextColor(view: TextView, stringColor: String?) {
        stringColor?.let {
            view.setTextColor(it.toColorInt())
        }
    }

    @BindingAdapter("is_selected")
    @JvmStatic
    fun setSelected(view: View, selected: Boolean) {
        view.isSelected = selected
    }

    @BindingAdapter("corner_out_provider")
    @JvmStatic
    fun setOutProvider(view: View, dpInt: Int?) {
        val provider = when (dpInt) {
            4 -> CornerIns.dp_4
            8 -> CornerIns.dp_8
            12 -> CornerIns.dp_12
            16 -> CornerIns.dp_16
            else -> null
        }
        provider ?: return
        view.setCorner(provider)
    }

    @BindingAdapter("is_show")
    @JvmStatic
    fun showOrGone(view: View, isShow: Boolean) {
        if (isShow) view.show() else view.gone()
    }

    @BindingAdapter("drawable_top")
    @JvmStatic
    fun drawableTop(view: TextView, da: Drawable?) {
        da ?: return
        da.setBounds(0, 0, da.minimumWidth, da.minimumHeight);
        view.setCompoundDrawables(null, da, null, null)
    }

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun bindImageUrl(view: ImageView, url: String) {
        Glide.with(view).load(url).into(view)
    }
}