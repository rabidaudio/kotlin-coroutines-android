package audio.rabid.talks.kotlinasync.helpers

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import java.util.*
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Created by cjk on 9/18/17.
 */
interface JobManagerMixin {

    data class ManagedJob(val job: Job, val end: LifecycleEnd) {
        enum class LifecycleEnd { onPause, onStop, onDestroy }
    }

    val _jobs: MutableMap<String, ManagedJob>

    fun launch(context: CoroutineContext = UI,
               end: ManagedJob.LifecycleEnd,
               block: suspend CoroutineScope.() -> Unit)
            = launchSingleTask(UUID.randomUUID().toString(), context, end, block)

    fun launchSingleTask(name: String,
                         context: CoroutineContext = UI,
                         end: ManagedJob.LifecycleEnd,
                         block: suspend CoroutineScope.() -> Unit) {
        _jobs[name]?.job?.cancel(CancellationException("singleTask replaced"))
        _jobs[name] = ManagedJob(launch(context, CoroutineStart.DEFAULT, block), end)
    }

    fun onPause() {
        cancelJobs(ManagedJob.LifecycleEnd.onPause)
    }

    fun onStop() {
        cancelJobs(ManagedJob.LifecycleEnd.onStop)
    }

    fun onDestroy() {
        cancelJobs(ManagedJob.LifecycleEnd.onDestroy)
    }

    private fun cancelJobs(lifecycleEnd: ManagedJob.LifecycleEnd) = _jobs.forEach { (name, managedJob) ->
        if (managedJob.end == lifecycleEnd) {
            managedJob.job.cancel(CancellationException("Job hit end of life cycle: $lifecycleEnd"))
            _jobs.remove(name)
        }
    }
}