package audio.rabid.talks.kotlinasync.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import audio.rabid.talks.kotlinasync.R

/**
 * Created by cjk on 9/17/17.
 */
class InProgressFragment : Fragment() {

    companion object {
        fun create(message: String) = InProgressFragment().apply {
            arguments = Bundle().apply {
                putString("EXTRA_MESSAGE", message)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_in_progress, container, false).apply {
            (findViewById(R.id.message) as TextView).text = arguments.getString("EXTRA_MESSAGE")
        }
    }
}