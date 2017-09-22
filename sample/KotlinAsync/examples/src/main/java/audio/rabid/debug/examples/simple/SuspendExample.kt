package audio.rabid.debug.examples.simple

import android.widget.TextView

/**
 * Created by cjk on 9/21/17.
 */
interface SuspendExample {

    suspend fun makeNetworkRequest(): List<String>
    suspend fun showConfirmDialog(): Boolean
    suspend fun doLongTask(data: String): Int
    val textView: TextView

    suspend fun example() {
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