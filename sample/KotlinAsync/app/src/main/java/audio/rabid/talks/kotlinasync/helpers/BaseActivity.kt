package audio.rabid.talks.kotlinasync.helpers

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.rx1.awaitFirstOrDefault
import rx.subjects.PublishSubject
import java.util.*
import java.util.concurrent.CancellationException
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Created by cjk on 9/17/17.
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * Container object for started coroutines
     */
    data class ManagedJob(val job: Job, val end: LifecycleEnd) {
        enum class LifecycleEnd { onPause, onStop, onDestroy }
    }

    private val jobs = mutableMapOf<String, ManagedJob>()

    /**
     * Container object for calls to [android.app.Activity.onActivityResult]
     */
    data class ActivityResult(val requestCode: Int, val resultCode: Int, val data: Intent?) {

        val isOk get() = resultCode == Activity.RESULT_OK
    }

    private val activityResultStream = PublishSubject.create<ActivityResult>()

    /**
     * Instead of calling [startActivityForResult], call this, which will suspend until a result
     * is received, and providing an [ActivityResult] describing the result.
     */
    suspend fun startActivityForResultAsync(intent: Intent, requestCode: Int): ActivityResult {
        startActivityForResult(intent, requestCode)
        return activityResultStream
                // wait for an activity result with a matching request code
                .filter { it.requestCode == requestCode }
                // if the stream ends without a match, just return a canceled result
                .awaitFirstOrDefault((ActivityResult(requestCode, Activity.RESULT_CANCELED, null)))
    }

    fun launch(context: CoroutineContext = UI,
               end: ManagedJob.LifecycleEnd,
               block: suspend CoroutineScope.() -> Unit)
            = launchSingleTask(UUID.randomUUID().toString(), context, end, block)

    fun launchSingleTask(name: String,
                         context: CoroutineContext = UI,
                         end: ManagedJob.LifecycleEnd,
                         block: suspend CoroutineScope.() -> Unit) {
        jobs[name]?.job?.cancel(CancellationException("singleTask replaced"))
        jobs[name] = ManagedJob(kotlinx.coroutines.experimental.launch(context, CoroutineStart.DEFAULT, block), end)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultStream.onNext(ActivityResult(requestCode, resultCode, data))
    }

    override fun onPause() {
        cancelJobs(ManagedJob.LifecycleEnd.onPause)
        super.onPause()
    }

    override fun onStop() {
        cancelJobs(ManagedJob.LifecycleEnd.onStop)
        super.onStop()
    }

    override fun onDestroy() {
        cancelJobs(ManagedJob.LifecycleEnd.onDestroy)
        activityResultStream.onCompleted()
        super.onDestroy()
    }

    private fun cancelJobs(lifecycleEnd: ManagedJob.LifecycleEnd) = jobs.forEach { (name, managedJob) ->
        if (managedJob.end == lifecycleEnd) {
            managedJob.job.cancel(CancellationException("Job hit end of life cycle: $lifecycleEnd"))
            jobs.remove(name)
        }
    }
}