package audio.rabid.debug.examples.comparison

import android.widget.TextView
import audio.rabid.debug.examples.comparison.models.Contact
import audio.rabid.debug.examples.comparison.models.Friend

/**
 * Created by cjk on 9/21/17.
 *
 * This is a way to implement the flow using callbacks. We've changed
 * the signature of each async method to accept callbacks.
 *
 * Note that this is not cancelable and therefore susceptible to a memory leak
 * of context
 */
interface CallbackExample {

    fun loadFriends(callback: (Exception?, List<Friend>?) -> Unit)
    fun searchContactsForFriends(friends: List<Friend>, callback: (List<Contact>) -> Unit)

    val textView: TextView

    fun showRetryDialog(callback: (Boolean) -> Unit)
    fun hasPermissions(): Boolean
    fun requestPermissions(callback: (Boolean) -> Unit)

    fun example() {
        textView.text = "Loading"
        loadFriendsWithRetry { _, friends ->
            if (friends == null) {
                textView.text = "Network Unavailable"
                return@loadFriendsWithRetry
            }
            if (!hasPermissions()) {
                requestPermissions { permissionsGranted ->
                    if (!permissionsGranted) {
                        textView.text = "Contacts Permission required"
                    } else {
                        exampleStep2(friends)
                    }
                }
            } else {
                exampleStep2(friends)
            }
        }
    }

    fun exampleStep2(friends: List<Friend>) {
        searchContactsForFriends(friends) { contacts ->
            textView.text = "${contacts.size} of your friends are in your contacts already"
        }
    }

    fun loadFriendsWithRetry(callback: (Exception?, List<Friend>?) -> Unit) {
        loadFriends { err, friends ->
            if (err == null) {
                callback.invoke(null, friends)
            } else {
                showRetryDialog { shouldRetry ->
                    if (shouldRetry) {
                        loadFriendsWithRetry(callback)
                    } else {
                        callback.invoke(err, null)
                    }
                }
            }
        }
    }
}