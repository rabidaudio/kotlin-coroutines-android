package audio.rabid.talks.kotlinasync.helpers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import rx.subjects.PublishSubject

/**
 * Created by cjk on 9/17/17.
 */
abstract class BaseActivity : AppCompatActivity(), ActivityResultMixin {

    override val _activityResultStream: PublishSubject<ActivityResultMixin.ActivityResult> = PublishSubject.create()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<AppCompatActivity>.onActivityResult(requestCode, resultCode, data)
        super<ActivityResultMixin>.onActivityResult(requestCode, resultCode, data)
    }
}