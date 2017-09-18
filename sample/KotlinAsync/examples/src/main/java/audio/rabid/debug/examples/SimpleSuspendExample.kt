package audio.rabid.debug.examples

/**
 * Created by cjk on 9/17/17.
 */

/**
 * Here's a rather contrived example of a suspending function
 */
interface SimpleSuspendExample {

    /**
     * fire off a network request, and when it completes, come back with the result
     */
    suspend fun makeNetworkRequest(): String

    /**
     * Pop a confirmation dialog, suspend until dismissed, and return if they accepted or not
     */
    suspend fun showConfirmationDialog(): Boolean

    /**
     * Start some long-running task on another thread, and suspend until it is complete
     */
    suspend fun doLongCalculationOffThread(data: String): Int

    suspend fun example() {
        val data = makeNetworkRequest()

        val isConfirmed = showConfirmationDialog()

        if (isConfirmed) {
            val result = doLongCalculationOffThread(data)
            print(result)
        }
    }
}


interface SimpleSuspendExampleConceptualImplementation {

    fun makeNetworkRequest(onComplete: (String?, Throwable?) -> Unit)

    fun showConfirmationDialog(onComplete: (Boolean?, Throwable?) -> Unit)

    fun doLongCalculationOffThread(data: String, onComplete: (Int?, Throwable?) -> Unit)

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

                showConfirmationDialog(onComplete = { res, err ->
                    val scope = mapOf("data" to data, "isConfirmed" to res)
                    example(Continuation(2, scope, err))
                })
            }
            2 -> {
                if (continuation.error != null) throw continuation.error
                val data = continuation.scope["data"] as String
                val isConfirmed = continuation.scope["isConfirmed"] as Boolean

                if (isConfirmed) {
                    doLongCalculationOffThread(data, onComplete = { res, err ->
                        val scope = mapOf("data" to data, "isConfirmed" to isConfirmed, "result" to res)
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

