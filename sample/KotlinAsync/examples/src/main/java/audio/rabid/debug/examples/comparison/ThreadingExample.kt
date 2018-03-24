package audio.rabid.debug.examples.comparison

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.widget.TextView
import audio.rabid.debug.examples.comparison.models.Contact
import audio.rabid.debug.examples.comparison.models.Friend

/**
 * Created by cjk on 3/19/18.
 *
 * An example using a fresh background thread to run our imperative code. We can call blocking IO
 * like network requests here
 */
interface ThreadingExample {

    fun loadFriends(): List<Friend>
    fun searchContactsForFriends(friends: List<Friend>): List<Contact>

    val textView: TextView

    fun hasPermissions(): Boolean
    fun requestPermissions()

    fun runOnUiThread(runnable: Runnable)

    var dialogResult: Boolean
    var permissionResult: Boolean
    val lock: Object

    fun example() {
        Thread(Runnable {
            runOnUiThread(Runnable {
                textView.text = "Loading"
            })
            var friends: List<Friend>? = null
            do {
                try {
                    friends = loadFriends()
                } catch (e: Exception) {
                    runOnUiThread(Runnable {
                        showRetryDialog()
                    })
                    try {
                        // wait for main to finish with the lock
                        lock.wait()
                    }catch (e: InterruptedException) {
                        return@Runnable
                    }
                    if (!dialogResult) {
                        runOnUiThread(Runnable {
                            textView.text = "Network Unavailable"
                        })
                        return@Runnable
                    }
                }
            } while (friends == null)

            if (!hasPermissions()) {
                runOnUiThread(Runnable {
                    requestPermissions()
                })
                try {
                    // wait for main to finish with the lock
                    lock.wait()
                }catch (e: InterruptedException) {
                    return@Runnable
                }
                if (!permissionResult) {
                    runOnUiThread(Runnable {
                        textView.text = "Contacts Permission required"
                    })
                    return@Runnable
                }
            }

            val contacts = searchContactsForFriends(friends)
            runOnUiThread(Runnable {
                textView.text = "${contacts.size} of your friends are in your contacts already"
            })
        }).start()
    }

    fun showRetryDialog() {
        AlertDialog.Builder(this as Context)
                .setMessage("Do you want to try again?")
                .setPositiveButton("yes") { _, _ ->
                    dialogResult = true
                    lock.notifyAll()
                }
                .setNegativeButton("no") { _, _ ->
                    dialogResult = false
                    lock.notifyAll()
                }
                .create().show()
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1 && permissions[0] == Manifest.permission.READ_CONTACTS) {
            val granted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (granted) {
                permissionResult = true
                lock.notifyAll()
            } else {
                permissionResult = false
                lock.notifyAll()
            }
        }
    }
}