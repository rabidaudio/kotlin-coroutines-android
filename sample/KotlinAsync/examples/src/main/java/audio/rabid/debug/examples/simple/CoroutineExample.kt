package audio.rabid.debug.examples.simple

import android.widget.TextView
import audio.rabid.debug.examples.simple.models.Contact
import audio.rabid.debug.examples.simple.models.Friend

/**
 * Created by cjk on 9/21/17.
 *
 * This is the flow using coroutines. Note that it is
 * identical to [ImperativeExample] except for the suspend keywords.
 */
interface CoroutineExample {

    suspend fun loadFriends(): List<Friend>
    suspend fun searchContactsForFriends(friends: List<Friend>): List<Contact>

    val textView: TextView

    suspend fun showRetryDialog(): Boolean
    fun hasPermissions(): Boolean
    suspend fun requestPermissions(): Boolean

    suspend fun example() {
        textView.text = "Loading"
        var friends: List<Friend>? = null
        do {
            try {
                friends = loadFriends()
            } catch (e: Exception) {
                val shouldRetry = showRetryDialog()
                if (!shouldRetry) {
                    textView.text = "Network Unavailable"
                    return
                }
            }
        } while (friends == null)
        if (!hasPermissions()) {
            val grantedPermissions = requestPermissions()
            if (!grantedPermissions) {
                textView.text = "Contacts Permission required"
                return
            }
        }
        val contacts = searchContactsForFriends(friends)
        textView.text = "${contacts.size} of your friends are in your contacts already"
    }
}