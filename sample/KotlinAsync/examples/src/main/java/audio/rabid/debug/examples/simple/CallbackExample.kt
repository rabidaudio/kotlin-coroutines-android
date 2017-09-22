package audio.rabid.debug.examples.simple

import android.widget.TextView

/**
 * Created by cjk on 9/21/17.
 */
interface CallbackExample {

    fun showConfirmDialogCallback(callback: (Boolean) -> Unit)
    fun loadWordsFromNetworkCallback(callback: (List<String>?, Exception?) -> Unit)
    fun findCountInFileCallback(data: String, callback: (Int) -> Unit)
    val textView: TextView

    fun example() {
        showConfirmDialogCallback { isConfirmed ->
            if (!isConfirmed) {
                textView.text = "cancelled"
                return@showConfirmDialogCallback
            }
            textView.text = "loading"
            loadWordsFromNetworkCallback { words, error ->
                if (error != null) {
                    textView.text = "network error"
                } else {
                    textView.text = ""
                    findCountInFileRecursive(words!!) { word, count ->
                        textView.append("$word: $count\n")
                    }
                }
            }
        }
    }

    private fun findCountInFileRecursive(remainingWords: List<String>, callback: (String, Int) -> Unit) {
        if (remainingWords.isEmpty()) return
        val currentWord = remainingWords.first()
        findCountInFileCallback(currentWord) { count ->
            callback(currentWord, count)
            findCountInFileRecursive(remainingWords.subList(1, remainingWords.size), callback)
        }
    }
}