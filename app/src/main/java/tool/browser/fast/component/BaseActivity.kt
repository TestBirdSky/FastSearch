package tool.browser.fast.component

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import tool.browser.fast.BR

abstract class BaseActivity<Binding : ViewDataBinding, Model : BaseViewModel>(
    @LayoutRes private val layoutResId: Int,
    private val viewModelClass: Class<Model>
) : AppCompatActivity() {
    protected lateinit var mLayoutBinding: Binding
        private set
    protected lateinit var mModel: Model
        private set
    var mIsOnResume = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resources.displayMetrics.run {
            density = heightPixels / 780.0F
            densityDpi = (160 * density).toInt()
        }

        mLayoutBinding = DataBindingUtil.setContentView(this, layoutResId)

        mModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(FastSearch.mSelf)
            .create(viewModelClass)
        withLayoutBinding {
            lifecycleOwner = this@BaseActivity
            setVariable(BR.model, mModel)
        }
        withViewModel {
            startActivity.observe(this@BaseActivity) {
                this@BaseActivity.startActivityForVm(it)
            }
            finishActivityForResult.observe(this@BaseActivity) {
                this@BaseActivity.finishActivityForResult(it)
            }
        }
    }

    private fun finishActivityForResult(result: BaseViewModel.FinishActivityForResult) {
        if (result.requestCode != -1) {
            setResult(result.requestCode, result.data)
        }
        finish()
    }

    fun startActivityForVm(option: BaseViewModel.StartActivityOption) {
        doStartActivityForVm(option)
    }

    protected open fun doStartActivityForVm(option: BaseViewModel.StartActivityOption) {
        val intent = Intent(this@BaseActivity, option.target)
            .apply {
                option.bundle?.let { bundle ->
                    putExtras(bundle)
                }
            }
        if (option.requestCode == -1) {
            this@BaseActivity.startActivity(intent)
        } else {
            this@BaseActivity.startActivityForResult(intent, option.requestCode)
        }
        if (option.enterAnimResId != -1 && option.exitAnimResId != -1) {
            overridePendingTransition(option.enterAnimResId, option.exitAnimResId)
        }
        if (option.finish) {
            this@BaseActivity.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        withViewModel {
            this.onActivityResult(requestCode, resultCode, data)
        }
    }

    protected fun withLayoutBinding(receiver: Binding.() -> Unit) {
        receiver(mLayoutBinding)
    }

    protected fun withViewModel(receiver: Model.() -> Unit) {
        receiver(mModel)
    }

    override fun onResume() {
        super.onResume()
        mIsOnResume = true
    }

    override fun onPause() {
        super.onPause()
        mIsOnResume = false
    }

    override fun onStop() {
        super.onStop()
        mIsOnResume = false
    }
}