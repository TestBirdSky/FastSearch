package tool.browser.fast.guide

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import tool.browser.fast.R
import tool.browser.fast.component.popup.BaseCenterPopupView
import tool.browser.fast.databinding.DialogPrivacyGuideBinding

@SuppressLint("ViewConstructor")
class PrivacyGuidePopupView(
    context: Context, lifecycleOwner: LifecycleOwner,
    private val privacyGuideFunc: IPrivacyGuideFunc
) : BaseCenterPopupView<DialogPrivacyGuideBinding, PrivacyGuideViewModel>(
    context, lifecycleOwner, R.layout.dialog_privacy_guide, PrivacyGuideViewModel::class.java
) {
    interface IPrivacyGuideFunc {
        fun connectServer()
    }

    override fun onCreate() {
        super.onCreate()
        withLayoutBinding {
            btnConfirm.setOnClickListener {
                privacyGuideFunc.connectServer()
                this@PrivacyGuidePopupView.dismiss()
            }
        }
    }
}