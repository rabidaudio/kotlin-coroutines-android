package audio.rabid.debug.examples.simple

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

/**
 * Created by cjk on 3/20/18.
 */
abstract class BasicActivityExample : Activity() {

    abstract fun loadFriends(): Call<List<Friend>>
    abstract fun searchContactsForFriends(friends: List<Friend>): List<Contact>

    abstract val textView: TextView

    lateinit var friends: List<Friend>

    override fun onStart() {
        super.onStart()
        start()
    }

    fun start() {
        textView.text = "Loading"
        loadFriends().enqueue(object : Callback<List<Friend>> {
            override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                showRetryDialog()
            }

            override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                friends = response.body()
                checkPermissions()
            }
        })
    }

    fun showRetryDialog() {
        AlertDialog.Builder(this)
                .setMessage("Do you want to try again?")
                .setPositiveButton("yes") { _, _ -> start() }
                .setNegativeButton("no") { _, _ -> textView.text = "Network Unavailable" }
                .create().show()
    }

    @SuppressLint("NewApi")
    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasPermissions = checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
            if (hasPermissions) {
                searchContacts()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), 1)
            }
        } else {
            searchContacts()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && permissions[0] == Manifest.permission.READ_CONTACTS) {
            val granted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (granted) {
                searchContacts()
            } else {
                textView.text = "Contacts Permission required"
            }
        }
    }

    fun searchContacts() {
        Executors.newSingleThreadExecutor().execute {
            val contacts = searchContactsForFriends(friends)
            runOnUiThread {
                textView.text = "${contacts.size} of your friends are in your contacts already"
            }
        }
    }
}