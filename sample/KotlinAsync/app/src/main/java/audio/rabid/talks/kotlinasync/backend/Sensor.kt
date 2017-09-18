package audio.rabid.talks.kotlinasync.backend

/**
 * Created by cjk on 9/17/17.
 *
 * A class that represents a connection to a vehicle
 */
interface Sensor {

    val isIgnitionOn: Boolean

    /**
     * Get a list of error codes from the vehicle. This is a blocking IO call, so
     * don't run it on the main thread!
     */
    fun getCodes(): List<String>
}