package audio.rabid.talks.kotlinasync.helpers

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import kotlinx.coroutines.experimental.Deferred
import java.util.*

/**
 * Created by cjk on 9/17/17.
 */

suspend fun <T> Iterable<Deferred<T>>.awaitAll() = this.map { it.await() }

fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) +  start

inline fun FragmentManager.transaction(allowingStateLoss: Boolean = false, now: Boolean = false, block: FragmentTransaction.() -> Unit) {
    val transaction = beginTransaction()
    block(transaction)
    when {
        !allowingStateLoss && !now -> transaction.commit()
        allowingStateLoss && !now -> transaction.commitAllowingStateLoss()
        !allowingStateLoss && now -> transaction.commitNow()
        allowingStateLoss && now -> transaction.commitNowAllowingStateLoss()
    }
}