package com.android.music_player.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.adapters.EqualizerAdapter;
import com.android.music_player.managers.MediaPlayerManager;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.SharedPrefsUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class EqualizerFragment extends DialogFragment implements View.OnClickListener,
        EqualizerAdapter.OnClickItemListener, SeekBar.OnSeekBarChangeListener {
    private Context context;
    private View view;
    private ImageButton mImbBackMusic;
    private SharedPrefsUtils mSharedPrefsUtils;
    private int mCurrentEqProfile = 0;
    private EqualizerAdapter mEqualizerAdapter;
    private ArrayList<String> mEqList;
    private ImageView mImgSelection;
    private RecyclerView mRecycleEqualizer;
    private TextView mTextOptionEqualizer;
    private LinearLayout mLinearEqualizer;
    private boolean isClick = false;
    private String item;
    private Button mBtnFlat;
    private SeekBar mBassBoost, mVirtualizer;
    private SeekBar[] sliders = new SeekBar[Constants.VALUE.MAX_SLIDERS];
    private SwitchCompat mSwEnabled;
    private int num_sliders = 0;
    private int minEQLevel = 0;
    private int maxEQLevel = 100;
    private BottomSheetDialog bottomSheet;
    private BottomSheetBehavior behavior;
    public static EqualizerFragment newInstance() {
        
        Bundle args = new Bundle();

        EqualizerFragment fragment = new EqualizerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.fragment_dialog_equalizer, null);

        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);

        params.width = width;
        params.height = height;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefsUtils = new SharedPrefsUtils(getContext());

        mCurrentEqProfile = mSharedPrefsUtils.getInteger(Constants.PREFERENCES.CURRENT_EQUALIZER_PROFILE
                , 0);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(this.view);
        initData();
        assignView();
    }

    private void initView(View view){
        sliders[0] = view.findViewById(R.id.slider_1);
        sliders[1] = view.findViewById(R.id.slider_2);
        sliders[2] = view.findViewById(R.id.slider_3);
        sliders[3] = view.findViewById(R.id.slider_4);
        sliders[4] = view.findViewById(R.id.slider_5);
        mBassBoost = view.findViewById(R.id.sb_bass_boost);
        mVirtualizer = view.findViewById(R.id.sb_virtualizer);
        mBtnFlat = view.findViewById(R.id.btnFlat);

        mLinearEqualizer = view.findViewById(R.id.ll_main_equalizer);
        mTextOptionEqualizer = view.findViewById(R.id.text_title_equalizer);
        mImgSelection = view.findViewById(R.id.img_selection);
        mRecycleEqualizer = view.findViewById(R.id.rc_Equalizer);
        mImbBackMusic = view.findViewById(R.id.imb_BackMusic);
        mSwEnabled = view.findViewById(R.id.sw_enabled);
    }

    private void initData(){
        mEqList = new ArrayList<>();
        mEqList.add("Profile 1");
        mEqList.add("Profile 2");
        mEqList.add("Profile 3");
        mEqList.add("Profile 4");
        mEqList.add("Profile 5");
    }
    private void assignView(){
        mBtnFlat.setOnClickListener(this);
        mSwEnabled.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedPrefsUtils.setBoolean(Constants.PREFERENCES.TURN_EQUALIZER, isChecked);
                if (MediaPlayerManager.mMediaPlayer == null){
                    Log.d("PPP", "media null");
                    return;
                }else {
                    if (isChecked == true){
                        Log.d("PPP", "isCheck true --- "+MediaPlayerManager.mMediaPlayer.getAudioSessionId());
                        setupEqualizerFxAndUI(true);
                    }else {
                        Log.d("PPP", "isCheck false");
                    }
                   /* if (isChecked == true) {
                        int session = MediaPlayerManager.mMediaPlayer.getAudioSessionId();
                        Equalizer equalizer = new Equalizer(0, session);
                        BassBoost bassBoost = new BassBoost(0, session);
                        Virtualizer virtualizer = new Virtualizer(0, session);

                        equalizer.setEnabled(isChecked);
                        bassBoost.setEnabled(isChecked);
                        virtualizer.setEnabled(isChecked);
                        // saving setting for equalizer
                        if (isChecked) {
                            for (int index = 0; index < Constants.VALUE.MAX_SLIDERS; index++) {
                                equalizer.setBandLevel((short) index,
                                        (short) mSharedPrefsUtils.getInteger("profile" + mCurrentEqProfile + "Band" + index, 0));
                            }
                            bassBoost.setStrength((short) mSharedPrefsUtils.
                                    getInteger("BASS_LEVEL" + mCurrentEqProfile, 0));
                            virtualizer.setStrength((short) mSharedPrefsUtils.
                                    getInteger("VIRTUAL_LEVEL" + mCurrentEqProfile, 0));
                        }
                        equalizer.release();
                        bassBoost.release();
                        virtualizer.release();
                    }*/
                }
            }
        });
        mImbBackMusic.setOnClickListener(this);
        mImgSelection.setOnClickListener(this);
        mEqualizerAdapter = new EqualizerAdapter(getContext(), mEqList);
        mEqualizerAdapter.setOnClickItemListener(this);
        mRecycleEqualizer.setAdapter(mEqualizerAdapter);
        mRecycleEqualizer.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,
                false));
        mBassBoost.setOnSeekBarChangeListener(this);
        mVirtualizer.setOnSeekBarChangeListener(this);


    }
    private void setupEqualizerFxAndUI(boolean isCheck){
        try {
            Equalizer equalizer = new Equalizer(0,
                    mSharedPrefsUtils.getInteger(Constants.PREFERENCES.AUDIO_SESSION_ID, 0));
            equalizer.setEnabled(isCheck);
            num_sliders = (int) equalizer.getNumberOfBands();
            short[] bands  = equalizer.getBandLevelRange();
            minEQLevel = bands [0];
            maxEQLevel = bands [1];
            for (int i = 0; i < num_sliders && i < Constants.VALUE.MAX_SLIDERS; i++) {
                sliders[i].setOnSeekBarChangeListener(this);
                //slider_labels[i].setText(formatBandLabel(freq_range));
            }
            equalizer.release();
        } catch (Exception ignored) {
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnFlat:
                setFlat();
                break;
            case R.id.imb_BackMusic:
                dismiss();
                break;
            case R.id.img_selection:
                if (isClick){
                    // ẩn list
                    Animation fadeOut = AnimationUtils.loadAnimation(getActivity(),
                            R.anim.fadeout);
                    mRecycleEqualizer.setAnimation(fadeOut);
                    mRecycleEqualizer.setVisibility(View.GONE);
                    // hiện thanh tiêu đề
                    mTextOptionEqualizer.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mTextOptionEqualizer.setVisibility(View.VISIBLE);
                            mTextOptionEqualizer.setBackgroundResource(R.color.black);
                        }
                    });
                    // hiện thanh công cụ
                    mLinearEqualizer.setAlpha(1);
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(),
                            R.anim.fadein);
                    mLinearEqualizer.setAnimation(fadeIn);
                    mLinearEqualizer.setVisibility(View.VISIBLE);

                    mImgSelection.setImageResource(R.drawable.ic_up_arrow_white);
                    isClick = false;
                }else {
                    // hiện list
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(),
                            R.anim.fadein);
                    mRecycleEqualizer.setAnimation(fadeIn);
                    mRecycleEqualizer.setVisibility(View.VISIBLE);
                    // ẩn thanh tiêu đề
                    mTextOptionEqualizer.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mTextOptionEqualizer.setVisibility(View.GONE);
                            mTextOptionEqualizer.setBackgroundResource(R.color.black);
                        }
                    });

                    // ẩn thanh công cụ
                    mLinearEqualizer.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mLinearEqualizer.setVisibility(View.GONE);
                        }
                    });

                    mImgSelection.setImageResource(R.drawable.ic_down_arrow_white);
                    isClick = true;
                }
                break;
        }
    }

    @Override
    public void onClickItem(String item, int pos) {
        this.item = item;

        // ẩn list
        Animation fadeOut = AnimationUtils.loadAnimation(getContext(),
                R.anim.fadeout);
        mRecycleEqualizer.setAnimation(fadeOut);
        mRecycleEqualizer.setVisibility(View.GONE);
        // NAME_PLAYLIST list sẽ hiện
        mTextOptionEqualizer.setAlpha(1);
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(),
                R.anim.fadein);
        mTextOptionEqualizer.setAnimation(fadeIn);
        mTextOptionEqualizer.setText(item);
        mTextOptionEqualizer.setBackgroundResource(R.color.whiteOpaqueX4);
        mTextOptionEqualizer.setVisibility(View.VISIBLE);
        // chỉnh equalizer sẽ hiện
        mLinearEqualizer.setAlpha(1);
        mLinearEqualizer.setAnimation(fadeIn);
        mLinearEqualizer.setVisibility(View.VISIBLE);

        mImgSelection.setImageResource(R.drawable.ic_up_arrow_white);
        isClick = false;

        if (mCurrentEqProfile != pos) {
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.CURRENT_EQUALIZER_PROFILE, pos);
            mCurrentEqProfile = pos;
        }
        Log.d("Equalizer", mCurrentEqProfile + "profile");
        updateUI();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Equalizer eq = new Equalizer(0, mSharedPrefsUtils.getInteger(Constants.PREFERENCES.AUDIO_SESSION_ID, 0));
        BassBoost bassBoost = new BassBoost(0, mSharedPrefsUtils.getInteger(Constants.PREFERENCES.AUDIO_SESSION_ID, 0));
        Virtualizer virtualizer = new Virtualizer(0, mSharedPrefsUtils.getInteger(Constants.PREFERENCES.AUDIO_SESSION_ID, 0));
        if (seekBar == mBassBoost){
            if (eq.getEnabled()){
                bassBoost.setEnabled(progress > 0);
                bassBoost.setStrength((short) progress);// Already in the right range 0-1000
            }
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.BASS_LEVEL + mCurrentEqProfile, progress);
        }else if (seekBar == mVirtualizer){
            if (eq.getEnabled()){
                virtualizer.setEnabled(progress > 0);
                virtualizer.setStrength((short) progress);// Already in the right range 0-1000
            }
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.VIRTUAL_LEVEL + mCurrentEqProfile, progress);
        }else {
            int new_progress = minEQLevel + (maxEQLevel - minEQLevel ) * progress / 100;
            for (int i = 0; i < num_sliders; i++) {
                if (sliders[i] == seekBar) {
                    if (eq.getEnabled()) {
                        eq.setBandLevel((short) i, (short) new_progress);
                    }
                    mSharedPrefsUtils.setInteger("profile" + mCurrentEqProfile + "Band" + i, new_progress);
                    break;
                }
            }
        }
        eq.release();
        bassBoost.release();
        virtualizer.release();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void updateSliders() {
        try {
            for (int i = 0; i < num_sliders; i++) {
                int level =
                        (short) mSharedPrefsUtils.getInteger("profile" + mCurrentEqProfile + "Band" + i,
                                0);
                int pos = 100 * level / (maxEQLevel - minEQLevel) + 50;
                sliders[i].setProgress(pos);
            }
        } catch (Exception ignored) {
        }
    }

    public void updateBassBoost() {
        try {
            mBassBoost.setProgress((short)
                    mSharedPrefsUtils.getInteger(Constants.PREFERENCES.BASS_LEVEL + mCurrentEqProfile,
                            0));
        } catch (Exception ignored) {
        }
    }

    public void updateVirtualizer() {
        try {
            mVirtualizer.setProgress((short)
                    mSharedPrefsUtils.getInteger(Constants.PREFERENCES.VIRTUAL_LEVEL + mCurrentEqProfile, 0));
        } catch (Exception ignored) {
        }
    }


    public void updateUI() {
        updateSliders();
        updateBassBoost();
        updateVirtualizer();
        mSwEnabled.setChecked(mSharedPrefsUtils.getBoolean(Constants.PREFERENCES.TURN_EQUALIZER, false));
    }

    public void setFlat() {
        try {
            Equalizer eq = new Equalizer(0,
                    mSharedPrefsUtils.getInteger(Constants.PREFERENCES.AUDIO_SESSION_ID, 0));
            BassBoost bassBoost = new BassBoost(0,
                    mSharedPrefsUtils.getInteger(Constants.PREFERENCES.AUDIO_SESSION_ID, 0));
            Virtualizer virtualizer = new Virtualizer(0,
                    mSharedPrefsUtils.getInteger(Constants.PREFERENCES.AUDIO_SESSION_ID, 0));
            bassBoost.setEnabled(false);
            bassBoost.setStrength((short) 0);
            virtualizer.setEnabled(false);
            virtualizer.setStrength((short) 0);
            for (int i = 0; i < num_sliders; i++) {
                eq.setBandLevel((short) i, (short) 0);
                mSharedPrefsUtils.setInteger("profile" + mCurrentEqProfile + "Band" + i, 0);
            }
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.BASS_LEVEL + mCurrentEqProfile, 0);
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.VIRTUAL_LEVEL + mCurrentEqProfile, 0);
            updateUI();
            eq.release();
            bassBoost.release();
            virtualizer.release();
        } catch (Exception ignored) {
        }
    }
}
