package audio.rabid.talks.kotlinasync.view_model

import audio.rabid.talks.kotlinasync.api.Api
import audio.rabid.talks.kotlinasync.backend.BluetoothDevice
import audio.rabid.talks.kotlinasync.backend.BluetoothDisabledException
import audio.rabid.talks.kotlinasync.backend.Sensor
import audio.rabid.talks.kotlinasync.backend.State
import audio.rabid.talks.kotlinasync.helpers.awaitAll
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.rx1.awaitSingle
import java.io.IOException

/**
 * Created by cjk on 9/17/17.
 */
interface CoroutineViewModel {

    val api: Api

    /**
     * Update the user interface with a new state
     */
    fun updateUI(state: State)

    /**
     * Begin a bluetooth scan and return all the devices in range which are [Sensor]s.
     * Throw [BluetoothDisabledException] if Bluetooth is disabled.
     */
    suspend fun searchForSensors(): List<BluetoothDevice>

    /**
     * Open an activity where the user selects a [BluetoothDevice] to connect to
     */
    suspend fun promptForWhichVehicle(devicesInRange: List<BluetoothDevice>): BluetoothDevice

    /**
     * Open a bluetooth connection to the device and set up a [Sensor] to interface with the vehicle
     */
    suspend fun connectToSensor(device: BluetoothDevice): Sensor

    /**
     * Show a dialog box asking the user to switch on the ignition, and resume when they say it is
     * done. Throw a [CancellationException] if they close the dialog.
     */
    suspend fun promptForIgnition()


    suspend fun execute() {
        try {
            updateUI(State.Searching)

            val devices = try {
                searchForSensors()
            } catch (e: BluetoothDisabledException) {
                return updateUI(State.BluetoothDisabled)
            }

            if (devices.isEmpty())
                return updateUI(State.NoneFound)

            val device = if (devices.size == 1) devices.first()
            else promptForWhichVehicle(devices)

            updateUI(State.Connecting)

            val sensor = try {
                connectToSensor(device)
            } catch (e: IOException) {
                return updateUI(State.ConnectionError(e))
            }

            while (!sensor.isIgnitionOn)
                promptForIgnition()

            updateUI(State.Communicating)

            val codes = try {
                // getCodes is blocking UI, so we run it on CommonPool,
                // and suspend main until it's done
                async(CommonPool) { sensor.getCodes() }.await()
            } catch (e: Exception) {
                return updateUI(State.CommunicationError(e))
            }

            val codeDetails = try {
                // to look up each code, one option is to do:
                //   codes.map { api.lookupTroubleCode(it).awaitSingle() }
                // however, this will run each network request one after the other.
                // instead, we create a promise (Deferred) for each network request,
                // and then await all of them
                codes.map {
                    async(CommonPool) {
                        api.lookupTroubleCode(it).awaitSingle()
                    }
                }.awaitAll()
            } catch (e: Exception) {
                return updateUI(State.NetworkError(e))
            }

            updateUI(State.ShowCodes(codeDetails))
        }catch (e: CancellationException) {
            updateUI(State.Canceled)
        }
    }

}
