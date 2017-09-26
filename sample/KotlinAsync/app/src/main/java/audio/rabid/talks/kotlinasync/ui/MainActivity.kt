package audio.rabid.talks.kotlinasync.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import audio.rabid.talks.kotlinasync.R
import audio.rabid.talks.kotlinasync.backend.BluetoothDevice
import audio.rabid.talks.kotlinasync.backend.State
import audio.rabid.talks.kotlinasync.helpers.ActivityResultMixin
import audio.rabid.talks.kotlinasync.helpers.BaseActivity
import audio.rabid.talks.kotlinasync.helpers.JobManagerMixin.ManagedJob.LifecycleEnd.onStop
import audio.rabid.talks.kotlinasync.helpers.transaction
import audio.rabid.talks.kotlinasync.ui.fragments.CodesFragment
import audio.rabid.talks.kotlinasync.ui.fragments.ErrorFragment
import audio.rabid.talks.kotlinasync.ui.fragments.InProgressFragment
import audio.rabid.talks.kotlinasync.viewmodel.CoroutineViewModel
import audio.rabid.talks.kotlinasync.viewmodel.implementation.MockCoroutineViewModel
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

class MainActivity : BaseActivity(), ActivityResultMixin {

    private val viewModel: CoroutineViewModel = MockCoroutineViewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        // note: if we wanted this process to continue while the screen was off or another
        // activity was active, we can move this to onCreate and change the end to onDestroy
        launchSingleTask("mainTask", end = onStop) {
            viewModel.execute()
        }
    }

    fun onStateChanged(state: State) {
        // WARNING: one caveat to showing fragments within coroutines
        // is that your fragment change code could be called after saveInstanceState
        // (if the screen turns off but your job keeps running). If you use saveInstanceState
        // in your fragments, your coroutine will need to suspend until the UI resumes.
        // If you don't use saveInstanceState, you can run your transactions allowing state
        // loss and have nothing to worry about.
        supportFragmentManager.transaction(allowingStateLoss = true) {
            replace(R.id.fragment_container, getFragmentForState(state), "main")
        }
    }

    private fun getFragmentForState(state: State): Fragment = when (state) {
        is State.Searching -> InProgressFragment.create("Searching...")
        is State.BluetoothDisabled -> ErrorFragment.create("Bluetooth is disabled. Please enable")
        is State.NoneFound -> ErrorFragment.create("No sensors in range")
        is State.Connecting -> InProgressFragment.create("Connecting...")
        is State.ConnectionError -> ErrorFragment.create(state.error.message ?: "Unknown Connection Error")
        is State.Communicating -> InProgressFragment.create("Communicating...")
        is State.CommunicationError -> ErrorFragment.create(state.error.message ?: "Unknown Communication Error")
        is State.Canceled -> ErrorFragment.create("Canceled")
        is State.NetworkError -> ErrorFragment.create(state.error.message ?: "Network Error")
        is State.ShowCodes -> CodesFragment.create(state.codes)
    }

    suspend fun showIgnitionDialog() = suspendCancellableCoroutine<Unit> { cont ->
        val dialog = AlertDialog.Builder(this)
                .setMessage("Please turn on your engine")
                .setPositiveButton("Ok, it's on", { _, _ -> cont.resume(Unit) })
                .setNegativeButton("Cancel", { _, _ -> cont.cancel() })
                .setCancelable(true)
                .setOnCancelListener { cont.cancel() }
                .create()

        dialog.show()
        cont.invokeOnCompletion { dialog.dismiss() }
    }

    suspend fun promptForSelectedDevice(devicesInRange: List<BluetoothDevice>): BluetoothDevice {
        val intent = SelectDeviceActivity.getLaunchIntent(this, devicesInRange)
        val res = startActivityForResultAsync(intent, 1)
        if (!res.isOk) throw CancellationException("User canceled device selection")
        return res.data?.getSerializableExtra("EXTRA_SELECTED_DEVICE") as? BluetoothDevice
                ?: throw CancellationException("No device returned")
    }
}
