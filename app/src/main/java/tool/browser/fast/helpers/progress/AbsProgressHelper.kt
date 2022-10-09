package tool.browser.fast.helpers.progress

abstract class AbsProgressHelper<Progress>(
    protected val onProgressUpdate: (progress: Progress, helper: AbsProgressHelper<*>) -> Unit,
    protected val onProgressEnd: () -> Unit,
    protected val duration: Long
) {

    abstract fun start()
    abstract fun pause()
    abstract fun dismiss()
    abstract fun resume()
}