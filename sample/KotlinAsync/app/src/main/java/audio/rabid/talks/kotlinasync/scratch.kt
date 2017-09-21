import android.app.Activity
import android.os.Bundle
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

interface SimpleSuspendExample {

    suspend fun makeNetworkRequest(): List<String>
    suspend fun showConfirmDialog(): Boolean
    suspend fun doOffThread(data: String): Int

    suspend fun example() {
        val isConfirmed = showConfirmDialog()

        if (isConfirmed) {
            val data = makeNetworkRequest()
            data.forEach { item ->
                val result = doOffThread(item)
                print(result)
            }
        } else {
            print("denied")
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
