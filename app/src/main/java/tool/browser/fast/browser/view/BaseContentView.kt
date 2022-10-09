package tool.browser.fast.browser.view

import android.graphics.Bitmap
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import tool.browser.fast.BR
import tool.browser.fast.browser.tab.ITabHost
import tool.browser.fast.browser.tab.IView
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.component.FastSearch
import tool.browser.fast.utils.captureByDrawCache

abstract class BaseContentView<Binding : ViewDataBinding, Model : BaseViewModel>(
    @LayoutRes private val layoutResId: Int,
    private val viewModelClass: Class<Model>
) : IView {

    protected lateinit var mLayoutBinding: Binding
        private set
    protected lateinit var mModel: Model
        private set

    @Suppress("MemberVisibilityCanBePrivate")
    protected var mHost: ITabHost? = null

    @CallSuper
    override fun onCreate(host: ITabHost) {
        super.onCreate(host)
        mHost = host
        mLayoutBinding = DataBindingUtil.inflate(
            host.createLayoutInflater(),
            layoutResId,
            null,
            false
        )
        mModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(FastSearch.mSelf)
            .create(viewModelClass)
        withLayoutBinding {
            lifecycleOwner = host.getLifeOwner()
            setVariable(BR.model, mModel)
        }
    }

    final override fun getRoot(): View {
        return mLayoutBinding.root
    }

    protected fun withLayoutBinding(receiver: Binding.() -> Unit) {
        receiver(mLayoutBinding)
    }

    protected fun withViewModel(receiver: Model.() -> Unit) {
        receiver(mModel)
    }

    @CallSuper
    override fun onDestroy() {
        mHost = null
    }

    override fun snapshot(): Bitmap? {
        return mLayoutBinding.root.captureByDrawCache()
    }
}