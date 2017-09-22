package audio.rabid.debug.examples.simple

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.widget.TextView
import java.util.concurrent.BlockingQueue

/**
 * Created by cjk on 9/21/17.
 */
interface AsyncTaskExample {

    fun showConfirmDialog()
    fun loadWordsFromNetworkBlocking(): List<String>
    fun findCountInFileBlocking(data: String): Int
    val textView: TextView

    val isConfirmedQueue: BlockingQueue<Boolean>

    fun example() = SimpleAsyncTask(this).execute()
}

class SimpleAsyncTask(val parent: AsyncTaskExample) : AsyncTask<Unit, String, Unit>() {

    override fun doInBackground(vararg params: Unit) {
        parent.showConfirmDialog()
        // we can't do wait()/notify() in kotlin,
        // so we use a blocking queue here
        val isConfirmed = parent.isConfirmedQueue.take()
        if (!isConfirmed) {
            onProgressUpdate("cancelled")
            return
        }
        onProgressUpdate("loading")
        try {
            val words = parent.loadWordsFromNetworkBlocking()
            onProgressUpdate("")
            val counts = mutableListOf<String>()
            words.forEach { word ->
                val count = parent.findCountInFileBlocking(word)
                counts.add("$word: $count\n")
                onProgressUpdate(*counts.toTypedArray())
            }
        } catch (e: Exception) {
            onProgressUpdate("network error")
        }
    }

    override fun onProgressUpdate(vararg values: String?) {
        parent.textView.text = values.joinToString("\n")
    }
}

fun AsyncTaskExample.exampleShowConfirmDialogImplementation(context: Context) {
    AlertDialog.Builder(context)
            .setPositiveButton("Yes", { _, _ -> isConfirmedQueue.put(true) })
            .setPositiveButton("Yes", { _, _ -> isConfirmedQueue.put(false) })
            .create().show()
}
