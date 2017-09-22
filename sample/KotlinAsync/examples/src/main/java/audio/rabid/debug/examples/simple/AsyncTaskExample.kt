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

    fun showConfirmDialogBlocking()
    fun makeNetworkRequestBlocking(): List<String>
    fun doOffThreadBlocking(data: String): Int
    val textView: TextView

    val isConfirmedQueue: BlockingQueue<Boolean>

    fun example() = SimpleAsyncTask().execute(this)
}

class SimpleAsyncTask : AsyncTask<AsyncTaskExample, String, Unit>() {

    private lateinit var iface: AsyncTaskExample

    override fun doInBackground(vararg params: AsyncTaskExample) {
        iface = params[0]
        iface.showConfirmDialogBlocking()
        // we can't do wait()/notify() in kotlin,
        // so we use a blocking queue here
        val isConfirmed = iface.isConfirmedQueue.take()
        if (!isConfirmed) {
            onProgressUpdate("cancelled")
            return
        }
        onProgressUpdate("loading")
        try {
            val data = iface.makeNetworkRequestBlocking()
            onProgressUpdate("")
            val results = mutableListOf<String>()
            data.forEach { item ->
                val result = iface.doOffThreadBlocking(item)
                results.add(result.toString())
                onProgressUpdate(*results.toTypedArray())
            }
        } catch (e: Exception) {
            onProgressUpdate("network error")
        }
    }

    override fun onProgressUpdate(vararg values: String?) {
        iface.textView.text = values.joinToString("\n")
    }
}

fun AsyncTaskExample.exampleShowConfirmDialogImplementation(context: Context) {
    AlertDialog.Builder(context)
            .setPositiveButton("Yes", { _, _ -> isConfirmedQueue.put(true) })
            .setPositiveButton("Yes", { _, _ -> isConfirmedQueue.put(false) })
            .create().show()
}
