package audio.rabid.debug.examples.simple

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.widget.TextView
import java.util.concurrent.Semaphore

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
    val semaphore: Semaphore

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
                        semaphore.acquire()
                        showRetryDialog()
                    })
                    try {
                        // wait for main to acquire the lock
                        while (semaphore.availablePermits() > 0) {
                            Thread.sleep(10)
                        }
                        // wait for main to finish with the lock
                        semaphore.acquire()
                        semaphore.release()
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
                    semaphore.acquire()
                    requestPermissions()
                })
                try {
                    // wait for main to acquire the lock
                    while (semaphore.availablePermits() > 0) {
                        Thread.sleep(10)
                    }
                    // wait for main to finish with the lock
                    semaphore.acquire()
                    semaphore.release()
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
                    semaphore.release()
                }
                .setNegativeButton("no") { _, _ ->
                    dialogResult = false
                    semaphore.release()
                }
                .create().show()
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1 && permissions[0] == Manifest.permission.READ_CONTACTS) {
            val granted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (granted) {
                permissionResult = true
                semaphore.release()
            } else {
                permissionResult = false
                semaphore.release()
            }
        }
    }
}