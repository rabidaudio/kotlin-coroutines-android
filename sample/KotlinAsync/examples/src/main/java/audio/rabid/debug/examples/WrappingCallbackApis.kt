package audio.rabid.debug.examples

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Created by cjk on 3/20/18.
 */

// Not Cancellable
suspend fun <T> Task<T>.await(): T = suspendCoroutine { cont ->
    addOnCompleteListener { cont.resume(it.result) }
    addOnFailureListener { cont.resumeWithException(it) }
}

@SuppressLint("MissingPermission")
suspend fun wrapingCallbackApisExample(context: Context) {
    launch(UI) {
        val locationClient = LocationServices.getFusedLocationProviderClient(context)
        val location = locationClient.lastLocation.await()
    }
}

class NetworkException(val response: Response<*>): Exception()

// Cancellable
suspend fun <T> Call<T>.await(): T = suspendCancellableCoroutine { cont ->
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>) {
            if (response.isSuccessful)
                cont.resume(response.body())
            else
                cont.resumeWithException(NetworkException(response))
        }
        override fun onFailure(call: Call<T>?, t: Throwable) {
            cont.resumeWithException(t)
        }
    })
    cont.invokeOnCompletion { if (cont.isCancelled) cancel() }
}


