package audio.rabid.talks.kotlinasync.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import audio.rabid.talks.kotlinasync.api.DiagnosticTroubleCode

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
        return ListView(inflater.context).apply {
            @Suppress("UNCHECKED_CAST")
            val codes = arguments.getSerializable("EXTRA_CODES") as ArrayList<DiagnosticTroubleCode>
            adapter = CodesAdapter(context, codes)
        }
    }

    private class CodesAdapter(context: Context, items: List<DiagnosticTroubleCode>)
        : ArrayAdapter<DiagnosticTroubleCode>(context, android.R.layout.simple_list_item_2, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return super.getView(position, convertView, parent).apply {
                (findViewById(android.R.id.text1) as TextView).text = getItem(position).code
                (findViewById(android.R.id.text2) as TextView).text = getItem(position).name
            }
        }
    }
}