package audio.rabid.talks.kotlinasync;

import java.util.List;

import kotlin.coroutines.experimental.Continuation;

/**
 * Created by cjk on 9/21/17.
 */

public class Scratch {

    void example(Continuation cont) {
        switch (cont.label) {
            case 0:
                cont.label = 1;
                showConfirmDialog(cont);
                break;
            case 1:
                if (cont.lastError != null)
                    throw cont.lastError;
                boolean isConfirmed = (boolean) cont.lastValue;
                if (!isConfirmed) {
                    textView.setText("cancelled");
                    return;
                }
                textView.setText("loading");
                makeNetworkRequest(cont);
                break;
            case 2:
                if (cont.lastError != null)
                    throw cont.lastError;
            /* ... */
        }
    }
}
