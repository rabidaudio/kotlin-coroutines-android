package audio.rabid.debug.examples.comparison

import android.widget.TextView
import audio.rabid.debug.examples.comparison.models.Contact
import audio.rabid.debug.examples.comparison.models.Friend
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by cjk on 9/21/17.
 *
 * This is the flow implemented with observables. Note that we create
 * an observable that emits UI states (strings in this case) and all ui
 * updates happen in the subscribe, which is a clean separation (and a
 * common pattern in MVVM). Also note that by exposing the subscription
 * we can cancel the task, avoiding a memory leak.
 *
 * However, note how dense the logic is, with the flatmap callback hell,
 * the confusing [Observable.retryWhen], the [Observable.startWith] coming
 * at the end of the code even though it happens first.
 * This is really hard for even an Rx expert to follow (imagine coming
 * back to this code after a few weeks and trying to figure it out),
 * and basically impossible for an rxjava newbie.
 */
interface ObservableExample {

    fun loadFriends(): Observable<List<Friend>>
    fun searchContactsForFriends(friends: List<Friend>): Observable<List<Contact>>

    val textView: TextView

    fun showRetryDialog(): Observable<Boolean>
    fun hasPermissions(): Boolean
    fun requestPermissions(): Observable<Boolean>

    class NetworkError: Exception()
    class NoPermissionError: Exception()

    fun example(): Subscription {
        return loadFriends().retryWhen {
            showRetryDialog().flatMap { retry ->
                if (retry) Observable.just(true)
                else Observable.error<Boolean>(NetworkError())
            }
        }.flatMap { friends ->
            val hasPermissionsStream = if (hasPermissions())
                Observable.just(true)
            else requestPermissions()
            hasPermissionsStream.flatMap { hasPermissions ->
                if (!hasPermissions) Observable.error(NoPermissionError())
                else searchContactsForFriends(friends)
            }.map { contacts ->
                "${contacts.size} of your friends are in your contacts already"
            }
        }.onErrorReturn { e ->
            when (e) {
                is NetworkError -> "Network Unavailable"
                is NoPermissionError -> "Contacts Permission required"
                else -> throw e
            }
        }.startWith("Loading").observeOn(AndroidSchedulers.mainThread())
                .subscribe { state -> textView.text = state }
    }
}