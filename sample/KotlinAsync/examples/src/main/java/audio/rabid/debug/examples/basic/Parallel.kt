package audio.rabid.debug.examples.basic

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay

/**
 * Created by cjk on 3/20/18.
 */
suspend fun parallelExample() {
    val one = async(CommonPool) {
        delay(100)
        return@async 1
    }
    // one is a Promise (Deferred<Int>). It has already started at this point

    val two = async(CommonPool) {
        delay(100)
        return@async 2
    }
    // now both promises are running

    // await() suspends until the promise resolves. This should take 100ms, not 200ms
    val sum = one.await() + two.await()
    print(sum)
}
