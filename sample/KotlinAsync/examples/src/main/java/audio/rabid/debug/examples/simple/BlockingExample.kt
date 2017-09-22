package audio.rabid.debug.examples.simple

import android.widget.TextView

/**
 * Created by cjk on 9/21/17.
 */
interface BlockingExample {

    fun showConfirmDialog(): Boolean
    fun loadWordsFromNetwork(): List<String>
    fun findCountInFile(item: String): Int
    val textView: TextView

    fun example() {
        val isConfirmed = showConfirmDialog()
        if (!isConfirmed) {
            textView.text = "cancelled"
            return
        }
        textView.text = "loading"
        try {
            val words = loadWordsFromNetwork()
            textView.text = ""
            words.forEach { word ->
                val count = findCountInFile(word)
                textView.append("$word: $count\n")
            }
        } catch (e: Exception) {
            textView.text = "network error"
        }
    }
}