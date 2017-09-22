package audio.rabid.debug.examples.simple

import android.widget.TextView

/**
 * Created by cjk on 9/21/17.
 */
interface NonAsyncExample {

    fun showConfirmDialog(): Boolean
    fun makeNetworkRequest(): List<String>
    fun doLongTask(item: String): Int
    val textView: TextView

    fun example() {
        val isConfirmed = showConfirmDialog()
        if (!isConfirmed) {
            textView.text = "cancelled"
            return
        }
        textView.text = "loading"
        try {
            val data = makeNetworkRequest()
            textView.text = ""
            data.forEach { item ->
                val result = doLongTask(item)
                textView.append(result.toString() + "\n")
            }
        } catch (e: Throwable) {
            textView.text = "network error"
        }
    }
}