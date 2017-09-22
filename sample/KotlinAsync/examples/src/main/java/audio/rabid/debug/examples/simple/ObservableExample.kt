package audio.rabid.debug.examples.simple

import android.widget.TextView
import rx.Observable

/**
 * Created by cjk on 9/21/17.
 */
interface ObservableExample {

    fun makeNetworkRequest(): Observable<List<String>>
    fun showConfirmDialog(): Observable<Boolean>
    fun doLongTask(data: String): Observable<Int>
    val textView: TextView

    fun example() {
        showConfirmDialog().flatMap { isConfirmed ->
            if (!isConfirmed) Observable.just("denied")
            else makeNetworkRequest().flatMap { data ->
                // what if the doOffThread method varies
                // in execution time for each result?
                // Will they come in out of order?
                // Is there another flatMap alternative
                // where the order is preserved?
                // I honestly don't know.
                Observable.from(data)
                        .flatMap { result -> doLongTask(result) }
                        .map { it.toString() }
                        .startWith("")
                        .reduce("") { a, b -> a + b }
                        .onErrorReturn { "network error" }
            }
        }.subscribe {
            textView.text = it
        }
    }
}