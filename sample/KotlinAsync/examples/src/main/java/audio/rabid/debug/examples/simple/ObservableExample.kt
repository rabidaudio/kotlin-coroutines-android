package audio.rabid.debug.examples.simple

import android.widget.TextView
import rx.Observable
import rx.Subscription

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
 * the reduce to accumulate state, the [Observable.startWith] coming after
 * the [findCountInFile] call in the code even though it happens before.
 * This is really hard for even an Rx expert to follow (imagine coming
 * back to this code after a few weeks and trying to figure it out),
 * and basically impossible for an rxjava newbie.
 *
 * Another note: I think there's a logic error in lines 36-38. If
 * [findCountInFile] takes a variable amount of time to complete, will
 * the flatmap on 37 maintain the order from the observable, or emit in
 * the order of completion? If the latter, is there another operator
 * which will do the right thing? I would need to check the docs.
 * This is where subtle, hard-to-diagnose bugs enter your code base.
 */
interface ObservableExample {

    fun showConfirmDialog(): Observable<Boolean>
    fun loadWordsFromNetwork(): Observable<List<String>>
    fun findCountInFile(data: String): Observable<Int>
    val textView: TextView

    fun example(): Subscription {
        return showConfirmDialog().flatMap { isConfirmed ->
            if (!isConfirmed) Observable.just("denied")
            else loadWordsFromNetwork().flatMap { words ->
                Observable.from(words)
                        .flatMap { word ->
                            findCountInFile(word).map { count -> "$word: $count\n" }
                        }
                        .startWith("")
                        .reduce("") { a, b -> a + b }
                        .onErrorReturn { "network error" }
            }
        }.subscribe {
            textView.text = it
        }
    }
}