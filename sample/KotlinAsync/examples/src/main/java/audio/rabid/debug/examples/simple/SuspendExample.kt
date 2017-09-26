package audio.rabid.debug.examples.simple

import android.widget.TextView

/**
 * Created by cjk on 9/21/17.
 *
 * This is the flow using coroutines. Note that it is
 * identical to [BlockingExample] except for the suspend
 * keywords.
 */
interface SuspendExample {

    suspend fun showConfirmDialog(): Boolean
    suspend fun loadWordsFromNetwork(): List<String>
    suspend fun findCountInFile(word: String): Int
    val textView: TextView

    suspend fun example() {
        val isConfirmed = showConfirmDialog()
        if (!isConfirmed) {
            textView.text = "cancelled"
            return
        }
        textView.text = "loading"
        try {
            val data = loadWordsFromNetwork()
            textView.text = ""
            data.forEach { word ->
                val count = findCountInFile(word)
                textView.append("$word: $count\n")
            }
        } catch (e: Throwable) {
            textView.text = "network error"
        }
    }
}