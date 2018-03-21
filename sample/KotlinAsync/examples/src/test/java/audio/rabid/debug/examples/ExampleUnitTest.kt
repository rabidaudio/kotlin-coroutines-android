package audio.rabid.debug.examples

import com.winterbe.expekt.expect
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

    suspend fun asyncAdd(a: Int, b: Int): Int {
        delay(1000)
        return a + b
    }

    @Test
    fun asyncAddition_isCorrect() = runBlocking<Unit> {
        val result = asyncAdd(2, 2)
        expect(result).to.equal(4)
    }

    @Test
    fun job_completes() = runBlocking<Unit> {
        val job = launch(CommonPool) {
            delay(1000)
            print("OK")
        }

        job.join() // block until the job completes
        expect(job.isCompleted).to.be.`true`
        expect(job.isCancelled).to.be.`false`
    }

    @Test
    fun job_cancels() = runBlocking<Unit> {
        val job = launch(CommonPool) {
            delay(1000)
            print("OK")
        }

        delay(500)
        job.cancel()
        expect(job.isCancelled).to.be.`true`
    }

    @Test
    fun eatingCancellations() = runBlocking<Unit> {
        var reachedEnd = false
        val job = launch(CommonPool) {
            try {
                delay(1000)
            }catch (e: CancellationException) {
                // oops, we ate it
            }
            reachedEnd = true // this still happens because we didn't throw
        }
        delay(10)
        job.cancel()
        delay(10)
        expect(reachedEnd).to.be.`true`
    }

    @Test
    fun notEatingCancellations() = runBlocking<Unit> {
        var reachedEnd = false
        val job = launch(CommonPool) {
            delay(1000)
            reachedEnd = true // this still happens because we didn't throw
        }
        delay(10)
        job.cancel()
        delay(10)
        expect(reachedEnd).to.be.`false`
    }
}