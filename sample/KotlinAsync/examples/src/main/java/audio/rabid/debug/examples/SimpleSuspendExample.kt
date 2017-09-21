package audio.rabid.debug.examples

import android.os.AsyncTask
import rx.Observable

/**
 * Created by cjk on 9/17/17.
 */

/**
 * Here's a rather contrived example of a suspending function
 */
s

interface SimpleCallbackExample {

    fun makeNetworkRequestCallback(callback: (String) -> Unit)

    fun showConfirmDialogCallback(callback: (Boolean) -> Unit)

    fun doOffThreadCallback(data: String, callback: (Int) -> Unit)

    fun example() {
        makeNetworkRequestCallback { data ->
            showConfirmDialogCallback { isConfirmed ->
                if (isConfirmed) doOffThreadCallback(data) { result ->
                    print(result)
                }
            }
        }
    }
}

interface SimpleAsyncTaskExample {

    fun makeNetworkRequestBlocking(): String
    fun showConfirmDialogBlocking(): Boolean
    fun doOffThreadCallback(data: String): Int

    class SimpleAsyncTask : AsyncTask<SimpleAsyncTaskExample, Void, Int?>() {

        override fun doInBackground(vararg params: SimpleAsyncTaskExample): Int? {
            val iface = params[0]

            val data = iface.makeNetworkRequestBlocking()
            return if (iface.showConfirmDialogBlocking())
                iface.doOffThreadCallback(data)
            else null
        }

        override fun onPostExecute(result: Int?) {
            if (result != null) print(result)
        }
    }

    fun example() = SimpleAsyncTask().execute(this)
}

interface SimpleAsyncObservableExample {

    fun makeNetworkRequest(): Observable<String>
    fun showConfirmDialog(): Observable<Boolean>
    fun doOffThread(data: String): Observable<Int>

    fun example1() {
        makeNetworkRequest().flatMap { data ->
            showConfirmDialog()
                    .filter { it == true }
                    .map { data }
        }.flatMap { data ->
            doOffThread(data)
        }.subscribe { result ->
            print(result)
        }
    }

    fun example2() {
        makeNetworkRequest().flatMap<Int> { data ->
            showConfirmDialog().flatMap { isConfirmed ->
                if (isConfirmed) doOffThread(data)
                else Observable.empty()
            }
        }.subscribe { result ->
            print(result)
        }
    }
}


interface SimpleSuspendExampleConceptualImplementation {

    fun makeNetworkRequest(onComplete: (String?, Throwable?) -> Unit)
    fun showConfirmDialog(onComplete: (Boolean?, Throwable?) -> Unit)
    fun doOffThread(data: String, onComplete: (Int?, Throwable?) -> Unit)

    data class Continuation(val step: Int, val scope: Map<String, Any?>, val error: Throwable?)

    fun example(continuation: Continuation) {
        when (continuation.step) {
            0 -> {
                if (continuation.error != null) throw continuation.error
                makeNetworkRequest(onComplete = { res, err ->
                    val scope = mapOf("data" to res)
                    example(Continuation(1, scope, err))
                })
            }
            1 -> {
                if (continuation.error != null) throw continuation.error
                val data = continuation.scope["data"] as String

                showConfirmDialog(onComplete = { res, err ->
                    val scope = mapOf("data" to data, "isConfirmed" to res)
                    example(Continuation(2, scope, err))
                })
            }
            2 -> {
                if (continuation.error != null) throw continuation.error
                val data = continuation.scope["data"] as String
                val isConfirmed = continuation.scope["isConfirmed"] as Boolean

                if (isConfirmed) {
                    doOffThread(data, onComplete = { res, err ->
                        val scope = mapOf(
                                "data" to data,
                                "isConfirmed" to isConfirmed,
                                "result" to res
                        )
                        example(Continuation(3, scope, err))
                    })
                }
            }
            3 -> {
                val result = continuation.scope["result"] as Int
                print(result)
            }
        }
    }

    /**
     * calling example really means calling it with step zero and no scope
     */
    fun example() = example(Continuation(0, emptyMap(), null))
}

