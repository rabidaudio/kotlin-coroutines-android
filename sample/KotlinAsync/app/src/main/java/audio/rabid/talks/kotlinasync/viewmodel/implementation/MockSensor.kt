package audio.rabid.talks.kotlinasync.viewmodel.implementation

import audio.rabid.talks.kotlinasync.backend.Sensor

/**
 * Created by cjk on 9/17/17.
 */
class MockSensor : Sensor {

    private var hasCheckedIgnition = false

    override val isIgnitionOn: Boolean
        get() {
            if (!hasCheckedIgnition) {
                hasCheckedIgnition = true
                return false
            }
            return true
        }

    override fun getCodes(): List<String> {
        Thread.sleep(2000)
        return listOf("P0171", "P0300")
    }
}