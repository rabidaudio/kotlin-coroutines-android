package audio.rabid.talks.kotlinasync.helpers

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import rx.subjects.PublishSubject

/**
 * Created by cjk on 9/18/17.
 */
class BaseFragment : Fragment(),  ActivityResultMixin, JobManagerMixin {

    override val _activityResultStream: PublishSubject<ActivityResultMixin.ActivityResult> = PublishSubject.create()
    override val _jobs = mutableMapOf<String, JobManagerMixin.ManagedJob>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<Fragment>.onActivityResult(requestCode, resultCode, data)
        super<ActivityResultMixin>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        super<JobManagerMixin>.onStop()
        super<Fragment>.onPause()
    }

    override fun onStop() {
        super<JobManagerMixin>.onStop()
        super<Fragment>.onStop()
    }

    override fun onDestroy() {
        super<ActivityResultMixin>.onDestroy()
        super<JobManagerMixin>.onDestroy()
        super<Fragment>.onDestroy()
    }
}