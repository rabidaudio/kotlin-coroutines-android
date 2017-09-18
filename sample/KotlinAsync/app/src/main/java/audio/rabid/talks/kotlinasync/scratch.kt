import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.experimental.rx1.await
import kotlinx.coroutines.experimental.rx1.awaitSingle
import rx.Observable
import rx.Single
import java.util.concurrent.Future
import kotlin.coroutines.experimental.suspendCoroutine


//suspend val FusedLocationProviderClient.awaitLastLocation(): Location? = suspendCoroutine { cont ->
//    getLastLocation()
//            .addOnCompleteListener { cont.resume(it.result) }
//            .addOnFailureListener { cont.resumeWithException(it) }
//}

suspend fun <T> Task<T>.await(): T = suspendCoroutine { cont ->
    addOnCompleteListener { cont.resume(it.result) }
    addOnFailureListener { cont.resumeWithException(it) }
}

suspend fun getLastLocation() {
    val locationClient = LocationServices.getFusedLocationProviderClient(this)
    locationClient.lastLocation.await()
}