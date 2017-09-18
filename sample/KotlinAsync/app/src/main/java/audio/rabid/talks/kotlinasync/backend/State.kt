package audio.rabid.talks.kotlinasync.backend

import audio.rabid.talks.kotlinasync.api.DiagnosticTroubleCode

/**
 * Created by cjk on 9/17/17.
 *
 * The possible UI states for [MainActivity]. They should provide everything required to actually
 * render some user interface.
 */
sealed class State {
    object Searching : State()
    object BluetoothDisabled : State()
    object NoneFound : State()
    object Connecting : State()
    data class ConnectionError(val error: Throwable) : State()
    object Communicating : State()
    data class CommunicationError(val error: Throwable) : State()
    object Canceled : State()
    data class NetworkError(val error: Throwable) : State()
    data class ShowCodes(val codes: List<DiagnosticTroubleCode>) : State()
}