package audio.rabid.talks.kotlinasync

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}
