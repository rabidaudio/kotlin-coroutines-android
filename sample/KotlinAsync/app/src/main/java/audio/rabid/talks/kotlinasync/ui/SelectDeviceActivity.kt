package audio.rabid.talks.kotlinasync.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import audio.rabid.talks.kotlinasync.backend.BluetoothDevice
import audio.rabid.talks.kotlinasync.R

class SelectDeviceActivity : AppCompatActivity() {

    companion object {

        fun getLaunchIntent(context: Context, devices: List<BluetoothDevice>)
                = Intent(context, SelectDeviceActivity::class.java).apply {
                        putExtra("EXTRA_DEVICES", ArrayList(devices))
                }
    }

    private val listView by lazy { findViewById(R.id.listView) as ListView }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_device)

        @Suppress("UNCHECKED_CAST")
        val devices = intent.getSerializableExtra("EXTRA_DEVICES") as ArrayList<BluetoothDevice>

        val adapter = DeviceAdapter(this, devices)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val data = Intent().putExtra("EXTRA_SELECTED_DEVICE", adapter.getItem(position))
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    private class DeviceAdapter(context: Context, devices: List<BluetoothDevice>)
        : ArrayAdapter<BluetoothDevice>(context, android.R.layout.simple_list_item_2, devices) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return (convertView ?: parent.inflate()).apply {
                (findViewById(android.R.id.text1) as TextView).text = getItem(position).name
                (findViewById(android.R.id.text2) as TextView).text = getItem(position).address
            }
        }

        private fun ViewGroup?.inflate(): View
                = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, this, false)
    }
}
