package audio.rabid.talks.kotlinasync

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.rx1.awaitSingle
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable
import rx.Single
import java.io.IOException

/**
 * Created by cjk on 9/16/17.
 */



