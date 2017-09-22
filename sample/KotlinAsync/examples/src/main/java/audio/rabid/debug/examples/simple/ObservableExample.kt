package audio.rabid.debug.examples.simple

import android.widget.TextView
import rx.Observable
import rx.Subscription

/**
 * Created by cjk on 9/21/17.
 */
interface ObservableExample {

    fun showConfirmDialog(): Observable<Boolean>
    fun loadWordsFromNetwork(): Observable<List<String>>
    fun findCountInFile(data: String): Observable<Int>
    val textView: TextView

    fun example():Subscription {
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