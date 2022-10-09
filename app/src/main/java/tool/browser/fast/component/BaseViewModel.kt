package tool.browser.fast.component

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.AnimRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    class StartActivityOption(
        val target: Class<out Activity>,
        val bundle: Bundle? = null,
        val finish: Boolean = false,
        val requestCode: Int = -1,
        @AnimRes
        val enterAnimResId: Int = -1,
        @AnimRes
        val exitAnimResId: Int = -1,
    )

    class FinishActivityForResult(
        val requestCode: Int,
        val data: Intent?
    )

    val startActivity = MutableLiveData<StartActivityOption>()
    val finishActivityForResult = MutableLiveData<FinishActivityForResult>()
    val enabled = MutableLiveData(true)

    fun onBackPressed(view: View) {
        (view.context as? Activity)?.onBackPressed()
    }

    fun finishActivity(view: View) {
        (view.context as? Activity)?.finish()
    }

    fun startActivity(view: View, option: StartActivityOption) {
        (view.context as? BaseActivity<*, *>)?.startActivityForVm(option)
    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
}