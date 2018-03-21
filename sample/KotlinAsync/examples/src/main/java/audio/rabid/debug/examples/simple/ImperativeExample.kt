package audio.rabid.debug.examples.simple

import android.widget.TextView
import audio.rabid.debug.examples.simple.models.Contact
import audio.rabid.debug.examples.simple.models.Friend

/**
 * Created by cjk on 9/21/17.
 *
 * This is the direct implementation of the flow. However,
 * we can't do this because we'd be blocking the main thread.
 */
interface ImperativeExample {

    fun loadFriends(): List<Friend>
    fun searchContactsForFriends(friends: List<Friend>): List<Contact>

    val textView: TextView

    fun showRetryDialog(): Boolean
    fun hasPermissions(): Boolean
    fun requestPermissions(): Boolean

    fun example() {
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