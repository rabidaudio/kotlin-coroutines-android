package audio.rabid.talks.kotlinasync.examples

import android.app.Activity
import android.support.v7.app.AlertDialog
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Created by cjk on 9/17/17.
 */
class ConfirmationDialogActivity : Activity() {

    suspend fun showConfirmationDialog() = suspendCancellableCoroutine<Boolean> { cont ->
        val dialog = AlertDialog.Builder(this)
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", { _, _ -> cont.resume(true) })
                .setNegativeButton("No", { _, _ -> cont.resume(false) })
                .setCancelable(true)
                .setOnCancelListener { cont.cancel() }
                .create()

        dialog.show()
        cont.invokeOnCompletion { dialog.dismiss() }
    }
}