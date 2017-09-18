package audio.rabid.talks.kotlinasync.helpers

import android.app.Activity
import android.content.Intent
import kotlinx.coroutines.experimental.rx1.awaitFirst
import rx.subjects.PublishSubject

/**
 * Created by cjk on 9/17/17.
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
        return _activityResultStream.filter { it.requestCode == requestCode }.awaitFirst()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        _activityResultStream.onNext(ActivityResult(requestCode, resultCode, data))
    }
}