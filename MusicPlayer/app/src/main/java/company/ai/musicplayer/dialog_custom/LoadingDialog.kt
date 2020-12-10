package company.ai.musicplayer.dialog_custom


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import company.ai.musicplayer.databinding.DialogLoadingBinding

class LoadingDialog : DialogFragment() {
    private lateinit var mView: View
    private lateinit var mContext: Context
    private lateinit var binding: DialogLoadingBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogLoadingBinding.inflate(layoutInflater)
        dialog!!.apply {
            requestWindowFeature(STYLE_NO_TITLE);
            isCancelable = false
        }

        return binding.root
    }
}