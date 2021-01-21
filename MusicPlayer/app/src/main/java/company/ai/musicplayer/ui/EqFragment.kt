package company.ai.musicplayer.ui

import android.animation.Animator
import android.content.Context
import android.content.res.ColorStateList
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.slider.Slider
import company.ai.musicplayer.PresetsViewHolder
import company.ai.musicplayer.R
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.databinding.FragmentEqBinding
import company.ai.musicplayer.extensions.afterMeasured
import company.ai.musicplayer.extensions.createCircularReveal
import company.ai.musicplayer.extensions.decodeColor
import company.ai.musicplayer.extensions.toToast
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.utils.ThemeHelper

/**
 * A simple [Fragment] subclass.
 * Use the [EqFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EqFragment : Fragment() {
    private lateinit var mBinding: FragmentEqBinding
    private lateinit var mEqualizer: Triple<Equalizer, BassBoost, Virtualizer>
    private lateinit var mUIControlInterface: UIControlInterface

    private lateinit var mEqAnimator: Animator

    private var sLaunchCircleReveal = true

    private val mPresetsList = mutableListOf<String>()

    private val mDataSource = emptyDataSource()

    private var mSelectedPreset = 0

    private val mSliders: Array<Slider?> = arrayOfNulls(5)
    private val mSlidersLabels: Array<TextView?> = arrayOfNulls(5)

    private val mRoundedTextBackGround by lazy {
        val shapeAppearanceModel = ShapeAppearanceModel()
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, resources.getDimension(R.dimen.md_corner_radius))
            .build()
        MaterialShapeDrawable(shapeAppearanceModel).apply {
            strokeColor = ColorStateList.valueOf(ThemeHelper.resolveThemeAccent(requireActivity()))
            strokeWidth = 0.50F
            fillColor = ColorStateList.valueOf(R.color.windowBackground.decodeColor(requireActivity()))
        }
    }
    override fun onAttach(context: Context){
        super.onAttach(context)
        mUIControlInterface = activity as UIControlInterface
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!::mEqualizer.isInitialized){
            mEqualizer = mUIControlInterface.onGetEqualizer()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentEqBinding.inflate(layoutInflater, container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view,savedInstanceState)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun assignView(){
        mBinding.sliderBass.addOnChangeListener { slider, value, fromUser ->
            if (fromUser){
                mEqualizer.second.setStrength(value.toInt().toShort())
            }
        }
        mBinding.sliderVirt.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed
            if (fromUser) {
                mEqualizer.third.setStrength(value.toInt().toShort())
            }
        }
        val equalizer: Equalizer = mEqualizer.first
        for (i in 0 until equalizer.numberOfPresets){
            mPresetsList.add(equalizer.getPresetName(i.toShort()))
        }
        mDataSource.set(mPresetsList)

        finishSetupEqualizer(mBinding.root)
    }

    private fun finishSetupEqualizer(view: View){
        mBinding.apply {
            mSliders[0] = slider0
            mSlidersLabels[0] = freq0
            mSliders[1] = slider1
            mSlidersLabels[1] = freq1
            mSliders[2] = slider2
            mSlidersLabels[2] = freq2
            mSliders[3] = slider3
            mSlidersLabels[3] = freq3
            mSliders[4] = slider4
            mSlidersLabels[4] = freq4
        }
        mPreferences.savedEqualizerSettings?.let { savedEqualizerSettings ->
            mSelectedPreset = savedEqualizerSettings.preset
        }

        mEqualizer.first.apply {
            val bandLevelRange = bandLevelRange
            val minBandLevel = bandLevelRange[0]
            val maxBandLevel = bandLevelRange[1]
            mSliders.iterator().withIndex().forEach { slider ->
                slider.value?.valueFrom = minBandLevel.toFloat()
                slider.value?.valueTo = maxBandLevel.toFloat()

                slider.value?.addOnChangeListener { selectedSlider, value, fromUser ->
                    if (fromUser) {
                        if (mSliders[slider.index] == selectedSlider) {
                            mEqualizer.first.setBandLevel(slider.index.toShort(), value.toInt().toShort())
                        }
                    }
                }
                mSlidersLabels[slider.index]?.apply {
                    text = formatMilliHzToK(getCenterFreq(slider.index.toShort()))
                    background = mRoundedTextBackGround
                }
            }
            mBinding.presets.apply {
                setup {
                    withDataSource(mDataSource)
                    withItem<String, PresetsViewHolder>(R.layout.item_eq_preset) {
                        onBind(::PresetsViewHolder) { index, item ->
                            presetTitle.text = item
                            val textColor = if (mSelectedPreset == index) {
                                ThemeHelper.resolveThemeAccent(context)
                            } else {
                                ThemeHelper.resolveColorAttr(requireActivity(), android.R.attr.textColorPrimary)
                            }
                            presetTitle.setTextColor(textColor)
                        }

                        onClick { index ->
                            adapter?.notifyItemChanged(mSelectedPreset)
                            mSelectedPreset = index
                            adapter?.notifyItemChanged(mSelectedPreset)
                            mEqualizer.first.usePreset(mSelectedPreset.toShort())
                            updateBandLevels(true)
                        }
                    }
                }
                scrollToPosition(mSelectedPreset)
            }
            updateBandLevels(false)

            setupToolbar()

            if (sLaunchCircleReveal) {
                view.afterMeasured {
                    mEqAnimator =
                        mBinding.root.createCircularReveal(
                            isErrorFragment = false,
                            show = true
                        )
                }
            }
        }
    }

    private fun setupToolbar(){
        mBinding.toolbar.setNavigationOnClickListener {
            mUIControlInterface.onCloseActivity(this@EqFragment)
        }
    }

    private fun updateBandLevels(isPresetChanged: Boolean){
        try {
            mSliders.iterator().withIndex().forEach { slider ->
                slider.value?.value = mEqualizer.first.getBandLevel(slider.index.toShort()).toFloat()
            }
            if (!isPresetChanged) {
                mPreferences.savedEqualizerSettings?.let { eqSettings ->
                    mBinding.sliderBass.value = eqSettings.bassBoost.toFloat()
                }
                mBinding.sliderVirt.value = mEqualizer.third.roundedStrength.toFloat()
            }
        } catch (e: UnsupportedOperationException) {
            e.printStackTrace()
            getString(R.string.error_eq).toToast(requireActivity())
        }
    }

    private fun formatMilliHzToK(milliHz: Int): String? {
        return if (milliHz < 1000000) {
            (milliHz / 1000).toString()
        } else {
            getString(R.string.freq_k, milliHz / 1000000)
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EqFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = EqFragment()
    }
}