import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.widget.TextView
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI


interface SuspendExample {

suspend fun doFirst(): String
suspend fun doSecond(): Boolean
suspend fun doThird(data: String)

suspend fun example() {
    val data = doFirst()
    if (doSecond()) {
        doThird(data)
    }
}
}




abstract class SActivity : Activity(), SimpleSuspendExample {


    override fun onStart() {
        super.onStart()


        val one = async(CommonPool) {
            delay(100)
            return@async 1
        }
        // one is a Promise (Deferred<Int>).
        // It has already started
        val two = async(CommonPool) {
            delay(100)
            return@async 2
        }
        // now both promises are running
        // await() suspends until the promise resolves
        val sum = one.await() + two.await()


        val jobs = List(100_000) {
            launch(CommonPool) {
                delay(1000)
                print(".")
            }
        }
        jobs.forEach { it.join() }

        newSingleThreadContext(name = "single")

        newFixedThreadPoolContext(nThreads = 3, name = "pool")

        val job = launch(UI) {
            delay(1000)
            print("this never gets called")
        }
        Thread.sleep(500)
        job.cancel()

        launch(CommonPool) {
            delay(100)
            print("ok")
        }
    }

}
