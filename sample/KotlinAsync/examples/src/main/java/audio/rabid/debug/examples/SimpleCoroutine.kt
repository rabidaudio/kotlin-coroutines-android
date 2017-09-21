package audio.rabid.debug.examples

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.experimental.delay
import rx.lang.kotlin.toCompletable
import java.util.concurrent.Executors
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Created by cjk on 9/20/17.
 */



suspend fun delayedParseInt(i: String): Int {
    return suspendCoroutine { cont ->
        // this code gets executed immediately to set up the coroutine

        Handler(Looper.getMainLooper()).postDelayed({
            // some time later, use the continuation to resume from suspension
            try {
                val result = i.toInt()
                cont.resume(result)
            }catch (e: NumberFormatException) {
                cont.resumeWithException(e)
            }
        }, 1000)

        // after leaving this method, the caller will suspend until resume is called
    }
}


suspend fun delayedSum(commaSeparatedNumbers: String): Int {


    val s = "0"

    // conditionals
    if (delayedParseInt(s) == 0) {
        // ...
    }

    // loops
    while (delayedParseInt(x))

    // high-level functions
    commaSeparatedNumbers.split(",").map { delayedParseInt(it) }

    var sum = 0
    // use normal flow control (loops)...
    for (i in (0..numbers.size)) {
        val number = numbers[i]
        // ...conditionals...
        if (number == 0) {
            delay(100) // call other suspending functions
        }
        sum += number
    }
    return sum
}