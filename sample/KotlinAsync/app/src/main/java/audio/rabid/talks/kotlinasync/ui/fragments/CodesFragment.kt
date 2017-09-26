package audio.rabid.talks.kotlinasync.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import audio.rabid.talks.kotlinasync.R
import audio.rabid.talks.kotlinasync.api.DiagnosticTroubleCode
import audio.rabid.talks.kotlinasync.ui.MainActivity

/**
 * Created by cjk on 9/17/17.
 */
class CodesFragment : Fragment() {

    companion object {
        fun create(codes: List<DiagnosticTroubleCode>) = CodesFragment().apply {
            arguments = Bundle().apply {
                putSerializable("EXTRA_CODES", ArrayList(codes))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_codes, container, false).apply {
            @Suppress("UNCHECKED_CAST")
            val codes = arguments.getSerializable("EXTRA_CODES") as ArrayList<DiagnosticTroubleCode>
            (findViewById(R.id.listView) as ListView).adapter = CodesAdapter(context, codes)
            (findViewById(R.id.refresh) as Button).setOnClickListener {
                (activity as MainActivity).restartJob()
            }
        }
    }

    private class CodesAdapter(context: Context, items: List<DiagnosticTroubleCode>)
        : ArrayAdapter<DiagnosticTroubleCode>(context, android.R.layout.simple_list_item_2, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return (convertView ?: parent.inflate()).apply {
                (findViewById(android.R.id.text1) as TextView).text = getItem(position).code
                (findViewById(android.R.id.text2) as TextView).text = getItem(position).name
            }
        }

        private fun ViewGroup?.inflate(): View
                = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, this, false)
    }
}