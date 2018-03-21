package audio.rabid.debug.examples.javainterop;

import java.util.concurrent.CancellationException;

import kotlinx.coroutines.experimental.Deferred;
import kotlinx.coroutines.experimental.Job;

/**
 * Created by cjk on 9/22/17.
 */

public class CallingCoroutinesFromKotlin {

    private JavaAccessibleCoroutine javaAccessibleCoroutine = new JavaAccessibleCoroutine();

    void callCoroutine() {

        // javaAccessibleCoroutine.foo() <-- doesn't work, requires Continuation

        // however, Jobs and Promises are interoperable

        Job job = javaAccessibleCoroutine.createFooJob();

        System.out.println(job.isActive());

        job.cancel(new CancellationException("Canceled from java"));

        Deferred<String> deferredFoo = javaAccessibleCoroutine.createFooDeferred();

        if (deferredFoo.isCompleted()) {
            String result = deferredFoo.getCompleted();
            System.out.println(result);
        }
    }
}
