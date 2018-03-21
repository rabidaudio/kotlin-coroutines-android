package audio.rabid.debug.examples

/**
 * Created by cjk on 9/21/17.
 *
 * This is one way to conceptualize what is going on under the hood with coroutines
 */

class Continuation(val step: Int, val scope: Map<String, Any?>, val error: Throwable?)

interface SuspendConceptualImplementation {

    fun makeNetworkRequest(onComplete: (String?, Throwable?) -> Unit)
    fun showConfirmDialog(onComplete: (Boolean?, Throwable?) -> Unit)
    fun doLongTask(data: String, onComplete: (Int?, Throwable?) -> Unit)

    fun example(continuation: Continuation) {
        when (continuation.step) {
            0 -> {
                if (continuation.error != null) throw continuation.error

                makeNetworkRequest(onComplete = { res, error ->
                    val scope = mapOf<String,Any?>("data" to res)
                    example(Continuation(1, scope, error))
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
                    doLongTask(data, onComplete = { res, err ->
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