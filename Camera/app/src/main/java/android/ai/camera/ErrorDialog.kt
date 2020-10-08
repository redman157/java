package android.ai.camera

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * Shows an error message dialog.
 */
class ErrorDialog : DialogFragment() {
    private lateinit var mContext : Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(activity)
            .setMessage(arguments!!.getString(ARG_MESSAGE))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                (mContext as Activity).finish()
            }
            .create()

    companion object {
        private const val ARG_MESSAGE = "message"
        fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
            arguments = Bundle().apply {
                putString(ARG_MESSAGE, message)
            }
        }
    }

}