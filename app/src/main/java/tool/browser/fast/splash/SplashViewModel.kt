package tool.browser.fast.splash

import androidx.lifecycle.MutableLiveData
import tool.browser.fast.component.BaseViewModel

class SplashViewModel: BaseViewModel() {
    val progress = MutableLiveData<Int>()
}