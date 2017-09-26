package audio.rabid.debug.examples.javainterop

import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by cjk on 9/22/17.
 */
class JavaAccessibleCoroutine {

    // not callable from java:
    suspend fun foo(): String {
        delay(100)
        return "foo"
    }

    fun createFooJob() = launch(UI) {
        print(foo())
    }

    fun createFooDeferred() = async(UI) {
        foo()
    }
}