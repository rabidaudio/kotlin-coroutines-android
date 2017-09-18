package audio.rabid.talks.kotlinasync.helpers

import android.app.Activity
import android.content.Intent
import kotlinx.coroutines.experimental.rx1.awaitFirstOrDefault
import rx.subjects.PublishSubject

/**
 * Created by cjk on 9/17/17.
 *
 * A little glue code to allow [Activity.startActivityForResult]/[Activity.onActivityResult]
 * pairs to work with coroutines.
 *
 * This could be implemented without [PublishSubject] if Rx isn't available, just a little less
 * elegantly.
 */
interface ActivityResultMixin {

    /**
     * Container object for calls to [android.app.Activity.onActivityResult]
     */
    data class ActivityResult(val requestCode: Int, val resultCode: Int, val data: Intent?) {

        val isOk get() = resultCode == Activity.RESULT_OK
    }

    fun startActivityForResult(intent: Intent, requestCode: Int)

    val _activityResultStream: PublishSubject<ActivityResult>

    suspend fun startActivityForResultAsync(intent: Intent, requestCode: Int): ActivityResult {
        startActivityForResult(intent, requestCode)
        return _activityResultStream
                // wait for an activity result with a matching request code
                .filter { it.requestCode == requestCode }
                // if the stream ends without a match, just return a canceled result
                .awaitFirstOrDefault((ActivityResult(requestCode, Activity.RESULT_CANCELED, null)))
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        _activityResultStream.onNext(ActivityResult(requestCode, resultCode, data))
    }

    fun onDestroy() {
        _activityResultStream.onCompleted()
    }
}