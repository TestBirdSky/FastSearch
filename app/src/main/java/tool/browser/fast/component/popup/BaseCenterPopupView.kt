package tool.browser.fast.component.popup

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.lxj.xpopup.core.CenterPopupView
import tool.browser.fast.BR
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.component.FastSearch

@SuppressLint("ViewConstructor")
open class BaseCenterPopupView<Binding : ViewDataBinding, Model : BaseViewModel>(
    context: Context,
    private val lifecycleOwner: LifecycleOwner,
    @LayoutRes private val layoutResId: Int,
    private val viewModelClass: Class<Model>
) : CenterPopupView(context) {
    protected lateinit var mLayoutBinding: Binding
        private set
    protected lateinit var mModel: Model
        private set

    final override fun addInnerContent() {
        mLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            layoutResId,
            centerPopupContainer,
            false
        )
        contentView = mLayoutBinding.root
        val params = contentView.layoutParams as LayoutParams
        params.gravity = Gravity.CENTER
        centerPopupContainer.addView(contentView, params)
        mModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(FastSearch.mSelf)
            .create(viewModelClass)
        withLayoutBinding {
            lifecycleOwner = this@BaseCenterPopupView.lifecycleOwner
            setVariable(BR.model, mModel)
        }
    }

    protected fun withLayoutBinding(receiver: Binding.() -> Unit) {
        receiver(mLayoutBinding)
    }

    protected fun withViewModel(receiver: Model.() -> Unit) {
        receiver(mModel)
    }
}