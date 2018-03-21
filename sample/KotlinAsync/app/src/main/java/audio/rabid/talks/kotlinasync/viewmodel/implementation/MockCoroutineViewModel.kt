package audio.rabid.talks.kotlinasync.viewmodel.implementation

import audio.rabid.talks.kotlinasync.backend.BluetoothDevice
import audio.rabid.talks.kotlinasync.backend.Sensor
import audio.rabid.talks.kotlinasync.backend.State
import audio.rabid.talks.kotlinasync.helpers.random
import audio.rabid.talks.kotlinasync.ui.MainActivity
import audio.rabid.talks.kotlinasync.viewmodel.CoroutineViewModel
import kotlinx.coroutines.experimental.delay

/**
 * Created by cjk on 9/17/17.
 */
class MockCoroutineViewModel(private val activity: MainActivity) : CoroutineViewModel {

    override val api = MockApi()

    override fun updateUI(state: State) {
        activity.onStateChanged(state)
    }

    override suspend fun searchForSensors(): List<BluetoothDevice> {
        delay(1500) // simulate search time
        return when(1) {
            0 -> emptyList()
            1 -> listOf(
                    BluetoothDevice(address = "AA:BB:CC:DD:EE:FF", name = "FIXD"),
                    BluetoothDevice(address = "01:23:45:67:89:AB", name = "FIXD")
            )
            else ->listOf(BluetoothDevice(address = "AA:BB:CC:DD:EE:FF", name = "FIXD"))
        }
    }

    override suspend fun promptForWhichVehicle(devicesInRange: List<BluetoothDevice>)
            = activity.promptForSelectedDevice(devicesInRange)

    override suspend fun connectToSensor(device: BluetoothDevice): Sensor {
        delay(1000)
        return MockSensor()
    }

    override suspend fun promptForIgnition() = activity.showIgnitionDialog()
}