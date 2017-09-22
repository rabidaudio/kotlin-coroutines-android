package audio.rabid.debug.examples.simple

import android.widget.TextView

/**
 * Created by cjk on 9/21/17.
 */
interface CallbackExample {

    fun showConfirmDialogCallback(callback: (Boolean) -> Unit)
    fun makeNetworkRequestCallback(callback: (List<String>?, Exception?) -> Unit)
    fun doOffThreadCallback(data: String, callback: (Int) -> Unit)
    val textView: TextView

    fun example() {
        showConfirmDialogCallback { isConfirmed ->
            if (!isConfirmed) {
                textView.text = "cancelled"
                return@showConfirmDialogCallback
            }
            textView.text = "loading"
            makeNetworkRequestCallback { data, error ->
                if (error != null) {
                    textView.text = "network error"
                } else {
                    textView.text = ""
                    doOffThreadForAllData(data!!) { result ->
                        textView.append(result.toString() + "\n")
                    }
                }
            }
        }
    }

    private fun doOffThreadForAllData(data: List<String>, callback: (Int) -> Unit) {
        if (data.isEmpty()) return
        val current = data.first()
        doOffThreadCallback(current) { result ->
            callback(result)
            doOffThreadForAllData(data.subList(1, data.size), callback)
        }
    }
}