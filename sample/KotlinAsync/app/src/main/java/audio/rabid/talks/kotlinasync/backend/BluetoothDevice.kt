package audio.rabid.talks.kotlinasync.backend

import java.io.Serializable

/**
 * Created by cjk on 9/17/17.
 */
data class BluetoothDevice(val address: String, val name: String): Serializable