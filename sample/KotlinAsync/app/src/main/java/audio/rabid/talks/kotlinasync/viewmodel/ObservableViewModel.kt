package audio.rabid.talks.kotlinasync.viewmodel

import android.bluetooth.BluetoothDevice
import audio.rabid.talks.kotlinasync.backend.Sensor
import audio.rabid.talks.kotlinasync.backend.State
import audio.rabid.talks.kotlinasync.api.Api
import rx.Observable
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by cjk on 9/17/17.
 *
 * Same logic as [CoroutineViewModel] but implemented with [Observable]s instead of coroutines.
 * I strongly discourage anyone to write code this way. It is included here purely for comparison's
 * sake.
 */
interface ObservableViewModel {

    val api: Api

    fun searchForSensors(): Observable<BluetoothDevice>

    fun promptForWhichVehicle(devicesInRange: List<BluetoothDevice>): Single<BluetoothDevice>

    fun connectToSensor(device: BluetoothDevice): Single<Sensor>

    fun promptForIgnition(): Single<Unit>

    fun ObservableViewModel.execute(): Observable<State> {
        return searchForSensors().toList().flatMap<State> { devices ->
            if (devices.isEmpty()) Observable.just(State.NoneFound)
            else when (devices.size) {
                1 -> Single.just(devices.first())
                else -> promptForWhichVehicle(devices)
            }.toObservable().flatMap { device ->
                connectToSensor(device).toObservable().flatMap<State> { sensor ->
                    awaitIgnitionOn(sensor).toObservable().flatMap<State> { sensor2 ->
                        val codesStream = Observable.fromCallable { sensor2.getCodes() }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                        codesStream.flatMap<State> { codes ->
                            Observable.from(codes).flatMap { code ->
                                api.lookupTroubleCode(code)
                            }.toList().map<State> { codes2 ->
                                State.ShowCodes(codes2)
                            }.onErrorReturn {
                                State.NetworkError(it)
                            }
                        }.startWith(State.Communicating).onErrorReturn {
                            State.CommunicationError(it)
                        }
                    }.onErrorReturn {
                        State.Canceled
                    }
                }.startWith(State.Connecting).onErrorReturn { e ->
                    State.ConnectionError(e)
                }
            }.onErrorReturn {
                State.Canceled
            }
        }.startWith(State.Searching).onErrorReturn {
            State.BluetoothDisabled
        }
    }

    /**
     * Uses recursion, waiting on [promptForIgnition] until [Sensor.isIgnitionOn]
     */
    private fun awaitIgnitionOn(sensor: Sensor): Single<Sensor> {
        return if (sensor.isIgnitionOn) Single.just(sensor)
        else promptForIgnition().flatMap { awaitIgnitionOn(sensor) }
    }
}