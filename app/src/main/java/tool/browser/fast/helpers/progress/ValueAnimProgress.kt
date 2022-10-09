package tool.browser.fast.helpers.progress

import android.animation.ValueAnimator
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import tool.browser.fast.utils.dismiss

class ValueAnimProgress(
    onProgressUpdate: (Int, AbsProgressHelper<*>) -> Unit,
    onProgressEnd: () -> Unit,
    duration: Long,
    private val lifecycleOwner: LifecycleOwner? = null
) : AbsProgressHelper<Int>(
    onProgressUpdate, onProgressEnd, duration
) {
    private val progressVA by lazy {
        createValueAnimator()
    }
    private val lifeObserver by lazy {
        LifecycleEventObserver { source, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    resume()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    onDestroy(source)
                }
                else -> {
                }
            }
        }
    }

    override fun start() {
        lifecycleOwner?.lifecycle?.addObserver(lifeObserver)
        with(progressVA) {
            duration = this@ValueAnimProgress.duration
            addUpdateListener {
                val progress = it.animatedValue as Int
                if (progress >= 95) {
                    this@ValueAnimProgress.dismiss()
                    this@ValueAnimProgress.onProgressEnd()
                } else {
                    this@ValueAnimProgress.onProgressUpdate(progress, this@ValueAnimProgress)
                }
            }
            start()
        }
    }

    override fun pause() {
        progressVA.pause()
    }

    override fun resume() {
        progressVA.resume()
    }

    private fun onDestroy(source: LifecycleOwner) {
        progressVA.dismiss()
        source.lifecycle.removeObserver(lifeObserver)
    }

    private fun createValueAnimator(): ValueAnimator {
        return ValueAnimator.ofInt(0, 100)
    }

    override fun dismiss() {
        progressVA.dismiss()
    }
}