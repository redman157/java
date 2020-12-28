package company.ai.musicplayer.ui

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import company.ai.musicplayer.R
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.databinding.FragmentSettingBinding
import company.ai.musicplayer.extensions.handleViewVisibility

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    private lateinit var mBinding: FragmentSettingBinding
    private lateinit var mUIControlInterface: UIControlInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            object : CountDownTimer(1000,1000){
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    (activity as HomeActivity).mLayoutMain.home.handleViewVisibility(true)
                    (activity as HomeActivity).setShowProgress(false)
                }

            }.start()

            mUIControlInterface = activity as UIControlInterface
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentSettingBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        mBinding.toolbar.setNavigationOnClickListener {
            mUIControlInterface.onCloseActivity(this@SettingsFragment)
        }
        childFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_layout, PreferencesFragment.newInstance())
            commit()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}