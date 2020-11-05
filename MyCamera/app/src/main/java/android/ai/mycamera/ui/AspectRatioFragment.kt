package android.ai.mycamera.ui

import android.ai.mycamera.caculator.AspectRatio
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [AspectRatioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AspectRatioFragment : DialogFragment() {
    companion object{
        private const val ARG_ASPECT_RATIOS = "aspect_ratios"
        private const val ARG_CURRENT_ASPECT_RATIO = "current_aspect_ratio"
        fun newInstance(
            ratios: Set<AspectRatio?>,
            currentRatio: AspectRatio
        ): AspectRatioFragment {
            val fragment = AspectRatioFragment()
            val args = Bundle()
            val ratio = ArrayList(ratios)
            for (i in ratio){
                Log.d("QQQ","ratios: $i")
            }
            args.putParcelableArray(
                ARG_ASPECT_RATIOS,
                ratios.toTypedArray()
            )
            args.putParcelable(ARG_CURRENT_ASPECT_RATIO, currentRatio)
            fragment.arguments = args
            return fragment
        }
    }

    interface AspectRatioListener {
        fun onAspectRatioSelected(ratio: AspectRatio)
    }

    private var mListener: AspectRatioListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as AspectRatioListener?
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = arguments
        val ratios = args!!.getParcelableArray(ARG_ASPECT_RATIOS) as Array<AspectRatio>?
            ?: throw RuntimeException("No ratios")
        Arrays.sort(ratios)
        val current: AspectRatio = args.getParcelable(ARG_CURRENT_ASPECT_RATIO)!!
        val adapter = AspectRatioAdapter(ratios, current)
        return AlertDialog.Builder(activity)
            .setAdapter(adapter,
                DialogInterface.OnClickListener { _, position ->
                    mListener!!.onAspectRatioSelected(
                        ratios[position]
                    )
                })
            .create()
    }

    private class AspectRatioAdapter internal constructor(
        private val mRatios: Array<AspectRatio>,
        private val mCurrentRatio: AspectRatio?
    ) :
        BaseAdapter() {
        override fun getCount(): Int {
            return mRatios.size
        }

        override fun getItem(position: Int): AspectRatio {
            return mRatios[position]
        }

        override fun getItemId(position: Int): Long {
            return getItem(position).hashCode().toLong()
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup): View {
            var view = view
            val holder: ViewHolder
            if (view == null) {
                view = LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
                holder = ViewHolder()
                holder.text = view.findViewById<View>(android.R.id.text1) as TextView
                view.tag = holder
            } else {
                holder = view.tag as ViewHolder
            }
            val ratio = getItem(position)
            val sb = StringBuilder(ratio.toString())
            if (ratio == mCurrentRatio) {
                sb.append(" *")
            }
            holder.text!!.text = sb
            return view!!
        }

        private class ViewHolder {
            var text: TextView? = null
        }
    }
}