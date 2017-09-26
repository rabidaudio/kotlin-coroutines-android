package audio.rabid.debug.examples

import android.app.Activity
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by cjk on 9/20/17.
 *
 * How to cancel jobs with the UI lifecylce to avoid memory leaks.
 *
 * See JobManagerMixin in the app module for a more powerful implementation
 * of this.
 */
class JobCancellationActivity : Activity() {

    var job: Job? = null

    override fun onStart() {
        super.onStart()

        job = launch(UI) { /* ... */ }
    }

    override fun onStop() {
        job?.cancel(CancellationException("onStop"))
        super.onStop()
    }
}