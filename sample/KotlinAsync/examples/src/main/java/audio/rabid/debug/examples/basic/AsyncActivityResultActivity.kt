package audio.rabid.debug.examples.basic

import android.app.Activity
import android.content.Intent
import kotlinx.coroutines.experimental.rx1.awaitFirstOrDefault
import rx.subjects.PublishSubject

/**
 * Created by cjk on 3/19/18.
 *
 * A little glue code to allow [Activity.startActivityForResult]/[Activity.onActivityResult]
 * pairs to work with coroutines.
 *
 * This could be implemented without [PublishSubject] if Rx isn't available, just a little less
 * elegantly.
 */
abstract class AsyncActivityResultActivity : Activity() {

    /**
     * Container object for calls to [android.app.Activity.onActivityResult]
     */
    data class ActivityResult(val requestCode: Int,
                              val resultCode: Int,
                              val data: Intent?) {

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
                .awaitFirstOrDefault((ActivityResult(requestCode, RESULT_CANCELED, null)))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultStream.onNext(ActivityResult(requestCode, resultCode, data))
    }

    override fun onDestroy() {
        activityResultStream.onCompleted()
        super.onDestroy()
    }
}